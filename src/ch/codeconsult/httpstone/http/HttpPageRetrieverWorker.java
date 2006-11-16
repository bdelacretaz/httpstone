package ch.codeconsult.httpstone.http;

import ch.codeconsult.httpstone.infrastructure.Worker;
import ch.codeconsult.httpstone.url.ConstantUrlGenerator;
import ch.codeconsult.httpstone.url.UrlGenerator;
import ch.codeconsult.httpstone.util.PropertiesReader;
import ch.codeconsult.httpstone.util.TimeStats;
import ch.codeconsult.httpstone.reports.PageErrorReport;

import java.awt.Color;
import java.net.URL;
import java.net.URLConnection;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/** A Worker which repeatedly retrieves an HTTP page, and stores it if
 *  it needs more than a specified time to retrieve it.
 *  
 *  The idea is that the page will contain debugging info that indicates
 *  what caused it to be slow (assuming you have control on the software
 *  which generates the page), so storing it helps find problems.
 *
 *  @author bdelacretaz@codeconsult.ch
 *  $Id$
 */
public class HttpPageRetrieverWorker extends Worker {
    private static final String NONE="NONE";
    private UrlGenerator urlGenerator;
    private long maxRetrievalTime;
    private final TimeStats stats = new TimeStats();
    private URL currentUrl;
    private String lastRetrievedContent;
    private int totalRequests;
    private int lateRequests;
    private int failedRequests;
    private String randomUrlSuffix;
    private File reportsDir;

    /** configure this worker */
    public void configure(PropertiesReader p, String prefix) throws Exception {
        super.configure(p, prefix);
        infoText = p.getRequiredString(prefix + "info");
        maxRetrievalTime = p.getRequiredInt(prefix + "max.retrieval.msec");
        randomUrlSuffix = p.getRequiredString(prefix + "random.url.suffix");
        reportsDir = new File(p.getRequiredString("error.reports.directory"));

        infoText+= " (max " + maxRetrievalTime + " msec)";
        
        final String urlClassProp = prefix + "urlGenerator.class";
        if(p.hasProperty(urlClassProp)) {
          final String clazz = p.getRequiredString(urlClassProp);
          urlGenerator = (UrlGenerator)(Class.forName(clazz)).newInstance();
        } else {
          urlGenerator = new ConstantUrlGenerator(new URL(p.getRequiredString(prefix + "url")));
        }
    }

    /** implement to do one iteration of our work */
    protected void doOneIteration() throws Exception {
        totalRequests++;
        final long urlRetrievalTime = getUrlContent();
        stats.addDataPoint(urlRetrievalTime);

        if(urlRetrievalTime > maxRetrievalTime) {
            lateRequests++;
            storeProblemInfo();
            statusColor = Color.YELLOW;
        } else {
            statusColor = Color.GREEN;
        }
        updateStatus();
    }

    /** update our status */
    private void updateStatus() {
        String status = "<html>";
        status += stats.toString() + "<font size=-2> (min/max/avg msec)</font>";
        status += "<br>";
        final int ok = totalRequests - lateRequests - failedRequests;
        status += totalRequests + "/" + ok + "/" + lateRequests + "/" + failedRequests;
        status += "<font size=-2> (total requests/ok/late/failure)";
        status += "</font>";
        status += "</html>";
        statusText = status;
    }

    /** for now, report exceptions to stderr only */
    protected void reportException(Exception e) {
        super.reportException(e);
        failedRequests++;
        statusColor = Color.RED;
        updateStatus();
    }

    /** called when a page needed more than the specified time to be retrieved */
    private void storeProblemInfo() throws IOException {
        final Map metadata = new HashMap();
        metadata.put("current.url",currentUrl);
        metadata.put("retrieval.stats",stats);

        final int ok = totalRequests - lateRequests - failedRequests;
        metadata.put("total.requests",String.valueOf(totalRequests));
        metadata.put("ok.requests",String.valueOf(ok));
        metadata.put("late.requests",String.valueOf(lateRequests));
        metadata.put("failed.requests",String.valueOf(failedRequests));

        final PageErrorReport pr = new PageErrorReport(reportsDir,getWorkerId(),metadata,lastRetrievedContent);
        System.err.println("Worker " + getWorkerId() + " - error report written to " + pr.getReportFile().getAbsolutePath());
    }

    /** retrieve the content of our URL, return the time in msec */
    private long getUrlContent() throws Exception {
        // maybe use random suffix for the URL
        String suffix = "";
        if(!NONE.equals(randomUrlSuffix)) {
            suffix = randomUrlSuffix + Math.random();
        }

        currentUrl = new URL(urlGenerator.getUrl() + suffix);
        lastRetrievedContent = "NO CONTENT FOR URL " + currentUrl;
        actionText = "Retrieving URL...";
        statusChanged();
        final long start = System.currentTimeMillis();
        final URLConnection c = currentUrl.openConnection();
        readUrlConnectionData(c);
        final long delta = System.currentTimeMillis() - start;
        actionText = "Done retrieving URL content";
        statusChanged();
        return delta;
    }

    /** read the contents of given URLconnection, which must be connected */
    private void readUrlConnectionData(URLConnection conn) throws IOException {
        final InputStream in = new BufferedInputStream(conn.getInputStream());
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final int BUFFER_SIZE = 16384;
        byte buffer[] = new byte[BUFFER_SIZE];
        while(true) {
            int n = in.read(buffer);
            if (n == -1) break;
            bos.write(buffer, 0, n);
        }
        bos.flush();
        lastRetrievedContent = bos.toString();
    }
}

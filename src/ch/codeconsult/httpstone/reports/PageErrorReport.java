package ch.codeconsult.httpstone.reports;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.Map;
import java.util.Date;
import java.util.Iterator;
import java.text.SimpleDateFormat;

/** Create an error report when a page was not retrieved in the
 *  specified time.
 *
 *  @author bdelacretaz@codeconsult.ch
 *  $Id$
 */
public class PageErrorReport {
    private final File reportFile;

    /** create a report under specified directory */
    public PageErrorReport(File baseReportDir,int workerId,Map reportMetadata,String pageContent) throws IOException {

        // store the report in a chronological subdirectory
        final Date now = new Date();
        final String subdir = new SimpleDateFormat("yyyy/MM/dd").format(now) + "/worker-" + workerId;
        final String filename = "worker-" + workerId + (new SimpleDateFormat("-yyyy-MM-DD-HH-mm-ss").format(now)) + ".txt";

        final File reportDir = new File(baseReportDir,subdir);
        reportDir.mkdirs();
        reportFile = new File(reportDir,filename);

        // create report
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(reportFile));
            writeReport(pw,reportFile,now,reportMetadata,pageContent);
        } finally {
            if(pw!=null) pw.close();
        }
    }

    /** File to which report was written */
    public File getReportFile() {
        return reportFile;
    }

    /** write report to disk */
    private void writeReport(PrintWriter pw,File f,Date d,Map reportMetadata,String pageContent) throws IOException {
        pw.println("HttpStone Page Failure Report");
        pw.println("Date: " + d);
        pw.println("Filename: " + f.getAbsolutePath());

        pw.println("--REPORT METADATA--");
        for(Iterator it=reportMetadata.entrySet().iterator(); it.hasNext(); ) {
            final Map.Entry e = (Map.Entry)it.next();
            pw.println(e.getKey() + ": " + e.getValue());
        }

        pw.println("--PAGE CONTENT--");
        pw.println(pageContent);

        pw.println("--END OF REPORT--");
    }
}

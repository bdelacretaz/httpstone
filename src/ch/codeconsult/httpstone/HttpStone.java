package ch.codeconsult.httpstone;

import ch.codeconsult.httpstone.swing.HttpStoneGui;
import ch.codeconsult.httpstone.infrastructure.Worker;
import ch.codeconsult.httpstone.infrastructure.WorkerFactory;
import ch.codeconsult.httpstone.http.HttpPageRetrieverWorker;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.Iterator;

/** Start the HttpStone web monitoring application
 *  @author bdelacretaz@codeconsult.ch
 *  $Id$
 */

public class HttpStone {
    private static final String VERSION = "0.5";

    public static String getVersion() {
        return VERSION;
    }

    private HttpStone(String args[]) throws Exception {
        startGui(args);
    }

    /** start with a graphical interface */
    private void startGui(String args[]) throws Exception {
        if(args.length < 1) {
            throw new Exception("Usage: HttpStone <config file>");
        }

        final String propFile = args[0];
        System.err.println("Loading configuration file " + propFile + "...");
        FileInputStream fis = null;
        final Properties p = new Properties();
        try {
            fis = new FileInputStream(propFile);
            p.load(fis);
        } finally {
            if(fis!=null) fis.close();
        }

        System.err.println("Creating workers...");
        final WorkerFactory wf = new WorkerFactory(p);

        System.err.println("Creating GUI...");
        final String helpInfo = "See file " + propFile + " for configuration information";
        final HttpStoneGui gui = new HttpStoneGui("HttpStone " + VERSION,helpInfo);
        int count=0;
        for(Iterator it = wf.getWorkersIterator(); it.hasNext(); ) {
            gui.addWorker((Worker)it.next());
            count++;
        }
        if(count==0) throw new Exception("No workers are defined in configuration " + propFile + "??");
        System.err.println(count + " Workers configured");

        System.err.println("Starting workers...");
        for(Iterator it = wf.getWorkersIterator(); it.hasNext(); ) {
            ((Worker)it.next()).start();
        }

        System.err.println("GUI created, workers started. Have fun.");
    }

    public static void main(String[] args) throws Exception {
        System.err.println("HttpStone " + VERSION + " starting...");
        new HttpStone(args);
    }
}

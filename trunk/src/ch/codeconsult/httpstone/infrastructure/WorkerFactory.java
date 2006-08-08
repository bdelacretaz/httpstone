package ch.codeconsult.httpstone.infrastructure;

import ch.codeconsult.httpstone.util.PropertiesReader;

import java.util.Properties;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

/** Create a collection of Workers from Properties
 *
 *  @author bdelacretaz@codeconsult.ch
 *  $Id$
 */
 
public class WorkerFactory {

    private static final String PROP_PREFIX = "worker.";
    private final List workers = new LinkedList();

    public WorkerFactory(Properties config) throws Exception {
        // simplistic way of walking the config, it's good enough
        // as we're only using this at startup
        final int MAX_WORKERS = 999;
        for(int i=0; i <= MAX_WORKERS; i++) {
            final String className=config.getProperty(PROP_PREFIX + i + ".class");
            if(className!=null) {
                configureWorker(new PropertiesReader(config),i,className);
            }
        }
    }

    /** create and configure worker */
    private void configureWorker(PropertiesReader config,int index,String className) throws Exception {
        Worker w = null;
        final String ePrefix = "While configuring Worker " + index + ": ";
        try {
            w = (Worker)Class.forName(className).newInstance();
        } catch(Exception e) {
            throw new Exception(ePrefix + "cannot instantiate class " + className + " as a Worker: " + e);
        }

        try {
            w.configure(config,PROP_PREFIX + index + ".");
        } catch(Exception e) {
            throw new Exception(ePrefix + "configuration error:" + e);
        }

        workers.add(w);
    }

    /** get configured Workers */
    public Iterator getWorkersIterator() {
        return workers.iterator();
    }
}

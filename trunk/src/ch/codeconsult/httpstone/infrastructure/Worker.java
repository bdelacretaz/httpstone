package ch.codeconsult.httpstone.infrastructure;

import ch.codeconsult.httpstone.util.PropertiesReader;

import java.awt.Color;
import java.util.Properties;

/** Base class for worker threads, which repeatedly do some action

 *  @author bdelacretaz@codeconsult.ch
 *  $Id$
 */

public abstract class Worker extends Thread {
    private static int instanceCounter;
    private final int instanceId;
    protected Color statusColor = Color.YELLOW;
    protected String statusText = "no status yet";
    protected String actionText = "no action yet";
    protected String infoText = "no info yet";

    private long waitMin = 100;
    private long waitDelta = 1000;

    /** called when status changes */
    public static interface WorkerStatusChangeAction {
        void workerStatusChanged();
    }
    private WorkerStatusChangeAction wsc;

    public Worker() {
        synchronized(Worker.class) {
            instanceId = ++instanceCounter;
        }
        setName("Worker #" + instanceId);
    }

    public int getWorkerId() {
        return instanceId;
    }

    public Color getStatusColor() {
        return statusColor;
    }

    public String getStatusText() {
        return statusText;
    }

    public String getActionText() {
        return actionText;
    }

    public String getInfoText() {
        return infoText;
    }

    /** the wsc will be called when this object's status changes */
    public void setStatusChangeAction(WorkerStatusChangeAction wsc) {
        this.wsc = wsc;
        wsc.workerStatusChanged();
    }

    /** must be called to inform user of status changes */
    protected void statusChanged() {
        if(wsc!=null) wsc.workerStatusChanged();
    }

    /** how much to wait between work iterations */
    protected long getWaitBetweenIterationsMilliseconds() {
        return waitMin + (long)(Math.random() * waitDelta);
    }

    /** how much to wait if an iteration throws an exception */
    protected long getWaitAfterExceptionMilliseconds() {
        return 5000L;
    }

    /** run a loop to do our job, report exceptions */
    public final void run() {
        while(true) {
            statusChanged();
            try {
                doOneIteration();
                waitMsec(getWaitBetweenIterationsMilliseconds(),"next iteration");
            } catch(Exception e) {
                reportException(e);
                try {
                    waitMsec(getWaitAfterExceptionMilliseconds(),"delay after error");
                } catch(Exception ex) {
                    // ignore, cannot do anything here
                }
            }
        }
    }

    /** wait the specified number of msec, changing status if info is provided */
    protected void waitMsec(long msec,String info) throws Exception {
        final String oldAction = actionText;
        if(info!=null) {
            actionText = "waiting " + msec + " msec for " + info;
            statusChanged();
        }
        sleep(msec);

        actionText = oldAction;
    }

    /** for now, report exceptions to stderr only */
    protected void reportException(Exception e) {
        e.printStackTrace();
    }

    /** configure this worker */
    public void configure(PropertiesReader p,String prefix) throws Exception {
        waitMin = p.getRequiredInt(prefix + "delay.msec.min");
        waitDelta = p.getRequiredInt(prefix + "delay.msec.delta");
    }

    /** implement to do one iteration of our work */
    protected abstract void doOneIteration() throws Exception;
}

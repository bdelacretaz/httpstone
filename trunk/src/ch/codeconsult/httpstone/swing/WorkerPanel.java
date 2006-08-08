package ch.codeconsult.httpstone.swing;

import ch.codeconsult.httpstone.infrastructure.Worker;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Font;

/** A JPanel which shows real-time info on a Worker
 *  @author bdelacretaz@codeconsult.ch
 *  $Id$
 */

public class WorkerPanel extends JPanel implements Worker.WorkerStatusChangeAction{
    private final Worker worker;

    private final JLabel title = new JLabel("title");
    private final JLabel info = new JLabel("info");
    private final JLabel status = new JLabel("status");
    private final JLabel currentAction = new JLabel("current action");

    WorkerPanel(Worker w) {
        worker = w;

        title.setText("Worker #" + w.getWorkerId());

        setBorder(BorderFactory.createEtchedBorder());

        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        add(title);
        add(info);
        add(status);
        add(currentAction);

        // fonts and sizes
        final Font std = title.getFont();
        final Font titleFont = new Font(std.getFamily(),Font.BOLD,std.getSize());
        final Font statusFont = new Font(std.getFamily(),std.getStyle(),std.getSize() + 4);

        title.setFont(titleFont);
        status.setFont(statusFont);

        // setOpaque needed so that background color is visible
        title.setOpaque(true);

        // be informed of changes in the Worker status
        w.setStatusChangeAction(this);
    }

    /** called when Worker status changed. Update UI in a thread-safe way */
    public void workerStatusChanged() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateStatus();
            }
        }
        );
    }

    /** call to update our status according to the worker's */
    public void updateStatus() {
        title.setBackground(worker.getStatusColor());
        status.setText(worker.getStatusText());
        info.setText(worker.getInfoText());
        currentAction.setText(worker.getActionText());
    }
}

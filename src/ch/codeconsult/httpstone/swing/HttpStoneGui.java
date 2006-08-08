package ch.codeconsult.httpstone.swing;

import ch.codeconsult.httpstone.infrastructure.Worker;

import javax.swing.*;
import java.awt.GridLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.LinkedList;

/** Swing-based graphical interface for HttpStone
 *  @author bdelacretaz@codeconsult.ch
 *  $Id$
 */

public class HttpStoneGui extends JFrame {
    private final JPanel workersGrid;

    /** HTML Help text at the top of the page */
    public static final String HELP_TEXT =
        "<h2>HttpStone performance test utility</h2>"
        + "<font size=-1>"
        + "<p align=left>Written by Bertrand Delacretaz, bdelacretaz@codeconsult.ch, see http://code.google.com/p/httpstone/</p>"
        + "<p>Each of the workers shown below makes requests continuously to a given URL.</p>"
        + "<p>If the page is not retrieved within a configurable time, an error report is written to disk.</p>"
        + "<p>The colors of the Workers titles indicate the result of the last request.</p>"
        + "</font>"
    ;

    public HttpStoneGui(String windowTitle,String mainProgramInfo) {
        super("HttpStoneGui " + windowTitle);

        final int NCOLS = 3;

        workersGrid = new JPanel();
        workersGrid.setLayout(new GridLayout(0,NCOLS));

        final JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        p.add(new JLabel("<html>" + HELP_TEXT + "<p>" + mainProgramInfo + "</p></html>"));
        p.add(workersGrid);

        this.getContentPane().add(new JScrollPane(p));

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // TODO stop test threads?
                dispose();
            }
        });

        pack();
        setSize(700,500);
        setVisible(true);
    }

    /** add a worker to our display */
    public void addWorker(Worker w) {
        workersGrid.add(new WorkerPanel(w));
    }
}

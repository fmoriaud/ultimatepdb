package ultiJmol;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolAdapter;
import org.jmol.api.JmolViewer;
import org.jmol.viewer.Viewer;
import org.openscience.jmol.app.jmolpanel.console.AppConsole;


public class UltiJMol {

    // just for testing
    public JmolViewer jmolviewerForUlti;
    public Viewer viewerForUlti;
    public JFrame frameForUlti;

    // this class should allow to create a frame with jmol
    public UltiJMol(){

        JFrame frame = new JFrame("JMOL_WS_V1");
        frame.addWindowListener(new ApplicationCloser());
        frame.setSize(410, 700);

        frameForUlti = frame;
        Container contentPane = frame.getContentPane();
        JmolPanel jmolPanel = new JmolPanel();
        jmolPanel.setPreferredSize(new Dimension(400, 400));

        // main panel -- Jmol panel on top

        JPanel panel = new JPanel();

        panel.setLayout(new BorderLayout());
        panel.add(jmolPanel);

        // main panel -- console panel on bottom

        JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout());
        panel2.setPreferredSize(new Dimension(400, 300));
        AppConsole console = new AppConsole(jmolPanel.viewer, panel2, "History State Clear");

        // You can use a different JmolStatusListener or JmolCallbackListener interface
        // if you want to, but AppConsole itself should take care of any console-related callbacks
        jmolPanel.viewer.setJmolCallbackListener(console);

        panel.add("South", panel2);

        contentPane.add(panel);
        frame.setVisible(true); // was true

        jmolviewerForUlti = jmolPanel.viewer;
        //jmolviewerForUlti.evalString("set refreshing false"); // was comment

        if (jmolviewerForUlti instanceof Viewer){
            viewerForUlti = (Viewer) jmolviewerForUlti;
        }
    }
    // and access it with .evalString


    static class ApplicationCloser extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }


    static class JmolPanel extends JPanel {
        /**
         *
         */
        private static final long serialVersionUID = -3661941083797644242L;
        JmolViewer viewer;
        JmolAdapter adapter;
        JmolPanel() {
            adapter = new SmarterJmolAdapter();
            viewer = JmolViewer.allocateViewer(this, adapter);

        }

        public JmolViewer getViewer() {
            return viewer;
        }

        public void executeCmd(String rasmolScript){
            viewer.evalString(rasmolScript);
        }


        final Dimension currentSize = new Dimension();
        final Rectangle rectClip = new Rectangle();

        public void paint(Graphics g) {
            //getSize(currentSize);
            //g.getClipBounds(rectClip);
            viewer.renderScreenImage(g, getWidth(), getHeight());
            //viewer.renderScreenImage(g, currentSize, rectClip);
        }
    }
}



package ultiJmol1462;

import org.openscience.jmol.app.JmolApp;
import org.openscience.jmol.app.jmolpanel.Splash;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Fabrice.Moriaud on 08.09.2016.
 */
public class UltiJmol1462 {

    public static final String viewer       = "org.jmol.api.JmolSimpleViewer";
    public static final String adapter      = "org.jmol.api.JmolAdapter";
    public static final String smartAdapter = "org.jmol.adapter.smarter.SmarterJmolAdapter";


    public JmolPanel jmolPanel;
    public JFrame frame ;


    public UltiJmol1462(){

        frame = new JFrame();

        //JMenuBar menu = MenuCreator.initMenu();

        //frame.setJMenuBar(menu);

       // frame.addWindowListener( new WindowAdapter() {
         //   @Override
           // public void windowClosing(WindowEvent e) {
             //   frame.dispose();
                //System.exit(0);
            //}
        //});

        Container contentPane = frame.getContentPane();

        Box vBox = Box.createVerticalBox();

        jmolPanel = new JmolPanel();

        jmolPanel.setPreferredSize(new Dimension(500,500));
        vBox.add(jmolPanel);


        JTextField field = new JTextField();

        field.setMaximumSize(new Dimension(Short.MAX_VALUE,30));
        field.setText("enter RASMOL like command...");
        RasmolCommandListener listener = new RasmolCommandListener(jmolPanel,field) ;

        field.addActionListener(listener);
        field.addMouseListener(listener);
        field.addKeyListener(listener);
        vBox.add(field);


        /// COMBO BOXES
        Box hBox1 = Box.createHorizontalBox();
        hBox1.setMaximumSize(new Dimension(Short.MAX_VALUE,30));


        String[] styles = new String[] { "Cartoon", "Backbone", "CPK", "Ball and Stick", "Ligands","Ligands and Pocket"};
        JComboBox style = new JComboBox(styles);

        hBox1.add(new JLabel("Style"));
        hBox1.add(style);
        vBox.add(hBox1);


        style.addActionListener(jmolPanel);

        String[] colorModes = new String[] { "Secondary Structure", "By Chain", "Rainbow", "By Element", "By Amino Acid", "Hydrophobicity" };
        JComboBox colors = new JComboBox(colorModes);
        colors.addActionListener(jmolPanel);
        hBox1.add(Box.createGlue());
        hBox1.add(new JLabel("Color"));
        hBox1.add(colors);

        // Check boxes
        Box hBox2 = Box.createHorizontalBox();
        hBox2.setMaximumSize(new Dimension(Short.MAX_VALUE,30));


        JButton resetDisplay = new JButton("Reset Display");

        resetDisplay.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("reset!!");
                jmolPanel.executeCmd("restore STATE state_1");

            }
        });

        hBox2.add(resetDisplay); hBox2.add(Box.createGlue());

        JCheckBox toggleSelection = new JCheckBox("Show Selection");
        toggleSelection.addItemListener(
                new ItemListener() {

                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        boolean showSelection = (e.getStateChange() == ItemEvent.SELECTED);

                        if (showSelection){
                            jmolPanel.executeCmd("set display selected");
                        } else {
                            jmolPanel.executeCmd("set display off");
                        }

                    }
                }
        );



        hBox2.add(toggleSelection);

        hBox2.add(Box.createGlue());
        vBox.add(hBox2);


        // finish up
        contentPane.add(vBox);
        frame.pack();
        frame.setVisible(true);
    }


    public void evalString(String rasmolScript){
        if ( jmolPanel == null ){
            System.err.println("please install Jmol first");
            return;
        }
        jmolPanel.evalString(rasmolScript);
    }


    public void openStringInline(String model){
        jmolPanel.openStringInline(model);
    }
}

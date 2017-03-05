package gui;

import io.IOTools;
import net.miginfocom.swing.MigLayout;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import protocols.ProtocolTools;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class UltimatepdbDialog extends JDialog {

    //------------------------------------------------------------------------------
    // DATA MEMBERS
    //------------------------------------------------------------------------------
    private AlgoParameters algoParameters;
    private Controller controller;
    private JTabbedPane tabbedPane;
    private JTextField pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER = new JTextField(10);

    private Map<String, java.util.List<Path>> indexPDBFileInFolder;


    //------------------------------------------------------------------------------
    // CONSTRUCTORS
    //------------------------------------------------------------------------------
    public UltimatepdbDialog() throws ParsingConfigFileException {

        algoParameters = ProtocolTools.prepareAlgoParameters();

        pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER.setText(algoParameters.getPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER());
        controller = new Controller();
        setModal(true);
        createPanel();
        setTitle("UltimatePDB");
        pack();
    }


    //------------------------------------------------------------------------------
    // IMPLEMENTATION
    //------------------------------------------------------------------------------
    private void createPanel() {


        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);


        // PDB Panel
        JPanel panel = new JPanel();
        panel.setLayout(new MigLayout("fill", "[][grow][]"));
        // "fill, ins dialog", "[][grow][]", "[][]"
        Label labelPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER = new Label("Path to PDB MMcif root folder :");

        JButton browseToPDBMMcifFolder = new JButton("Browse...");

        browseToPDBMMcifFolder.addActionListener(e -> indexMMcifPDBFolder(pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER));
        panel.add(labelPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER);
        panel.add(pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER, "grow");
        panel.add(browseToPDBMMcifFolder, "wrap");

        TitledBorder border = new TitledBorder(null, "Setup MMcif files", TitledBorder.LEADING, TitledBorder.TOP, null, null);
        panel.setBorder(border);
        tabbedPane.addTab("PDB", panel);

        // Path to results folder
        JPanel panel2 = new JPanel();
        panel2.setLayout(new MigLayout("fill", "[][grow][]"));
        Label labelPATH_TO_RESULTS = new Label("Path to result folder :");
        JTextField pATH_TO_RESULTS_FOLDER = new JTextField(10);

        JButton browseToResultsFolder = new JButton("Browse...");
        panel2.add(labelPATH_TO_RESULTS);
        panel2.add(pATH_TO_RESULTS_FOLDER, "grow");
        panel2.add(browseToResultsFolder, "wrap");
        TitledBorder border2 = new TitledBorder(null, "Setup MMcif files", TitledBorder.LEADING, TitledBorder.TOP, null, null);
        panel2.setBorder(border2);
        tabbedPane.addTab("Results", panel2);

        add(tabbedPane);
    }


    private void indexMMcifPDBFolder(JTextField pATH_to_remediated_pdb_mmcif_folder) {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setCurrentDirectory(new File(pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER.getText()));

        int returnVal = fileChooser.showDialog(this, "Select MMcif PDB root folder");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER.setText(file.getAbsolutePath());
            indexPDBFileInFolder = IOTools.indexPDBFileInFolder(file.getAbsolutePath());
            System.out.println("Found : " + indexPDBFileInFolder.size() + " mmCif Files");

        } else {
            pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER.setText("");
        }

    }
}

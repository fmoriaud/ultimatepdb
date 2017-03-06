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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class UltimatepdbDialog extends JDialog {

    //------------------------------------------------------------------------------
    // DATA MEMBERS
    //------------------------------------------------------------------------------
    private AlgoParameters algoParameters;
    private Controller controller;
    private JTabbedPane tabbedPane;
    private JTextField pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER = new JTextField(10);
    private JTextField pdbCount = new JTextField(10);
    private JPanel panelRun;

    private Map<String, java.util.List<Path>> indexPDBFileInFolder;


    //------------------------------------------------------------------------------
    // CONSTRUCTORS
    //------------------------------------------------------------------------------
    public UltimatepdbDialog() throws ParsingConfigFileException {

        algoParameters = ProtocolTools.prepareAlgoParameters();

        controller = new Controller();
        setModal(true);
        createPanel();
        setTitle("UltimatePDB");
        pack();

        String pathFromXmlFile = algoParameters.getPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER();
        Path pdbFolder = Paths.get(pathFromXmlFile);
        if (Files.exists(pdbFolder)){
            pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER.setText(pathFromXmlFile);
            updateWithPDBpath(pdbFolder.toFile());
        }
    }


    //------------------------------------------------------------------------------
    // IMPLEMENTATION
    //------------------------------------------------------------------------------
    private void createPanel() {


        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);


        // PDB Panel
        JPanel panelPDB = new JPanel();
        panelPDB.setLayout(new MigLayout("fill", "[][grow][]"));
        // "fill, ins dialog", "[][grow][]", "[][]"
        Label labelPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER = new Label("Path to PDB MMcif root folder :");
        JButton browseToPDBMMcifFolder = new JButton("Browse...");

        Label labelPDBcount = new Label("PDB MMcif files found :");


        browseToPDBMMcifFolder.addActionListener(e -> indexMMcifPDBFolder(pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER));
        panelPDB.add(labelPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER);
        panelPDB.add(pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER, "grow");
        panelPDB.add(browseToPDBMMcifFolder, "wrap");

        panelPDB.add(labelPDBcount);
        panelPDB.add(pdbCount, "wrap");


        TitledBorder border = new TitledBorder(null, "Setup MMcif files", TitledBorder.LEADING, TitledBorder.TOP, null, null);
        panelPDB.setBorder(border);
        tabbedPane.addTab("PDB", panelPDB);

        // Path to results folder
        panelRun = new JPanel();
        panelRun.setLayout(new MigLayout("fill", "[][grow][]"));
        Label labelPATH_TO_RESULTS = new Label("Path to result folder :");
        JTextField pATH_TO_RESULTS_FOLDER = new JTextField(10);

        JButton browseToResultsFolder = new JButton("Browse...");
        panelRun.add(labelPATH_TO_RESULTS);
        panelRun.add(pATH_TO_RESULTS_FOLDER, "grow");
        panelRun.add(browseToResultsFolder, "wrap");
        TitledBorder border2 = new TitledBorder(null, "Path to results folder", TitledBorder.LEADING, TitledBorder.TOP, null, null);
        panelRun.setBorder(border2);
        //tabbedPane.addTab("Run", panelRun);

        add(tabbedPane);
    }


    private void indexMMcifPDBFolder(JTextField pATH_to_remediated_pdb_mmcif_folder) {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setCurrentDirectory(new File(pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER.getText()));

        int returnVal = fileChooser.showDialog(this, "Select MMcif PDB root folder");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            updateWithPDBpath(file);

        } else {
            pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER.setText("");
            tabbedPane.remove(panelRun);
        }
    }

    private void updateWithPDBpath(File file) {

        pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER.setText(file.getAbsolutePath());
        indexPDBFileInFolder = IOTools.indexPDBFileInFolder(file.getAbsolutePath());
        if (indexPDBFileInFolder.size() > 0){
            tabbedPane.addTab("Run", panelRun);
        }else{
            pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER.setText("");
            tabbedPane.remove(panelRun);
        }
        pdbCount.setText(String.valueOf(indexPDBFileInFolder.size()));
    }
}

/*
Author:
      Fabrice Moriaud <fmoriaud@ultimatepdb.org>

  Copyright (c) 2016 Fabrice Moriaud

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */
package gui;

import database.HashTablesTools;
import database.SequenceTools;
import io.FileListingVisitorForPDBCifGzFiles;
import io.IOTools;
import io.MMcifFileInfos;
import net.miginfocom.swing.MigLayout;
import protocols.ParsingConfigFileException;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class UltimatepdbDialog extends JDialog {
    //------------------------------------------------------------------------------
    // DATA MEMBERS
    //------------------------------------------------------------------------------
    private Controller controller;
    private JTabbedPane tabbedPane;


    private JPanel panelPDB;
    private JTextField pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER = new JTextField(30);

    private JPanel panelQuery;
    private JTextField query4letterCode = new JTextField(4);
    private String defaulPDB4letterCode = "1be9";
    private JButton validate4LetterCode;

    private JPanel panelRun;
    private JTextField pATH_TO_RESULT_FOLDER = new JTextField(30);
    private JTextField pdbCount = new JTextField(10);
    private JTextField sequenceDBCount = new JTextField(10);

    private JTextField countOfFileToUpdate = new JTextField(10);

    private JButton updateSequenceDB;
    private JProgressBar pbar;

    //------------------------------------------------------------------------------
    // CONSTRUCTORS
    //------------------------------------------------------------------------------
    public UltimatepdbDialog() throws ParsingConfigFileException {

        pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER.getPreferredSize().height));
        pATH_TO_RESULT_FOLDER.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, pATH_TO_RESULT_FOLDER.getPreferredSize().height));

        query4letterCode.setText(defaulPDB4letterCode);
        validate4LetterCode = new JButton("Validate");
        updateSequenceDB = new JButton("update");
        controller = new Controller();
        setModal(true);
        createPanel();
        setTitle("UltimatePDB");
        pack();

        String pathFromXmlFile = controller.getAlgoParameters().getPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER();
        Path pdbFolder = Paths.get(pathFromXmlFile);
        if (Files.exists(pdbFolder)) {
            updateWithPDBpath(pdbFolder.toFile());
        }
        String pathFromXmlFileResult = controller.getAlgoParameters().getPATH_TO_RESULT_FILES();
        Path resultFolder = Paths.get(pathFromXmlFileResult);
        if (Files.exists(resultFolder)) {
            pATH_TO_RESULT_FOLDER.setText(pathFromXmlFileResult);
        }

        int countOfFilesToUpdate = updateWithSeqDB();
        countOfFileToUpdate.setText(String.valueOf(countOfFilesToUpdate));

    }


    //------------------------------------------------------------------------------
    // IMPLEMENTATION
    //------------------------------------------------------------------------------
    private void createPanel() {

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        buildPDBpanel();
        tabbedPane.addTab("PDB", panelPDB);

        buildQueryPanel();
        tabbedPane.addTab("Query", panelQuery);
        buildRunPanel();

        add(tabbedPane);
    }


    private void buildQueryPanel() {

        panelQuery = new JPanel();
        panelQuery.setLayout(new MigLayout("fill", "[][]"));

        panelQuery.add(query4letterCode, "split 2");
        panelQuery.add(validate4LetterCode, "wrap");

        validate4LetterCode.addActionListener(e -> initializeTabbedPane(query4letterCode.getText()));


        TitledBorder border = new TitledBorder(null, "Define query", TitledBorder.LEADING, TitledBorder.TOP, null, null);

        panelQuery.setBorder(border);

    }

    private void initializeTabbedPane(String text) {

        FindQueriesFrom4LetterCode find = new FindQueriesFrom4LetterCode(text, controller.getAlgoParameters());
        String[] chains = find.getChains();

        JTabbedPane tabbedPane = builtTabbedPane(chains);
        panelQuery.add(tabbedPane, "span, grow");
    }


    private JTabbedPane builtTabbedPane(String[] chains) {

        String BUTTONPANEL = "Chain";
        String TEXTPANEL = "HetAtm";

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel card1 = new JPanel();
        JTextField chainsText = new JTextField();
        StringBuilder sb = new StringBuilder();
        for (String chain : chains) {
            sb.append(chain + " ");
        }
        chainsText.setText(sb.toString());
        card1.add(chainsText);

        JPanel card2 = new JPanel();
        card2.add(new JTextField("TextField", 20));

        tabbedPane.addTab(BUTTONPANEL, card1);
        tabbedPane.addTab(TEXTPANEL, card2);

        return tabbedPane;
    }


    private void buildPDBpanel() {

        panelPDB = new JPanel();
        panelPDB.setLayout(new MigLayout("fill", "[][grow][]"));
        // "fill, ins dialog", "[][grow][]", "[][]"
        Label labelPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER = new Label("Path to PDB MMcif root folder :");
        JButton browseToPDBMMcifFolder = new JButton("Browse...");


        browseToPDBMMcifFolder.addActionListener(e -> updateIndexMMcifPDBFolder(pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER));
        panelPDB.add(labelPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER);
        panelPDB.add(pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER, "grow");
        panelPDB.add(browseToPDBMMcifFolder, "wrap");

        Label labelPDBcount = new Label("PDB MMcif files found :");
        panelPDB.add(labelPDBcount);
        panelPDB.add(pdbCount, "wrap");

        JSeparator separator = new JSeparator();
        //separator.setOrientation(SwingConstants.HORIZONTAL);
        separator.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(1, 0, 0)));
        separator.setPreferredSize(new Dimension(200, 1));
        panelPDB.add(separator, "span, span, wrap");

        Label labelSequenceDBcount = new Label("PDB chains indexed in sequence DB :");
        panelPDB.add(labelSequenceDBcount);
        panelPDB.add(sequenceDBCount);
        //panelPDB.add(countOfFileToUpdate, "wrap");
        panelPDB.add(updateSequenceDB);
        updateSequenceDB.addActionListener(e -> updateSequenceDb());

        TitledBorder border = new TitledBorder(null, "Setup MMcif files", TitledBorder.LEADING, TitledBorder.TOP, null, null);

        // pbar = new JProgressBar();
        //pbar.setMinimum(0);
        //pbar.setMaximum(100);
        // panelPDB.add(pbar, "wrap");

        panelPDB.setBorder(border);
    }


    private void buildRunPanel() {

        panelRun = new JPanel();
        panelRun.setLayout(new MigLayout("fill", "[][grow][]"));
        Label labelPATH_TO_RESULTS = new Label("Path to result folder :");

        JButton browseToResultsFolder = new JButton("Browse...");
        browseToResultsFolder.addActionListener(e -> updateResultFolder(pATH_TO_RESULT_FOLDER));

        panelRun.add(labelPATH_TO_RESULTS);
        panelRun.add(pATH_TO_RESULT_FOLDER, "grow");
        panelRun.add(browseToResultsFolder, "wrap");
        TitledBorder border2 = new TitledBorder(null, "Path to results folder", TitledBorder.LEADING, TitledBorder.TOP, null, null);
        panelRun.setBorder(border2);
    }


    private void updateResultFolder(JTextField pATH_to_remediated_pdb_mmcif_folder) {

        File folder = browseFolder(pATH_to_remediated_pdb_mmcif_folder.getText(), " Browse to result folder");
        if (folder != null) {
            pATH_to_remediated_pdb_mmcif_folder.setText(folder.getAbsolutePath());
        } else {
            pATH_to_remediated_pdb_mmcif_folder.setText("");
        }
    }

    private void updateIndexMMcifPDBFolder(JTextField pATH_to_remediated_pdb_mmcif_folder) {

        File pdbFolder = browseFolder(pATH_to_remediated_pdb_mmcif_folder.getText(), "Select MMcif PDB root folder");

        if (pdbFolder != null) {
            updateWithPDBpath(pdbFolder);
        } else {
            pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER.setText("");
            tabbedPane.remove(panelRun);
            tabbedPane.remove(panelQuery);

        }
    }


    private void updateSequenceDb() {


        Map<String, java.util.List<MMcifFileInfos>> indexPDBFileInFolder = IOTools.indexPDBFileInFolder(controller.getAlgoParameters().getPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER());
        //pbar.setMaximum(0);
        //pbar.setMaximum(indexPDBFileInFolder.size());
        int current = 0;
        int fileAddedCount = 0;
        for (Map.Entry<String, java.util.List<MMcifFileInfos>> entry : indexPDBFileInFolder.entrySet()) {
            for (MMcifFileInfos fileInfos : entry.getValue()) {
                try {

                    String fourLetterCode = FileListingVisitorForPDBCifGzFiles.makeFourLetterCodeUpperCaseFromFileNameForMmcifGzFiles(fileInfos.getPathToFile().getFileName().toString());
                    boolean fileAdded = HashTablesTools.addAFile(fileInfos.getPathToFile(), fourLetterCode, HashTablesTools.getConnection(), HashTablesTools.tableSequenceName, HashTablesTools.tableSequenceFailureName, controller.getAlgoParameters());
                    if (fileAdded) {
                        fileAddedCount += 1;
                    }
                } catch (NoSuchAlgorithmException | IOException e) {
                    e.printStackTrace();
                }
            }
            current += 1;
            System.out.println(current + "  " + fileAddedCount);
            //sequenceDBCount.setText(String.valueOf(current));
            //sequenceDBCount.updateUI();
            //panelPDB.updateUI();
            //tabbedPane.updateUI();
            //pbar.setValue(current);
            //pbar.updateUI();
            //panelPDB.updateUI();
        }
        int countOfFilesToUpdate = updateWithSeqDB();
        countOfFileToUpdate.setText(String.valueOf(countOfFilesToUpdate));
    }


    private int updateWithSeqDB() {

        int numberChainInDB = SequenceTools.findNumberOfEntries();
        sequenceDBCount.setText(String.valueOf(numberChainInDB));
        int countOfFilesToUpdate = HashTablesTools.countFilesWhichAreAlreadyIndexedInSequenceDB(HashTablesTools.tableSequenceName, HashTablesTools.tableSequenceFailureName, controller.getAlgoParameters().getIndexPDBFileInFolder());

        return countOfFilesToUpdate;
    }


    private File browseFolder(String defaultPath, String dialogTitle) {

        File file = null;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setCurrentDirectory(new File(defaultPath));

        int returnVal = fileChooser.showDialog(this, dialogTitle);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        }
        return file;
    }


    private void updateWithPDBpath(File file) {

        int pdbFileNumber = controller.updatePDBFileFoldersAndIndexing(file.getAbsolutePath());

        if (pdbFileNumber > 0) {
            pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER.setText(file.getAbsolutePath());
            tabbedPane.addTab("Query", panelQuery);
            tabbedPane.addTab("Run", panelRun);

        } else {
            pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER.setText("");
            tabbedPane.remove(panelRun);
            tabbedPane.remove(panelQuery);
        }
        pdbCount.setText(String.valueOf(pdbFileNumber));
    }
}

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

import io.IOTools;
import net.miginfocom.swing.MigLayout;
import protocols.ParsingConfigFileException;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class UltimatepdbDialog extends JDialog {

    //------------------------------------------------------------------------------
    // DATA MEMBERS
    //------------------------------------------------------------------------------
    private Controller controller;
    private JTabbedPane tabbedPane;
    private JTextField pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER = new JTextField(30);
    private JTextField pATH_TO_RESULT_FOLDER = new JTextField(30);
    private JTextField pdbCount = new JTextField(10);
    private JPanel panelRun;

    private Map<String, java.util.List<Path>> indexPDBFileInFolder;


    //------------------------------------------------------------------------------
    // CONSTRUCTORS
    //------------------------------------------------------------------------------
    public UltimatepdbDialog() throws ParsingConfigFileException {

        pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER.getPreferredSize().height));
        pATH_TO_RESULT_FOLDER.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, pATH_TO_RESULT_FOLDER.getPreferredSize().height));

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


        JButton browseToResultsFolder = new JButton("Browse...");
        browseToResultsFolder.addActionListener(e -> updateResultFolder(pATH_TO_RESULT_FOLDER));

        panelRun.add(labelPATH_TO_RESULTS);
        panelRun.add(pATH_TO_RESULT_FOLDER, "grow");
        panelRun.add(browseToResultsFolder, "wrap");
        TitledBorder border2 = new TitledBorder(null, "Path to results folder", TitledBorder.LEADING, TitledBorder.TOP, null, null);
        panelRun.setBorder(border2);
        //tabbedPane.addTab("Run", panelRun);

        add(tabbedPane);
    }


    private void updateResultFolder(JTextField pATH_to_remediated_pdb_mmcif_folder) {

        File folder = browseFolder(pATH_to_remediated_pdb_mmcif_folder.getText(), " Browse to result folder");
        if (folder != null) {
            pATH_to_remediated_pdb_mmcif_folder.setText(folder.getAbsolutePath());
        } else {
            pATH_to_remediated_pdb_mmcif_folder.setText("");
        }
    }

    private void indexMMcifPDBFolder(JTextField pATH_to_remediated_pdb_mmcif_folder) {

        File pdbFolder = browseFolder(pATH_to_remediated_pdb_mmcif_folder.getText(), "Select MMcif PDB root folder");

        if (pdbFolder != null) {
            updateWithPDBpath(pdbFolder);
        } else {
            pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER.setText("");
            tabbedPane.remove(panelRun);
        }
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


        Integer pdbFileNumber = controller.countPDBFiles(file.getAbsolutePath());
        indexPDBFileInFolder = IOTools.indexPDBFileInFolder(file.getAbsolutePath());
        if (pdbFileNumber != null && pdbFileNumber > 0) {
            pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER.setText(file.getAbsolutePath());
            tabbedPane.addTab("Run", panelRun);
        } else {
            pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER.setText("");
            tabbedPane.remove(panelRun);
        }
        pdbCount.setText(String.valueOf(indexPDBFileInFolder.size()));
    }
}

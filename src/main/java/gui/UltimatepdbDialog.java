package gui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class UltimatepdbDialog extends JDialog{

    //------------------------------------------------------------------------------
    // DATA MEMBERS
    //------------------------------------------------------------------------------
    private Controller controller;

    private JTextField pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER;


    //------------------------------------------------------------------------------
    // CONSTRUCTORS
    //------------------------------------------------------------------------------
    public UltimatepdbDialog(){

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

        JPanel panel = new JPanel();
        panel.setLayout(new MigLayout("fill", "[][grow][]"));
        // "fill, ins dialog", "[][grow][]", "[][]"
        Label labelPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER = new Label("Path to PDB MMcif root folder :");
        pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER = new JTextField(10);
        JButton browseToPDBMMcifFolder = new JButton("Browse...");

        browseToPDBMMcifFolder.addActionListener(e -> indexMMcifPDBFolder(pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER));
        panel.add(labelPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER);
        panel.add(pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER, "grow");
        panel.add(browseToPDBMMcifFolder, "wrap");

        TitledBorder border = new TitledBorder(null, "Setup MMcif files", TitledBorder.LEADING, TitledBorder.TOP, null, null);
        panel.setBorder(border);
        add(panel);
    }



    private void indexMMcifPDBFolder(JTextField pATH_to_remediated_pdb_mmcif_folder) {


    }
}

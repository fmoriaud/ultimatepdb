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
package parameters;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import genericBuffer.GenericBuffer;
import genericBuffer.MyStructureBuffer;
import io.IOTools;
import io.MMcifFileInfos;
import jmolgui.UltiJmol1462;
import math.ProcrustesAnalysisIfc;

/**
 * AlgoParameters stores all global parameters.
 * AlgoParameters contains buffers to store UltiJmol classes and ProcrustesAnalysis classes. They are as many as thresds.
 * N.B When parameters are elsewhere in a class or in a method, they are meant to be implementation dependent and
 * are therefore local.
 *
 */
public class AlgoParameters {

    //-------------------------------------------------------------
    // Buffers
    //-------------------------------------------------------------
    public GenericBuffer<UltiJmol1462> ultiJMolBuffer;
    public GenericBuffer<ProcrustesAnalysisIfc> procrustesAnalysisBuffer;
    public MyStructureBuffer myStructureBuffer;


    //-------------------------------------------------------------
    // PDB files indexing
    //-------------------------------------------------------------
    private Map<String, List<MMcifFileInfos>> indexPDBFileInFolder;


    // ****************************
    // Admin parameters
    //
    private String PATH_TO_TARGET_LIST_FILE_TO_BE_PROCESSED_BUILD_SEQ_DB;

    public String getPATH_TO_TARGET_LIST_FILE_TO_BE_PROCESSED_BUILD_SEQ_DB() {
        return PATH_TO_TARGET_LIST_FILE_TO_BE_PROCESSED_BUILD_SEQ_DB;
    }

    public void setPATH_TO_TARGET_LIST_FILE_TO_BE_PROCESSED_BUILD_SEQ_DB(
            String pATH_TO_TARGET_LIST_FILE_TO_BE_PROCESSED_BUILD_SEQ_DB) {
        PATH_TO_TARGET_LIST_FILE_TO_BE_PROCESSED_BUILD_SEQ_DB = pATH_TO_TARGET_LIST_FILE_TO_BE_PROCESSED_BUILD_SEQ_DB;
    }

    // ****************************
    // Ultimate.XML file parameters
    //
    private boolean OPTIMIZE_HIT_GEOMETRY;

    private int SHAPE_COMPARISON_THREAD_COUNT = 1;
    private int SUB_THREAD_COUNT_FORK_AND_JOIN = 1;

    private String PATH_TO_REMEDIATED_PDB_MMCIF_FOLDER;
    private String PATH_TO_CHEMCOMP_FOLDER;
    private String PATH_TO_RESULT_FILES;

    private String QUERY_PDB_FOUR_LETTER_CODE;

    private String QUERY_TYPE;

    private List<String> CHAIN_TO_IGNORE;

    // different storage for different query type
    // Query related to a single ChainId
    private String QUERY_CHAIN_ID;
    // WHOLE_CHAIN
    // SEGMENT_OF_CHAIN
    private int STARTING_RANK_ID;
    private int PEPTIDE_LENGTH;

    // HETATM
    private String QUERY_PDB_THREE_LETTER_CODE;
    private int OCCURENCE_ID;

    // Query related to atoms which are within the query
    // ATOMIDS_WITHIN_SHAPE
    private List<QueryAtomDefinedByIds> QUERY_ATOMS_DEFINED_BY_IDS;


    // Getters only
    public boolean isOPTIMIZE_HIT_GEOMETRY() {
        return OPTIMIZE_HIT_GEOMETRY;
    }

    public int getSHAPE_COMPARISON_THREAD_COUNT() {
        return SHAPE_COMPARISON_THREAD_COUNT;
    }

    public int getSUB_THREAD_COUNT_FORK_AND_JOIN() {
        return SUB_THREAD_COUNT_FORK_AND_JOIN;
    }

    public String getPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER() {
        return PATH_TO_REMEDIATED_PDB_MMCIF_FOLDER;
    }

    public String getPATH_TO_CHEMCOMP_FOLDER() {
        return PATH_TO_CHEMCOMP_FOLDER;
    }

    public String getPATH_TO_RESULT_FILES() {
        return PATH_TO_RESULT_FILES;
    }

    public String getQUERY_PDB_FOUR_LETTER_CODE() {
        return QUERY_PDB_FOUR_LETTER_CODE;
    }

    public String getQUERY_TYPE() {
        return QUERY_TYPE;
    }

    public List<String> getCHAIN_TO_IGNORE() {
        return CHAIN_TO_IGNORE;
    }

    public String getQUERY_CHAIN_ID() {
        return QUERY_CHAIN_ID;
    }

    public int getSTARTING_RANK_ID() {
        return STARTING_RANK_ID;
    }

    public int getPEPTIDE_LENGTH() {
        return PEPTIDE_LENGTH;
    }

    public String getQUERY_PDB_THREE_LETTER_CODE() {
        return QUERY_PDB_THREE_LETTER_CODE;
    }

    public int getOCCURENCE_ID() {
        return OCCURENCE_ID;
    }

    public List<QueryAtomDefinedByIds> getQUERY_ATOMS_DEFINED_BY_IDS() {
        return QUERY_ATOMS_DEFINED_BY_IDS;
    }

    // Setters only
    public void setOPTIMIZE_HIT_GEOMETRY(boolean oPTIMIZE_HIT_GEOMETRY) {
        OPTIMIZE_HIT_GEOMETRY = oPTIMIZE_HIT_GEOMETRY;
    }

    public void setSUB_THREAD_COUNT_FORK_AND_JOIN(int sUB_THREAD_COUNT_FORK_AND_JOIN) {
        SUB_THREAD_COUNT_FORK_AND_JOIN = sUB_THREAD_COUNT_FORK_AND_JOIN;
    }

    public void setSHAPE_COMPARISON_THREAD_COUNT(int sHAPE_COMPARISON_THREAD_COUNT) {
        SHAPE_COMPARISON_THREAD_COUNT = sHAPE_COMPARISON_THREAD_COUNT;
    }

    public void setPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER(
            String pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER) {
        PATH_TO_REMEDIATED_PDB_MMCIF_FOLDER = pATH_TO_REMEDIATED_PDB_MMCIF_FOLDER;
    }

    public void setPATH_TO_CHEMCOMP_FOLDER(String pATH_TO_CHEMCOMP_FOLDER) {
        PATH_TO_CHEMCOMP_FOLDER = pATH_TO_CHEMCOMP_FOLDER;
    }

    public void setPATH_TO_RESULT_FILES(String pATH_TO_RESULT_FILES) {
        PATH_TO_RESULT_FILES = pATH_TO_RESULT_FILES;
    }

    public void setQUERY_PDB_FOUR_LETTER_CODE(String qUERY_PDB_FOUR_LETTER_CODE) {
        QUERY_PDB_FOUR_LETTER_CODE = qUERY_PDB_FOUR_LETTER_CODE;
    }

    public void setQUERY_TYPE(String qUERY_TYPE) {
        QUERY_TYPE = qUERY_TYPE;
    }

    public void setCHAIN_TO_IGNORE(List<String> cHAIN_TO_IGNORE) {
        CHAIN_TO_IGNORE = cHAIN_TO_IGNORE;
    }

    public void setQUERY_CHAIN_ID(String qUERY_CHAIN_ID) {
        QUERY_CHAIN_ID = qUERY_CHAIN_ID;
    }

    public void setSTARTING_RANK_ID(int sTARTING_RANK_ID) {
        STARTING_RANK_ID = sTARTING_RANK_ID;
    }

    public void setPEPTIDE_LENGTH(int pEPTIDE_LENGTH) {
        PEPTIDE_LENGTH = pEPTIDE_LENGTH;
    }

    public void setQUERY_PDB_THREE_LETTER_CODE(String qUERY_PDB_THREE_LETTER_CODE) {
        QUERY_PDB_THREE_LETTER_CODE = qUERY_PDB_THREE_LETTER_CODE;
    }

    public void setOCCURENCE_ID(int oCCURENCE_ID) {
        OCCURENCE_ID = oCCURENCE_ID;
    }

    public void setQUERY_ATOMS_DEFINED_BY_IDS(
            List<QueryAtomDefinedByIds> qUERY_ATOMS_DEFINED_BY_IDS) {
        QUERY_ATOMS_DEFINED_BY_IDS = qUERY_ATOMS_DEFINED_BY_IDS;
    }

    // ****************************
    // Ultimate parameters not exposed in ultimate.xml file
    private float MIN_DISTANCE_TO_BE_NEIBHOR_IN_JMOL_MINIMIZATION = 5.0f;
    private float LIMIT_MAX_DISTANCE_TO_STOP_MERGING = 1.0f; // 1.5f; // clustering
    private int FINGERPRINT_COUNT_OF_BINS = 10;
    private float FINGERPRINT_SIZE_OF_BINS_ANGSTROM = 2.0f;
    private float LENNARD_JONES_CUTOFF_MAX = -0.001f;
    private float LENNARD_JONES_CUTOFF_MIN = -0.003f;
    private boolean USE_CUTOFF_PROBABILITY_IN_SHAPES = true;
    private float CUTOFF_MIN_PROBABILITY_IN_SHAPES = 0.04f;


    private float DISTANCE_MIN_FOR_EXTENDED_PAIRING_FROM_SEED = 2.5f;
    private float ANGLE_DIFF_TOLERANCE = (float) (1.0f * Math.PI / 3.0f);
    private float EDGE_DIFF_TOLERANCE = 4.0f;
    private float MIN_DISTANCE_TO_BE_NEIBHOR = 12.0f;
    private float CELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM = 0.7f;
    private float EXTRA_DISTANCE_OUT_FORALLBOX = 5.0f;
    private int CUTOFF_COUNT_OF_HYDROPHOBIC_ATOM_SURRONDING_HBOND = 3;
    private float CUTOFF_DISTANCE_FORHYDROPHOBIC_AROUND_HBOND = 6.5f;
    private float DISTANCE_FROM_PEPTIDE_TO_WHICH_INTERACTINGPROTEIN_IS_CONSIDERED = 5.0f;

    private float WEIGHT_HBOND_DONNOR = 1.0f;
    private float WEIGHT_HBOND_ACCEPTOR = 1.0f;
    private float WEIGHT_DEHYDRON = 1.0f;
    private float WEIGHT_DIFFERENCE_IN_CHARGES_BETWEEN_PAIRED_POINTS = 1.0f;
    private float WEIGHT_DIFFERENCE_IN_HYDROPHOBICITY_BETWEEN_PAIRED_POINTS = 1.0f;
    private float WEIGHT_DIFFERENCE_AROMATICRING = 1.0f;

    private float WEIGHT_DIFFERENCE_IN_PROBABILITIES_IN_PAIRED_POINTS = 1.0f;
    private float WEIGHT_DIFFERENCE_IN_PROBA_IN_DISTANCES_BETWEEN_PAIRED_POINTS = 0.4f;
    private float WEIGHT_DIFFERENCE_TO_OUTSIDE = 2.0f;//1.0f;

    private float WEIGHT_UNPAIRED_POINT_IN_SMALLEST_MAP = 0.5f;


    private float THRESHOLD_DISTANCE_TO_KEEP_NEIGHBORING_NONE_STRIKING_PROPERTY = 1.5f;
    private int DEHYDRON_CUTOFF_COUNT_OF_HYDROPHOBIC_ATOM_SURRONDING_HBOND = 16;
    private float DISTANCE_FROM_PEPTIDE_TO_WHICH_INTERACTINGPROTEIN_IS_SHORTENED = 4.0f;
    private int MAX_COUNT_NONE_PROPERTY_IN_TRIANGLE = 2;
    private float ANGLE_MAX = (float) (5.0 * Math.PI / 6.0);
    private float ANGLE_MIN = (float) (Math.PI / 6.0);
    private int COUNT_OF_INCREMENT_ANGLE = 28;
    private float FIRST_RADIUS_INCREMENT_IN_SHAPE_REDUCTION = 4.0f;

    private float FRACTION_NEEDED_ON_QUERY = 0.75f;
    private int COUNT_OF_RESIDUES_IGNORED_IN_SHAPE_BUILDING_BEFORE_AND_AFTER_PEPTIDE = 0;

    private int DOCKING_PEPTIDES_SPLITTING_SEQUENCE_LENGTH = 5;


    // -------------------------------------------------------------------
    // Getter and Setter
    // -------------------------------------------------------------------
    public Map<String, List<MMcifFileInfos>> getIndexPDBFileInFolder() {

        if (indexPDBFileInFolder == null){
            Map<String, List<MMcifFileInfos>> indexPDBFileInFolder = IOTools.indexPDBFileInFolder(PATH_TO_REMEDIATED_PDB_MMCIF_FOLDER);
            setIndexPDBFileInFolder(indexPDBFileInFolder);
        }
        return indexPDBFileInFolder;
    }

    public void setIndexPDBFileInFolder(Map<String, List<MMcifFileInfos>> indexPDBFileInFolder) {
        this.indexPDBFileInFolder = indexPDBFileInFolder;
    }

    public float getMIN_DISTANCE_TO_BE_NEIBHOR_IN_JMOL_MINIMIZATION() {
        return MIN_DISTANCE_TO_BE_NEIBHOR_IN_JMOL_MINIMIZATION;
    }

    public void setMIN_DISTANCE_TO_BE_NEIBHOR_IN_JMOL_MINIMIZATION(
            float mIN_DISTANCE_TO_BE_NEIBHOR_IN_JMOL_MINIMIZATION) {
        MIN_DISTANCE_TO_BE_NEIBHOR_IN_JMOL_MINIMIZATION = mIN_DISTANCE_TO_BE_NEIBHOR_IN_JMOL_MINIMIZATION;
    }

    public float getLIMIT_MAX_DISTANCE_TO_STOP_MERGING() {
        return LIMIT_MAX_DISTANCE_TO_STOP_MERGING;
    }

    public void setLIMIT_MAX_DISTANCE_TO_STOP_MERGING(
            float lIMIT_MAX_DISTANCE_TO_STOP_MERGING) {
        LIMIT_MAX_DISTANCE_TO_STOP_MERGING = lIMIT_MAX_DISTANCE_TO_STOP_MERGING;
    }

    public int getFINGERPRINT_COUNT_OF_BINS() {
        return FINGERPRINT_COUNT_OF_BINS;
    }

    public void setFINGERPRINT_COUNT_OF_BINS(int fINGERPRINT_COUNT_OF_BINS) {
        FINGERPRINT_COUNT_OF_BINS = fINGERPRINT_COUNT_OF_BINS;
    }

    public float getFINGERPRINT_SIZE_OF_BINS_ANGSTROM() {
        return FINGERPRINT_SIZE_OF_BINS_ANGSTROM;
    }

    public void setFINGERPRINT_SIZE_OF_BINS_ANGSTROM(
            float fINGERPRINT_SIZE_OF_BINS_ANGSTROM) {
        FINGERPRINT_SIZE_OF_BINS_ANGSTROM = fINGERPRINT_SIZE_OF_BINS_ANGSTROM;
    }

    public float getLENNARD_JONES_CUTOFF_MAX() {
        return LENNARD_JONES_CUTOFF_MAX;
    }

    public void setLENNARD_JONES_CUTOFF_MAX(float lENNARD_JONES_CUTOFF_MAX) {
        LENNARD_JONES_CUTOFF_MAX = lENNARD_JONES_CUTOFF_MAX;
    }

    public float getLENNARD_JONES_CUTOFF_MIN() {
        return LENNARD_JONES_CUTOFF_MIN;
    }

    public void setLENNARD_JONES_CUTOFF_MIN(float lENNARD_JONES_CUTOFF_MIN) {
        LENNARD_JONES_CUTOFF_MIN = lENNARD_JONES_CUTOFF_MIN;
    }

    public boolean isUSE_CUTOFF_PROBABILITY_IN_SHAPES() {
        return USE_CUTOFF_PROBABILITY_IN_SHAPES;
    }

    public void setUSE_CUTOFF_PROBABILITY_IN_SHAPES(
            boolean uSE_CUTOFF_PROBABILITY_IN_SHAPES) {
        USE_CUTOFF_PROBABILITY_IN_SHAPES = uSE_CUTOFF_PROBABILITY_IN_SHAPES;
    }

    public float getCUTOFF_MIN_PROBABILITY_IN_SHAPES() {
        return CUTOFF_MIN_PROBABILITY_IN_SHAPES;
    }

    public void setCUTOFF_MIN_PROBABILITY_IN_SHAPES(
            float cUTOFF_MIN_PROBABILITY_IN_SHAPES) {
        CUTOFF_MIN_PROBABILITY_IN_SHAPES = cUTOFF_MIN_PROBABILITY_IN_SHAPES;
    }

    public float getDISTANCE_MIN_FOR_EXTENDED_PAIRING_FROM_SEED() {
        return DISTANCE_MIN_FOR_EXTENDED_PAIRING_FROM_SEED;
    }

    public void setDISTANCE_MIN_FOR_EXTENDED_PAIRING_FROM_SEED(
            float dISTANCE_MIN_FOR_EXTENDED_PAIRING_FROM_SEED) {
        DISTANCE_MIN_FOR_EXTENDED_PAIRING_FROM_SEED = dISTANCE_MIN_FOR_EXTENDED_PAIRING_FROM_SEED;
    }

    public float getANGLE_DIFF_TOLERANCE() {
        return ANGLE_DIFF_TOLERANCE;
    }

    public void setANGLE_DIFF_TOLERANCE(float aNGLE_DIFF_TOLERANCE) {
        ANGLE_DIFF_TOLERANCE = aNGLE_DIFF_TOLERANCE;
    }

    public float getEDGE_DIFF_TOLERANCE() {
        return EDGE_DIFF_TOLERANCE;
    }

    public void setEDGE_DIFF_TOLERANCE(float eDGE_DIFF_TOLERANCE) {
        EDGE_DIFF_TOLERANCE = eDGE_DIFF_TOLERANCE;
    }

    public float getMIN_DISTANCE_TO_BE_NEIBHOR() {
        return MIN_DISTANCE_TO_BE_NEIBHOR;
    }

    public void setMIN_DISTANCE_TO_BE_NEIBHOR(float mIN_DISTANCE_TO_BE_NEIBHOR) {
        MIN_DISTANCE_TO_BE_NEIBHOR = mIN_DISTANCE_TO_BE_NEIBHOR;
    }

    public float getCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM() {
        return CELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM;
    }

    public void setCELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM(
            float cELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM) {
        CELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM = cELL_DIMENSION_OF_THE_PROBABILITY_MAP_ANGSTROM;
    }

    public float getEXTRA_DISTANCE_OUT_FORALLBOX() {
        return EXTRA_DISTANCE_OUT_FORALLBOX;
    }

    public void setEXTRA_DISTANCE_OUT_FORALLBOX(float eXTRA_DISTANCE_OUT_FORALLBOX) {
        EXTRA_DISTANCE_OUT_FORALLBOX = eXTRA_DISTANCE_OUT_FORALLBOX;
    }

    public int getCUTOFF_COUNT_OF_HYDROPHOBIC_ATOM_SURRONDING_HBOND() {
        return CUTOFF_COUNT_OF_HYDROPHOBIC_ATOM_SURRONDING_HBOND;
    }

    public void setCUTOFF_COUNT_OF_HYDROPHOBIC_ATOM_SURRONDING_HBOND(
            int cUTOFF_COUNT_OF_HYDROPHOBIC_ATOM_SURRONDING_HBOND) {
        CUTOFF_COUNT_OF_HYDROPHOBIC_ATOM_SURRONDING_HBOND = cUTOFF_COUNT_OF_HYDROPHOBIC_ATOM_SURRONDING_HBOND;
    }

    public float getCUTOFF_DISTANCE_FORHYDROPHOBIC_AROUND_HBOND() {
        return CUTOFF_DISTANCE_FORHYDROPHOBIC_AROUND_HBOND;
    }

    public void setCUTOFF_DISTANCE_FORHYDROPHOBIC_AROUND_HBOND(
            float cUTOFF_DISTANCE_FORHYDROPHOBIC_AROUND_HBOND) {
        CUTOFF_DISTANCE_FORHYDROPHOBIC_AROUND_HBOND = cUTOFF_DISTANCE_FORHYDROPHOBIC_AROUND_HBOND;
    }

    public float getDISTANCE_FROM_PEPTIDE_TO_WHICH_INTERACTINGPROTEIN_IS_CONSIDERED() {
        return DISTANCE_FROM_PEPTIDE_TO_WHICH_INTERACTINGPROTEIN_IS_CONSIDERED;
    }

    public void setDISTANCE_FROM_PEPTIDE_TO_WHICH_INTERACTINGPROTEIN_IS_CONSIDERED(
            float dISTANCE_FROM_PEPTIDE_TO_WHICH_INTERACTINGPROTEIN_IS_CONSIDERED) {
        DISTANCE_FROM_PEPTIDE_TO_WHICH_INTERACTINGPROTEIN_IS_CONSIDERED = dISTANCE_FROM_PEPTIDE_TO_WHICH_INTERACTINGPROTEIN_IS_CONSIDERED;
    }

    public float getWEIGHT_HBOND_DONNOR() {
        return WEIGHT_HBOND_DONNOR;
    }

    public void setWEIGHT_HBOND_DONNOR(float wEIGHT_HBOND_DONNOR) {
        WEIGHT_HBOND_DONNOR = wEIGHT_HBOND_DONNOR;
    }

    public float getWEIGHT_HBOND_ACCEPTOR() {
        return WEIGHT_HBOND_ACCEPTOR;
    }

    public void setWEIGHT_HBOND_ACCEPTOR(float wEIGHT_HBOND_ACCEPTOR) {
        WEIGHT_HBOND_ACCEPTOR = wEIGHT_HBOND_ACCEPTOR;
    }

    public float getWEIGHT_DEHYDRON() {
        return WEIGHT_DEHYDRON;
    }

    public void setWEIGHT_DEHYDRON(float wEIGHT_DEHYDRON) {
        WEIGHT_DEHYDRON = wEIGHT_DEHYDRON;
    }

    public float getWEIGHT_DIFFERENCE_IN_PROBABILITIES_IN_PAIRED_POINTS() {
        return WEIGHT_DIFFERENCE_IN_PROBABILITIES_IN_PAIRED_POINTS;
    }

    public void setWEIGHT_DIFFERENCE_IN_PROBABILITIES_IN_PAIRED_POINTS(
            float wEIGHT_DIFFERENCE_IN_PROBABILITIES_IN_PAIRED_POINTS) {
        WEIGHT_DIFFERENCE_IN_PROBABILITIES_IN_PAIRED_POINTS = wEIGHT_DIFFERENCE_IN_PROBABILITIES_IN_PAIRED_POINTS;
    }

    public float getWEIGHT_DIFFERENCE_IN_PROBA_IN_DISTANCES_BETWEEN_PAIRED_POINTS() {
        return WEIGHT_DIFFERENCE_IN_PROBA_IN_DISTANCES_BETWEEN_PAIRED_POINTS;
    }

    public void setWEIGHT_DIFFERENCE_IN_PROBA_IN_DISTANCES_BETWEEN_PAIRED_POINTS(
            float wEIGHT_DIFFERENCE_IN_PROBA_IN_DISTANCES_BETWEEN_PAIRED_POINTS) {
        WEIGHT_DIFFERENCE_IN_PROBA_IN_DISTANCES_BETWEEN_PAIRED_POINTS = wEIGHT_DIFFERENCE_IN_PROBA_IN_DISTANCES_BETWEEN_PAIRED_POINTS;
    }

    public float getWEIGHT_DIFFERENCE_TO_OUTSIDE() {
        return WEIGHT_DIFFERENCE_TO_OUTSIDE;
    }

    public void setWEIGHT_DIFFERENCE_TO_OUTSIDE(float wEIGHT_DIFFERENCE_TO_OUTSIDE) {
        WEIGHT_DIFFERENCE_TO_OUTSIDE = wEIGHT_DIFFERENCE_TO_OUTSIDE;
    }

    public float getWEIGHT_DIFFERENCE_IN_CHARGES_BETWEEN_PAIRED_POINTS() {
        return WEIGHT_DIFFERENCE_IN_CHARGES_BETWEEN_PAIRED_POINTS;
    }

    public void setWEIGHT_DIFFERENCE_IN_CHARGES_BETWEEN_PAIRED_POINTS(
            float wEIGHT_DIFFERENCE_IN_CHARGES_BETWEEN_PAIRED_POINTS) {
        WEIGHT_DIFFERENCE_IN_CHARGES_BETWEEN_PAIRED_POINTS = wEIGHT_DIFFERENCE_IN_CHARGES_BETWEEN_PAIRED_POINTS;
    }

    public float getWEIGHT_DIFFERENCE_IN_HYDROPHOBICITY_BETWEEN_PAIRED_POINTS() {
        return WEIGHT_DIFFERENCE_IN_HYDROPHOBICITY_BETWEEN_PAIRED_POINTS;
    }

    public void setWEIGHT_DIFFERENCE_IN_HYDROPHOBICITY_BETWEEN_PAIRED_POINTS(
            float wEIGHT_DIFFERENCE_IN_HYDROPHOBICITY_BETWEEN_PAIRED_POINTS) {
        WEIGHT_DIFFERENCE_IN_HYDROPHOBICITY_BETWEEN_PAIRED_POINTS = wEIGHT_DIFFERENCE_IN_HYDROPHOBICITY_BETWEEN_PAIRED_POINTS;
    }

    public float getWEIGHT_UNPAIRED_POINT_IN_SMALLEST_MAP() {
        return WEIGHT_UNPAIRED_POINT_IN_SMALLEST_MAP;
    }

    public void setWEIGHT_UNPAIRED_POINT_IN_SMALLEST_MAP(
            float wEIGHT_UNPAIRED_POINT_IN_SMALLEST_MAP) {
        WEIGHT_UNPAIRED_POINT_IN_SMALLEST_MAP = wEIGHT_UNPAIRED_POINT_IN_SMALLEST_MAP;
    }

    public float getWEIGHT_DIFFERENCE_AROMATICRING() {
        return WEIGHT_DIFFERENCE_AROMATICRING;
    }

    public void setWEIGHT_DIFFERENCE_AROMATICRING(
            float wEIGHT_DIFFERENCE_AROMATICRING) {
        WEIGHT_DIFFERENCE_AROMATICRING = wEIGHT_DIFFERENCE_AROMATICRING;
    }

    public int getDEHYDRON_CUTOFF_COUNT_OF_HYDROPHOBIC_ATOM_SURRONDING_HBOND() {
        return DEHYDRON_CUTOFF_COUNT_OF_HYDROPHOBIC_ATOM_SURRONDING_HBOND;
    }

    public void setDEHYDRON_CUTOFF_COUNT_OF_HYDROPHOBIC_ATOM_SURRONDING_HBOND(
            int dEHYDRON_CUTOFF_COUNT_OF_HYDROPHOBIC_ATOM_SURRONDING_HBOND) {
        DEHYDRON_CUTOFF_COUNT_OF_HYDROPHOBIC_ATOM_SURRONDING_HBOND = dEHYDRON_CUTOFF_COUNT_OF_HYDROPHOBIC_ATOM_SURRONDING_HBOND;
    }

    public float getTHRESHOLD_DISTANCE_TO_KEEP_NEIGHBORING_NONE_STRIKING_PROPERTY() {
        return THRESHOLD_DISTANCE_TO_KEEP_NEIGHBORING_NONE_STRIKING_PROPERTY;
    }

    public void setTHRESHOLD_DISTANCE_TO_KEEP_NEIGHBORING_NONE_STRIKING_PROPERTY(
            float tHRESHOLD_DISTANCE_TO_KEEP_NEIGHBORING_NONE_STRIKING_PROPERTY) {
        THRESHOLD_DISTANCE_TO_KEEP_NEIGHBORING_NONE_STRIKING_PROPERTY = tHRESHOLD_DISTANCE_TO_KEEP_NEIGHBORING_NONE_STRIKING_PROPERTY;
    }

    public float getDISTANCE_FROM_PEPTIDE_TO_WHICH_INTERACTINGPROTEIN_IS_SHORTENED() {
        return DISTANCE_FROM_PEPTIDE_TO_WHICH_INTERACTINGPROTEIN_IS_SHORTENED;
    }

    public void setDISTANCE_FROM_PEPTIDE_TO_WHICH_INTERACTINGPROTEIN_IS_SHORTENED(
            float dISTANCE_FROM_PEPTIDE_TO_WHICH_INTERACTINGPROTEIN_IS_SHORTENED) {
        DISTANCE_FROM_PEPTIDE_TO_WHICH_INTERACTINGPROTEIN_IS_SHORTENED = dISTANCE_FROM_PEPTIDE_TO_WHICH_INTERACTINGPROTEIN_IS_SHORTENED;
    }

    public int getMAX_COUNT_NONE_PROPERTY_IN_TRIANGLE() {
        return MAX_COUNT_NONE_PROPERTY_IN_TRIANGLE;
    }

    public void setMAX_COUNT_NONE_PROPERTY_IN_TRIANGLE(
            int mAX_COUNT_NONE_PROPERTY_IN_TRIANGLE) {
        MAX_COUNT_NONE_PROPERTY_IN_TRIANGLE = mAX_COUNT_NONE_PROPERTY_IN_TRIANGLE;
    }

    public float getANGLE_MAX() {
        return ANGLE_MAX;
    }

    public void setANGLE_MAX(float aNGLE_MAX) {
        ANGLE_MAX = aNGLE_MAX;
    }

    public float getANGLE_MIN() {
        return ANGLE_MIN;
    }

    public void setANGLE_MIN(float aNGLE_MIN) {
        ANGLE_MIN = aNGLE_MIN;
    }

    public int getCOUNT_OF_INCREMENT_ANGLE() {
        return COUNT_OF_INCREMENT_ANGLE;
    }

    public void setCOUNT_OF_INCREMENT_ANGLE(int cOUNT_OF_INCREMENT_ANGLE) {
        COUNT_OF_INCREMENT_ANGLE = cOUNT_OF_INCREMENT_ANGLE;
    }

    public float getFIRST_RADIUS_INCREMENT_IN_SHAPE_REDUCTION() {
        return FIRST_RADIUS_INCREMENT_IN_SHAPE_REDUCTION;
    }

    public void setFIRST_RADIUS_INCREMENT_IN_SHAPE_REDUCTION(
            float fIRST_RADIUS_INCREMENT_IN_SHAPE_REDUCTION) {
        FIRST_RADIUS_INCREMENT_IN_SHAPE_REDUCTION = fIRST_RADIUS_INCREMENT_IN_SHAPE_REDUCTION;
    }

    public int getCOUNT_OF_RESIDUES_IGNORED_IN_SHAPE_BUILDING_BEFORE_AND_AFTER_PEPTIDE() {
        return COUNT_OF_RESIDUES_IGNORED_IN_SHAPE_BUILDING_BEFORE_AND_AFTER_PEPTIDE;
    }

    public int getDOCKING_PEPTIDES_SPLITTING_SEQUENCE_LENGTH() {
        return DOCKING_PEPTIDES_SPLITTING_SEQUENCE_LENGTH;
    }

    public void setFRACTION_NEEDED_ON_QUERY(float FRACTION_NEEDED_ON_QUERY) {
        this.FRACTION_NEEDED_ON_QUERY = FRACTION_NEEDED_ON_QUERY;
    }

    public float getFRACTION_NEEDED_ON_QUERY() {
        return FRACTION_NEEDED_ON_QUERY;
    }
}

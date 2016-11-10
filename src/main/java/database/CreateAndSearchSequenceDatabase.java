package database;

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
import io.BiojavaReader;
import io.ExceptionInIOPackage;
import io.IOTools;
import mystructure.*;
import org.biojava.nbio.structure.Structure;
import parameters.AlgoParameters;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * Created by Fabrice on 29/09/16.
 */
public class CreateAndSearchSequenceDatabase {

    private Connection connexion;
    private AlgoParameters algoParameters;

    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public CreateAndSearchSequenceDatabase() {

        this.connexion = DatabaseTools.getConnection();
    }


    // -------------------------------------------------------------------
    // Public & Interface Methods
    // -------------------------------------------------------------------
    public void shutdownDb() {

        DatabaseTools.shutdown();
    }


    public void updateDatabase(AlgoParameters algoParameters) {

        this.algoParameters = algoParameters;

        Map<String, List<Path>> indexPDBFileInFolder = IOTools.indexPDBFileInFolder(algoParameters.getPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER());

        Structures:
        for (Map.Entry<String, List<Path>> entry : indexPDBFileInFolder.entrySet()) {
            String fourLetterCode = entry.getKey();
            try {

                // Search if already in DB
                Statement stmt = connexion.createStatement();
                String findEntry = "SELECT * from sequence WHERE fourLettercode = '" + fourLetterCode + "'";


                ResultSet resultFindEntry = stmt.executeQuery(findEntry);
                stmt.close();
                int foundEntriesCount = 0;

                if (resultFindEntry.next()) {
                    foundEntriesCount += 1;
                }

                if (foundEntriesCount != 0) {
                    continue Structures;
                }

            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                System.out.println("SQL Exception when searching for previous entries with a given PDB code : " + fourLetterCode);
                // exception
                //
            }
            generateMyStructureAndstoreSequenceInDB(entry.getKey());
        }
    }


    public void buildDatabase(AlgoParameters algoParameters) {

        this.algoParameters = algoParameters;

        DatabaseTools.createDBandTableSequence(connexion);
        Map<String, List<Path>> indexPDBFileInFolder = IOTools.indexPDBFileInFolder(algoParameters.getPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER());

        BiojavaReader biojavaReader = new BiojavaReader();
        for (Map.Entry<String, List<Path>> entry : indexPDBFileInFolder.entrySet()) {

            generateMyStructureAndstoreSequenceInDB(entry.getKey());

        }
        System.out.println("Sequence database is created");
    }


    public String returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase(String fourLetterCode, String chainName) {

        return DatabaseTools.returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase(connexion, fourLetterCode, chainName);
    }


    //-------------------------------------------------------------
    // Implementation
    //-------------------------------------------------------------
    private void generateMyStructureAndstoreSequenceInDB(String fourLetterCode) {


        String fourLetterCodeToLowerCase = fourLetterCode.toLowerCase();
        MyStructureIfc myStructure = IOTools.getMyStructureIfc(algoParameters, fourLetterCodeToLowerCase.toCharArray());
        if (myStructure == null) {
            return;
        }

        MyChainIfc[] chainsForShapeBuilding = myStructure.getAllChainsRelevantForShapeBuilding();
        Chains:
        for (MyChainIfc chain : chainsForShapeBuilding) {

            MyMonomerType monomerType = MyMonomerType.getEnumType(chain.getMyMonomers()[0].getType());
            char[] chainType = "  ".toCharArray();
            if (monomerType.equals(MyMonomerType.AMINOACID)) {
                chainType = "AA".toCharArray();
            }
            if (monomerType.equals(MyMonomerType.NUCLEOTIDE)) {
                chainType = "NU".toCharArray();
            }
            if (monomerType.equals(MyMonomerType.HETATM)) {
                continue Chains;
            }
            char[] chainName = chain.getChainId();
            String sequence = SequenceTools.generateSequence(chain);

            if (sequence.length() > DatabaseTools.maxCharInVarchar) {
                String truncatedSequence = sequence.substring(0, DatabaseTools.maxCharInVarchar);
                sequence = truncatedSequence;
            }

            try {
                String insertTableSQL = "INSERT INTO sequence"
                        + "(fourLettercode, chainId, chainType, sequenceString) VALUES"
                        + "(?,?,?,?)";
                PreparedStatement preparedStatement = connexion.prepareStatement(insertTableSQL);
                preparedStatement.setString(1, fourLetterCode);
                preparedStatement.setString(2, String.valueOf(chainName));
                preparedStatement.setString(3, String.valueOf(chainType));
                preparedStatement.setString(4, sequence);
                preparedStatement.close();
                int ok = preparedStatement.executeUpdate();

                System.out.println(ok + " raw created " + fourLetterCode + "  " + String.valueOf(chainName) + "  " + String.valueOf(chainType)); // + " " + sequence);

            } catch (SQLException e1) {
                System.out.println("Failed to enter entry in sequence table ");
            }
        }
    }
}
package database;

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
import io.BiojavaReader;
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
    private int maxCharInVarchar = 30000;
    private Path pathToMMCIFFolder;
    private Path pathToChemCompFolder;
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



    public void updateDatabase(Path pathToMMCIFFolder, Path pathToChemCompFolder, AlgoParameters algoParameters) {

        this.pathToMMCIFFolder = pathToMMCIFFolder;
        this.pathToChemCompFolder = pathToChemCompFolder;
        this.algoParameters = algoParameters;

        Map<String, List<Path>> indexPDBFileInFolder = IOTools.indexPDBFileInFolder(pathToMMCIFFolder.toString());

        BiojavaReader biojavaReader = new BiojavaReader();
        Structures:
        for (Map.Entry<String, List<Path>> entry : indexPDBFileInFolder.entrySet()) {
            String fourLetterCode = entry.getKey();
            try {

                // Search if already in DB
                Statement stmt = connexion.createStatement();
                String findEntry = "SELECT * from sequence WHERE fourLettercode = '" + fourLetterCode + "'";


                ResultSet resultFindEntry = stmt.executeQuery(findEntry);
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
            generateMyStructureAndstoreSequenceInDB(entry.getValue(), biojavaReader);
        }
    }


    public void buildDatabase(Path pathToMMCIFFolder, Path pathToChemCompFolder, AlgoParameters algoParameters) {
        this.pathToMMCIFFolder = pathToMMCIFFolder;
        this.pathToChemCompFolder = pathToChemCompFolder;
        this.algoParameters = algoParameters;

        createDBandTableSequence();
        Map<String, List<Path>> indexPDBFileInFolder = IOTools.indexPDBFileInFolder(pathToMMCIFFolder.toString());

        BiojavaReader biojavaReader = new BiojavaReader();
        for (Map.Entry<String, List<Path>> entry : indexPDBFileInFolder.entrySet()) {

            generateMyStructureAndstoreSequenceInDB(entry.getValue(), biojavaReader);

        }
        System.out.println("Sequence database is created");
    }



    public String returnSequenceInDbifFourLetterCodeAndChainfoundInDatabase(String fourLetterCode, String chainName) {

        String sequenceInDb = null;
        try {
            Statement stmt = connexion.createStatement();
            String findEntry = "SELECT * from sequence WHERE fourLettercode = '" + fourLetterCode + "' and chainId = '" + chainName + "'";
            ResultSet resultFindEntry = stmt.executeQuery(findEntry);
            int foundEntriesCount = 0;
            String fourLetterCodeFromDB;
            String chainIdFromDB;
            if (resultFindEntry.next()) {
                foundEntriesCount += 1;

                fourLetterCodeFromDB = resultFindEntry.getString(1);
                chainIdFromDB = resultFindEntry.getString(2);
                sequenceInDb = resultFindEntry.getString(4);
            }

            if (foundEntriesCount != 1) {
                System.out.println("problem isFourLetterCodeAndChainfoundInDatabase " + fourLetterCode + "  " + chainName + "  " + foundEntriesCount);
                return null;
            }
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return null;
        }
        return sequenceInDb;
    }


    //-------------------------------------------------------------
    // Implementation
    //-------------------------------------------------------------
    private void generateMyStructureAndstoreSequenceInDB(List<Path> paths, BiojavaReader biojavaReader) {

        for (Path path : paths) {
            //System.out.println(path.toString());

            MyStructureIfc myStructure = null;
            try {
                myStructure = getMyStructure(biojavaReader, path);
            } catch (IOException | ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat exception) {
                continue;
            }

            char[] fourLetterCode = myStructure.getFourLetterCode();

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

                if (sequence.length() > maxCharInVarchar) {
                    String truncatedSequence = sequence.substring(0, maxCharInVarchar);
                    sequence = truncatedSequence;
                }

                try {
                    String insertTableSQL = "INSERT INTO sequence"
                            + "(fourLettercode, chainId, chainType, sequenceString) VALUES"
                            + "(?,?,?,?)";
                    PreparedStatement preparedStatement = connexion.prepareStatement(insertTableSQL);
                    preparedStatement.setString(1, String.valueOf(fourLetterCode));
                    preparedStatement.setString(2, String.valueOf(chainName));
                    preparedStatement.setString(3, String.valueOf(chainType));
                    preparedStatement.setString(4, sequence);

                    int ok = preparedStatement.executeUpdate();
                    System.out.println(ok + " raw created " + String.valueOf(fourLetterCode) + "  " + String.valueOf(chainName) + "  " + String.valueOf(chainType)); // + " " + sequence);

                } catch (SQLException e1) {
                    System.out.println("Failed to enter entry in sequence table ");
                }
            }
        }
    }


    private MyStructureIfc getMyStructure(BiojavaReader biojavaReader, Path path) throws IOException, ReadingStructurefileException, ExceptionInMyStructurePackage, ExceptionInConvertFormat {
        Structure mmcifStructure = biojavaReader.read(path, pathToChemCompFolder.toString());

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc myStructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure, EnumMyReaderBiojava.BioJava_MMCIFF);
        return myStructure;
    }


    private void createDBandTableSequence() {

        try {
            Statement stmt = connexion.createStatement();
            String sql = "DROP TABLE sequence";
            stmt.execute(sql);
            System.out.println("Drop all tables from myDB !");
            stmt.close();
        } catch (SQLException e1) {
            System.out.println("Table sequence is not existing so cannot be droped");
        }

        // check if table exists if not create it
        try {
            Statement stmt = connexion.createStatement();
            //String createTableSql = "CREATE TABLE " + "sequence" + " (fourLettercode varchar(4), chainId varchar(1), sequenceString varchar(" + maxCharInVarchar + "), lastmodificationtime timestamp )";
            String createTableSql = "CREATE TABLE " + "sequence" + " (fourLettercode varchar(4), chainId varchar(2), chainType varchar(2), "
                    + "sequenceString varchar(" + maxCharInVarchar + "), PRIMARY KEY (fourLettercode, chainId) ) ";
            //System.out.println(createTableSql);
            stmt.executeUpdate(createTableSql);
            System.out.println("created table sequence in myDB !");
            stmt.close();
        } catch (SQLException e1) {
            System.out.println("Table sequence already exists in myDB !");
        }
    }
}
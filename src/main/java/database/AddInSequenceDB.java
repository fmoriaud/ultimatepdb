package database;

import io.IOTools;
import mystructure.MyChainIfc;
import mystructure.MyMonomerType;
import mystructure.MyStructureIfc;
import parameters.AlgoParameters;

import java.sql.*;

/**
 * Created by Fabrice on 06/11/16.
 */
public class AddInSequenceDB implements DoMyDbTaskIfc {

    private AlgoParameters algoParameters;
    private String fourLetterCode;
    private boolean override;

    public AddInSequenceDB(AlgoParameters algoParameters, String fourLetterCode, boolean override) {
        this.algoParameters = algoParameters;
        this.fourLetterCode = fourLetterCode;
        this.override = override;
    }


    @Override
    public boolean doAndReturnSuccessValue(Connection connexion) {

        boolean alreadyFound = isFourLetterCodeAlreadyFoundInDB(connexion, fourLetterCode);
        if (alreadyFound == true && override == false) {
            return false;
        }

        if (alreadyFound == true && override == true) {
            removeAllEntriesForThisFourLetterCode(connexion, fourLetterCode);
        }

        String fourLetterCodeLowerCase = fourLetterCode.toLowerCase();
        MyStructureIfc myStructure = IOTools.getMyStructureIfc(algoParameters, fourLetterCodeLowerCase.toCharArray());

        if (myStructure == null) {
            return false;
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
                preparedStatement.setString(1, String.valueOf(fourLetterCode));
                preparedStatement.setString(2, String.valueOf(chainName));
                preparedStatement.setString(3, String.valueOf(chainType));
                preparedStatement.setString(4, sequence);

                int ok = preparedStatement.executeUpdate();
                preparedStatement.close();
                System.out.println(ok + " raw created " + String.valueOf(fourLetterCode) + "  " + String.valueOf(chainName) + "  " + String.valueOf(chainType)); // + " " + sequence);

            } catch (SQLException e1) {
                System.out.println("Failed to enter entry in sequence table ");
                return false;
            }
        }

        return true;
    }


    private boolean removeAllEntriesForThisFourLetterCode(Connection connexion, String fourLetterCode) {

        Statement stmt = null;
        try {
            stmt = connexion.createStatement();
        } catch (SQLException e) {
            return false;
        }
        String deleteEntry = "DELETE * from sequence WHERE fourLettercode = '" + fourLetterCode + "'";

        ResultSet value = null;
        try {
            value = stmt.executeQuery(deleteEntry);
            stmt.close();
        } catch (SQLException e) {
            try {
                stmt.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            return false;
        }
        return true;
    }


    private boolean isFourLetterCodeAlreadyFoundInDB(Connection connexion, String fourLetterCode) {

        Statement stmt = null;
        try {
            stmt = connexion.createStatement();
        } catch (SQLException e) {
            return false;
        }
        String findEntry = "SELECT * from sequence WHERE fourLettercode = '" + fourLetterCode + "'";

        ResultSet resultFindEntry = null;
        try {
            resultFindEntry = stmt.executeQuery(findEntry);
        } catch (SQLException e) {
            return false;
        }
        int foundEntriesCount = 0;

        try {
            if (resultFindEntry.next()) {
                foundEntriesCount += 1;
            }
        } catch (SQLException e) {
            try {
                stmt.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            return false;
        }

        if (foundEntriesCount != 0) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}

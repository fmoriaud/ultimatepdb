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

        return DatabaseTools.addInSequenceDB(connexion, override, fourLetterCode, algoParameters);

    }
}

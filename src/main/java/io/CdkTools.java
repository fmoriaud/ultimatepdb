package io;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.MDLV3000Reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


/**
 * Created by Fabrice on 07/09/16.
 */
public class CdkTools {


    public static IAtomContainer readV3000molFile(String path) {

        IAtomContainer mol = null;
        try {
            MDLV3000Reader reader = new MDLV3000Reader(new FileInputStream(path));
            mol = reader.readMolecule(DefaultChemObjectBuilder.getInstance());
        } catch (CDKException | FileNotFoundException e) {
            e.printStackTrace();
        }
        return mol;
    }

}

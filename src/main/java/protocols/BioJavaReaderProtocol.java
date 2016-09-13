package protocols;

import io.BiojavaReader;
import io.BiojavaReaderIfc;
import org.biojava.nbio.structure.Structure;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Fabrice on 13/09/16.
 */
public class BioJavaReaderProtocol implements BiojavaReaderIfc {


    @Override
    public Structure read(Path pathToFile, String pathToChemcompFolder) throws IOException {

        Structure mmcif = null;
        try{
            BiojavaReader reader = new BiojavaReader();
            mmcif = reader.read(pathToFile, pathToChemcompFolder);
        } catch (Exception e){
            System.out.println("Read failed");
        }

        return mmcif;
    }


    @Override
    public Structure readFromPDBFolder(String fourLetterCode, String pathToDividedPDBFolder, String pathToChemcompFolder) throws IOException {
        return null;
    }
}

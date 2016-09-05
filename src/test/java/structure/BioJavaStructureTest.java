package structure;

import io.BiojavaReaderTest;
import org.biojava.bio.structure.*;
//import org.biojava.nbio.structure.secstruc.SecStrucInfo;
import org.junit.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 01/09/16.
 */
public class BioJavaStructureTest {

    @Test
    public void testMmcifCheckSecondaryStructureFromFile() {

        URL url = BiojavaReaderTest.class.getClassLoader().getResource("1di9.cif.gz");

        Path path = null;
        try {
            path = Paths.get(url.toURI());
        } catch (URISyntaxException e1) {
            assertTrue(false);
        }
        Structure cifStructure = null;

            cifStructure = null ; //IOTools.readMMCIFFile(path);


        Chain chain = cifStructure.getChain(0);
        List<Group> listGroupsAmino = chain.getAtomGroups(GroupType.AMINOACID);
        assertTrue(listGroupsAmino.size() == 348);

        for (Group currentGroup: listGroupsAmino){

            AminoAcid aa = (AminoAcid)currentGroup;

           // SecStrucInfo readsecStruc = (SecStrucInfo) aa.getProperty(Group.SEC_STRUC);
            //assertTrue(readsecStruc != null);
            //SecStrucType secType = readsecStruc.getType();
        }
    }
}

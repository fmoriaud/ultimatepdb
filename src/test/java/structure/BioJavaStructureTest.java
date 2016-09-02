package structure;

import io.ExceptionInIOPackage;
import io.IOTools;
import io.ContentOfReadMmCifFileReadFromResourcesTest;
import org.biojava.nbio.structure.*;
//import org.biojava.nbio.structure.secstruc.SecStrucInfo;
import org.biojava.nbio.structure.secstruc.SecStrucType;
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

        URL url = ContentOfReadMmCifFileReadFromResourcesTest.class.getClassLoader().getResource("1di9.cif.gz");

        Path path = null;
        try {
            path = Paths.get(url.toURI());
        } catch (URISyntaxException e1) {
            assertTrue(false);
        }
        Structure cifStructure = null;
        try {
            cifStructure = IOTools.readMMCIFFile(path);
        } catch (ExceptionInIOPackage e) {
            assertTrue(false);
        }

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

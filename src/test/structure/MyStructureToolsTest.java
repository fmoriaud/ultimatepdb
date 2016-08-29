import org.junit.Test;
import static org.junit.Assert.assertTrue;

import structure.MyStructureTools;
import org.biojava.nbio.structure.GroupType;
import structure.MyMonomerType;
/**
 * Created by Fabrice on 29/08/16.
 */
public class MyStructureToolsTest {


    @Test
    public void testConversion() throws Exception {

        assertTrue(MyStructureTools.convertType(GroupType.AMINOACID).equals(MyMonomerType.AMINOACID));
        assertTrue(MyStructureTools.convertType(GroupType.HETATM).equals(MyMonomerType.HETATM));
        assertTrue(MyStructureTools.convertType(GroupType.NUCLEOTIDE).equals(MyMonomerType.NUCLEOTIDE));
    }
}

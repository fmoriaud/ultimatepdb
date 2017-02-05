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
package myjmol;

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
import io.BiojavaReader;
import io.ExceptionInIOPackage;
import io.Tools;
import jmolgui.UltiJmol1462;
import mystructure.ExceptionInMyStructurePackage;
import mystructure.MyStructureIfc;
import mystructure.ReadingStructurefileException;
import org.biojava.nbio.structure.Structure;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 31/08/16.
 */
public class MyJmolTest {


    // method to be tested
// ultiJmol.viewerForUlti.areHydrogenAdded()

    private String pathToPDBFolder;

    @Rule
    public TemporaryFolder testPDBFolder = new TemporaryFolder();

    @Before
    public void createPath() {

        try {
            File file = testPDBFolder.newFile("empty");
            pathToPDBFolder = file.getParentFile().getAbsolutePath();
        } catch (IOException e) {

        }
    }

    @Test
    public void testOpenStringInlineV3000Jmol() throws ParsingConfigFileException, IOException, ReadingStructurefileException, ExceptionInMyStructurePackage, ExceptionInConvertFormat {

        String fourLetterCode = "1di9";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFolders();

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc myStructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure);

        UltiJmol1462 ultiJmol = new UltiJmol1462();
        String myStructureV3000 = myStructure.toV3000();
        try {
            Thread.sleep(4 * 1000);
        } catch (InterruptedException e) {

        }
        assertTrue(myStructureV3000.contains("M  V30 COUNTS 2798 2863 0 0 0"));
        assertTrue(myStructureV3000.contains("M  V30 1 N 19.12 41.85 25.992  0"));
        assertTrue(myStructureV3000.contains("M  V30 2863 1 2798 2797"));
        assertTrue(myStructureV3000.contains("M  END"));

        ultiJmol.openStringInline(myStructureV3000);
        try {
            Thread.sleep(4 * 1000);
        } catch (InterruptedException e) {

        }
        String readV3000 = ultiJmol.jmolPanel.getViewer().getData("*", "V3000");
        assertTrue(readV3000.contains("M  V30 COUNTS 2798 2863 0 0 0"));
        assertTrue(readV3000.contains("M  V30 1 N     19.12000     41.85000     25.99200 0"));
        assertTrue(myStructureV3000.contains("M  V30 2863 1 2798 2797"));
        assertTrue(myStructureV3000.contains("M  END"));

        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e) {

        }
        ultiJmol.evalString("zap");
        try {
            Thread.sleep(4 * 1000);
        } catch (InterruptedException e) {

        }
        // that throws an excepion. Don't know how to fix it
        ultiJmol.frame.dispose();
    }
}
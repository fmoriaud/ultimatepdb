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
package mystructure;

import convertformat.AdapterBioJavaStructure;
import convertformat.ExceptionInConvertFormat;
import hits.ExceptionInScoringUsingBioJavaJMolGUI;
import io.*;
import org.biojava.nbio.structure.Structure;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openscience.cdk.interfaces.IAtomContainer;
import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import shapeBuilder.ShapeBuildingException;
import ultiJmol1462.Protonate;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by Fabrice on 29/08/16.
 */
public class MyStructureTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();


    @Test
    public void MyStructureConstructorWithThreeChainArray() {

        MyMonomerIfc myMonomer1 = null;
        MyMonomerIfc myMonomer2 = null;
        try {
            myMonomer1 = TestTools.buildValidMyMonomer(1);
            myMonomer2 = TestTools.buildValidMyMonomer(2);
        } catch (ExceptionInMyStructurePackage e) {
        }

        MyMonomerIfc[] myMonomers1 = new MyMonomerIfc[2];
        myMonomers1[0] = myMonomer1;
        myMonomers1[1] = myMonomer2;
        MyChainIfc myChain1 = new MyChain(myMonomers1, "A".toCharArray());
        MyChainIfc[] anyChainArray = new MyChainIfc[1];
        anyChainArray[0] = myChain1;

        AlgoParameters algoParameters = new AlgoParameters();
        try {
            MyStructureIfc myStructure1 = new MyStructure(anyChainArray, anyChainArray, anyChainArray, ExpTechniquesEnum.UNDEFINED, algoParameters);
        } catch (ExceptionInMyStructurePackage e) {
            assertTrue(false);
        }

        // one null MyChain[] throws an exception
        try {
            MyStructureIfc myStructure1 = new MyStructure(null, anyChainArray, anyChainArray, ExpTechniquesEnum.UNDEFINED, algoParameters);
        } catch (ExceptionInMyStructurePackage e) {
            assertTrue(true);
        }

        // All empty chains throw exception
        MyChainIfc[] emptyChainArray = new MyChainIfc[0];
        try {
            MyStructureIfc myStructure1 = new MyStructure(emptyChainArray, emptyChainArray, emptyChainArray, ExpTechniquesEnum.UNDEFINED, algoParameters);
        } catch (ExceptionInMyStructurePackage e) {
            assertTrue(true);
        }
    }


    //TODO  should test that each and every atom has monomer parent: as it is mandatory for the monomer by bond
    // so needed to put this test at the end of each MyStructure constructor

    // then build here the test home
    // should not throw exception

    // change one parent to null or to something else than a monomer then see if it throws an exception


    @Test
    public void testParentConstruction() {

        MyStructureIfc myStructure = null;
        try {
            myStructure = TestTools.buildValidMyStructure(MyMonomerType.AMINOACID);
        } catch (ExceptionInMyStructurePackage e1) {
            assertTrue(false);
        }

        for (MyChainIfc chainOriginal : myStructure.getAllChains()) {
            for (MyMonomerIfc monomer : chainOriginal.getMyMonomers()) {
                assertTrue(monomer.getParent() == chainOriginal);

                for (MyAtomIfc atom : monomer.getMyAtoms()) {
                    assertTrue(atom.getParent() == monomer);
                }
            }
        }
    }


    // MyStructure integrity is not safe, one can modify everything inside without checking if it is valid
    // and without updating the neighbors... Don't know what to do.

    @Test
    public void testToV3000ProteinStructure() throws ParsingConfigFileException, IOException, ReadingStructurefileException, ExceptionInMyStructurePackage, ExceptionInConvertFormat {

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

        String myStructureV3000 = myStructure.toV3000();

        // write to a temp text file
        String pathToTempFolder = folder.getRoot().getAbsolutePath();
        String pathTOWriteV3000Molfile = pathToTempFolder + "//v3000test.mol";
        WriteTextFile.writeTextFile(myStructureV3000, pathTOWriteV3000Molfile);

        // read it with cdk and check atom and bond count

        IAtomContainer mol = CdkTools.readV3000molFile(pathTOWriteV3000Molfile);
        int atomCount = MyStructureTools.getAtomCount(myStructure);
        int bondCount = TestTools.getBondCount(myStructure);
        assertTrue(mol.getAtomCount() == atomCount);
        assertTrue(mol.getBondCount() * 2 == bondCount);
    }


    @Test
    public void testToV3000Neighors() throws ParsingConfigFileException, IOException, ReadingStructurefileException, ExceptionInMyStructurePackage {

        String fourLetterCode = "1a9u";
        BiojavaReader reader = new BiojavaReader();
        Structure mmcifStructure = null;
        try {
            mmcifStructure = reader.readFromPDBFolder(fourLetterCode, Tools.testPDBFolder, Tools.testChemcompFolder);
        } catch (IOException | ExceptionInIOPackage e) {
            assertTrue(false);
        }

        AlgoParameters algoParameters = Tools.generateModifiedAlgoParametersForTestWithTestFoldersWithUltiJmol();
        int initialCount = algoParameters.ultiJMolBuffer.getSize();

        AdapterBioJavaStructure adapterBioJavaStructure = new AdapterBioJavaStructure(algoParameters);
        MyStructureIfc mystructure = null;
        try {
            mystructure = adapterBioJavaStructure.getMyStructureAndSkipHydrogens(mmcifStructure);
        } catch (ExceptionInMyStructurePackage | ReadingStructurefileException | ExceptionInConvertFormat e) {
            assertTrue(false);
        }

        MyMonomerIfc msqLigand = mystructure.getHeteroChain("A".toCharArray()).getMyMonomerFromResidueId(800);

        MyChainIfc[] neighbors = msqLigand.getNeighboringAminoMyMonomerByRepresentativeAtomDistance();
        Cloner cloner = new Cloner(neighbors, algoParameters);
        MyStructureIfc myStructureFromNeighbors = cloner.getClone();

        Protonate protonate = new Protonate(myStructureFromNeighbors, algoParameters);
        try {
            protonate.compute();
        } catch (ExceptionInScoringUsingBioJavaJMolGUI | ShapeBuildingException exceptionInScoringUsingBioJavaJMolGUI) {
            assertTrue(false);
        }

        MyStructureIfc protonatedTarget = protonate.getProtonatedMyStructure();
        protonatedTarget.setFourLetterCode("1di9".toCharArray());

        int finalCount = algoParameters.ultiJMolBuffer.getSize();
        assertTrue(finalCount == initialCount);
        try {
            for (int i = 0; i < initialCount; i++) {
                algoParameters.ultiJMolBuffer.get().frame.dispose();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(algoParameters.ultiJMolBuffer.getSize() == 0);
    }
}

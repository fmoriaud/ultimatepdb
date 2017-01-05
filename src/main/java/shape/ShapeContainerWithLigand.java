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
package shape;

import java.util.ArrayList;
import java.util.List;

import mystructure.*;
import parameters.AlgoParameters;
import pointWithProperties.CollectionOfPointsWithPropertiesIfc;
import pointWithProperties.PointIfc;
import shapeCompare.ResultsFromEvaluateCost;

public class ShapeContainerWithLigand extends ShapeContainer implements ShapeContainerIfc {
    //-------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------
    private MyStructureIfc hetatmLigand;
    private MyMonomerIfc hetatmLigandMymonomer;

    private char[] hetatmLigandChainId;
    private char[] hetatmThreeLetterCode;
    private int occurenceId;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public ShapeContainerWithLigand(CollectionOfPointsWithPropertiesIfc shape, List<PointIfc> listPointDefininingLigandUsedToComputeShape, MyStructureIfc myStructureUsedToComputeShape, List<MyMonomerIfc> foreignMonomerToExclude, AlgoParameters algoParameters, MyStructureIfc hetatmLigand, int occurenceId) {
        super(shape, listPointDefininingLigandUsedToComputeShape, myStructureUsedToComputeShape, foreignMonomerToExclude, algoParameters);

        this.hetatmLigand = hetatmLigand;
        this.occurenceId = occurenceId;
        this.hetatmLigandMymonomer = ShapeContainerTools.findFirstMonomer(hetatmLigand);
        this.hetatmLigandChainId = ShapeContainerTools.findChainId(hetatmLigand);
        this.hetatmThreeLetterCode = ShapeContainerTools.findThreeLetterCode(hetatmLigand);
    }


    // -------------------------------------------------------------------
    // Public methods
    // -------------------------------------------------------------------
    @Override
    public String makeEndFileName() {

        return String.valueOf(fourLetterCode) + "_" + String.valueOf(hetatmLigandChainId) + "_" + String.valueOf(hetatmThreeLetterCode) + "_" + occurenceId;
    }


    @Override
    public void exportShapeToPDBFile(String fileName, AlgoParameters algoParameters) {

        List<String> contentPDBFile = new ArrayList<>();
        List<String> contentPDBFilePeptide = makeContentPDBFileHetatmLigand(algoParameters);
        contentPDBFile.addAll(contentPDBFilePeptide);

        List<String> contributioncontentPDBFileShape = makeContentPDBFileShape(fileName, algoParameters);
        contentPDBFile.addAll(contributioncontentPDBFileShape);
        writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
    }


    @Override
    public void exportRotatedShapeToPDBFile(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result) {

        List<String> contentPDBFile = new ArrayList<>();
        List<String> contentPDBFilePeptide = makeContentRotatedPDBFileHetatmLigand(algoParameters, result);
        contentPDBFile.addAll(contentPDBFilePeptide);

        List<String> contributioncontentPDBFileShape = makeContentRotatedPDBFileShape(fileName, algoParameters, result);
        contentPDBFile.addAll(contributioncontentPDBFileShape);
        writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
    }


    @Override
    public void exportShapeColoredToPDBFile(String fileName, AlgoParameters algoParameters) {

        List<String> contentPDBFile = new ArrayList<>();
        List<String> contentPDBFilePeptide = makeContentPDBFileHetatmLigand(algoParameters);
        contentPDBFile.addAll(contentPDBFilePeptide);

        List<String> contributioncontentPDBFileShape = makeContentPDBFileColoredShape(fileName, algoParameters);
        contentPDBFile.addAll(contributioncontentPDBFileShape);
        writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
    }


    @Override
    public void exportRotatedShapeColoredToPDBFile(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result) {

        List<String> contentPDBFile = new ArrayList<>();
        List<String> contentPDBFilePeptide = makeContentRotatedPDBFileHetatmLigand(algoParameters, result);
        contentPDBFile.addAll(contentPDBFilePeptide);

        List<String> contributioncontentPDBFileShape = makeContentRotatedPDBFileColoredShape(fileName, algoParameters, result);
        contentPDBFile.addAll(contributioncontentPDBFileShape);
        writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
    }


    @Override
    public void exportMiniShapeToPDBFile(String fileName, AlgoParameters algoParameters) {

        List<String> contentPDBFile = new ArrayList<>();
        List<String> contentPDBFilePeptide = makeContentPDBFileHetatmLigand(algoParameters);
        contentPDBFile.addAll(contentPDBFilePeptide);

        List<String> contributioncontentPDBFileShape = makeContentPDBFileMiniShape(fileName, algoParameters);
        contentPDBFile.addAll(contributioncontentPDBFileShape);
        writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
    }


    @Override
    public void exportRotatedMiniShapeToPDBFile(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result) {

        List<String> contentPDBFile = new ArrayList<>();
        List<String> contentPDBFilePeptide = makeContentRotatedPDBFileHetatmLigand(algoParameters, result);
        contentPDBFile.addAll(contentPDBFilePeptide);

        List<String> contributioncontentPDBFileShape = makeContentRotatedPDBFileMiniShape(fileName, algoParameters, result);
        contentPDBFile.addAll(contributioncontentPDBFileShape);
        writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
    }


    @Override
    public void exportMiniShapeColoredToPDBFile(String fileName, AlgoParameters algoParameters) {

        List<String> contentPDBFile = new ArrayList<>();
        List<String> contentPDBFilePeptide = makeContentPDBFileHetatmLigand(algoParameters);
        contentPDBFile.addAll(contentPDBFilePeptide);

        List<String> contributioncontentPDBFileShape = makeContentPDBFileColoredMiniShape(fileName, algoParameters);
        contentPDBFile.addAll(contributioncontentPDBFileShape);
        writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
    }


    @Override
    public void exportRotatedMiniShapeColoredToPDBFile(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result) {

        List<String> contentPDBFile = new ArrayList<>();
        List<String> contentPDBFilePeptide = makeContentRotatedPDBFileHetatmLigand(algoParameters, result);
        contentPDBFile.addAll(contentPDBFilePeptide);

        List<String> contributioncontentPDBFileShape = makeContentRotatedPDBFileColoredMiniShape(fileName, algoParameters, result);
        contentPDBFile.addAll(contributioncontentPDBFileShape);
        writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
    }


    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------
    private List<String> makeContentPDBFileHetatmLigand(AlgoParameters algoParameters) {
        List<String> contentPDBFilePeptide = new ArrayList<>();

        contentPDBFilePeptide.addAll(ShapeContainerTools.generateLinesFromMyMonomer(hetatmLigandMymonomer, algoParameters, hetatmLigandChainId));

        contentPDBFilePeptide.addAll(ShapeContainerTools.generateConnectLines(hetatmLigandMymonomer));
        return contentPDBFilePeptide;
    }


    private List<String> makeContentRotatedPDBFileHetatmLigand(AlgoParameters algoParameters, ResultsFromEvaluateCost result) {
        List<String> contentPDBFilePeptide = new ArrayList<>();

        Cloner cloner = new Cloner(this.hetatmLigand, algoParameters);
        MyMonomerIfc hetatmLigandRotated = cloner.getRotatedClone(result).getAllChains()[0].getMyMonomers()[0];
        contentPDBFilePeptide.addAll(ShapeContainerTools.generateLinesFromMyMonomer(hetatmLigandRotated, algoParameters, hetatmLigandChainId));

        contentPDBFilePeptide.addAll(ShapeContainerTools.generateConnectLines(hetatmLigandRotated));
        return contentPDBFilePeptide;
    }


    // -------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------
    public MyMonomerIfc getHetatmLigand() {
        return hetatmLigandMymonomer;
    }

    public char[] getHetatmLigandChainId() {
        return hetatmLigandChainId;
    }

    public int getOccurenceId() {
        return occurenceId;
    }
}

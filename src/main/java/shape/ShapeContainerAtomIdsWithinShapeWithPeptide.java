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

import mystructure.MyMonomerIfc;
import parameters.AlgoParameters;
import parameters.QueryAtomDefinedByIds;
import pointWithProperties.CollectionOfPointsWithPropertiesIfc;
import pointWithProperties.PointIfc;
import shapeCompare.ResultsFromEvaluateCost;
import mystructure.MyChainIfc;
import mystructure.MyStructureIfc;

public class ShapeContainerAtomIdsWithinShapeWithPeptide extends ShapeContainerAtomIdsWithinShape implements HasPeptideIfc {
    //-------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------
    private MyChainIfc peptide;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public ShapeContainerAtomIdsWithinShapeWithPeptide(List<QueryAtomDefinedByIds> listAtomDefinedByIds,
                                                       CollectionOfPointsWithPropertiesIfc shape,
                                                       List<PointIfc> listPointDefininingLigandUsedToComputeShape,
                                                       MyStructureIfc myStructureUsedToComputeShape, List<MyMonomerIfc> foreignMonomerToExclude,
                                                       String pdbFileHash) {

        super(listAtomDefinedByIds, shape, listPointDefininingLigandUsedToComputeShape, myStructureUsedToComputeShape, foreignMonomerToExclude, pdbFileHash);
    }


    // -------------------------------------------------------------------
    // Public methods
    // -------------------------------------------------------------------
    @Override
    public MyChainIfc getPeptide() {
        return peptide;
    }


    @Override
    public void exportShapeToPDBFile(String fileName, AlgoParameters algoParameters) {

        List<String> contentPDBFile = new ArrayList<>();
        List<String> contentPDBFilePeptide = ShapeContainerTools.makeContentPDBFilePeptide(algoParameters, peptide);
        contentPDBFile.addAll(contentPDBFilePeptide);

        List<String> contributioncontentPDBFileShape = makeContentPDBFileShape(fileName, algoParameters);
        contentPDBFile.addAll(contributioncontentPDBFileShape);
        writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
    }


    @Override
    public void exportRotatedShapeToPDBFile(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result) {

        List<String> contentPDBFile = new ArrayList<>();
        List<String> contentPDBFilePeptide = ShapeContainerTools.makeContentRotatedPDBFilePeptide(algoParameters, result, peptide);
        contentPDBFile.addAll(contentPDBFilePeptide);

        List<String> contributioncontentPDBFileShape = makeContentRotatedPDBFileShape(fileName, algoParameters, result);
        contentPDBFile.addAll(contributioncontentPDBFileShape);
        writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
    }


    @Override
    public void exportShapeColoredToPDBFile(String fileName, AlgoParameters algoParameters) {

        List<String> contentPDBFile = new ArrayList<>();
        List<String> contentPDBFilePeptide = ShapeContainerTools.makeContentPDBFilePeptide(algoParameters, peptide);
        contentPDBFile.addAll(contentPDBFilePeptide);

        List<String> contributioncontentPDBFileShape = makeContentPDBFileColoredShape(fileName, algoParameters);
        contentPDBFile.addAll(contributioncontentPDBFileShape);
        writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
    }


    @Override
    public void exportRotatedShapeColoredToPDBFile(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result) {

        List<String> contentPDBFile = new ArrayList<>();
        List<String> contentPDBFilePeptide = ShapeContainerTools.makeContentRotatedPDBFilePeptide(algoParameters, result, peptide);
        contentPDBFile.addAll(contentPDBFilePeptide);

        List<String> contributioncontentPDBFileShape = makeContentRotatedPDBFileColoredShape(fileName, algoParameters, result);
        contentPDBFile.addAll(contributioncontentPDBFileShape);
        writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
    }


    @Override
    public void exportMiniShapeToPDBFile(String fileName, AlgoParameters algoParameters) {

        List<String> contentPDBFile = new ArrayList<>();
        List<String> contentPDBFilePeptide = ShapeContainerTools.makeContentPDBFilePeptide(algoParameters, peptide);
        contentPDBFile.addAll(contentPDBFilePeptide);

        List<String> contributioncontentPDBFileShape = makeContentPDBFileMiniShape(fileName, algoParameters);
        contentPDBFile.addAll(contributioncontentPDBFileShape);
        writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
    }


    @Override
    public void exportRotatedMiniShapeToPDBFile(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result) {

        List<String> contentPDBFile = new ArrayList<>();
        List<String> contentPDBFilePeptide = ShapeContainerTools.makeContentRotatedPDBFilePeptide(algoParameters, result, peptide);
        contentPDBFile.addAll(contentPDBFilePeptide);

        List<String> contributioncontentPDBFileShape = makeContentRotatedPDBFileMiniShape(fileName, algoParameters, result);
        contentPDBFile.addAll(contributioncontentPDBFileShape);
        writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
    }


    @Override
    public void exportMiniShapeColoredToPDBFile(String fileName, AlgoParameters algoParameters) {

        List<String> contentPDBFile = new ArrayList<>();
        List<String> contentPDBFilePeptide = ShapeContainerTools.makeContentPDBFilePeptide(algoParameters, peptide);
        contentPDBFile.addAll(contentPDBFilePeptide);

        List<String> contributioncontentPDBFileShape = makeContentPDBFileColoredMiniShape(fileName, algoParameters);
        contentPDBFile.addAll(contributioncontentPDBFileShape);
        writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
    }


    @Override
    public void exportRotatedMiniShapeColoredToPDBFile(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result) {

        List<String> contentPDBFile = new ArrayList<>();
        List<String> contentPDBFilePeptide = ShapeContainerTools.makeContentRotatedPDBFilePeptide(algoParameters, result, peptide);
        contentPDBFile.addAll(contentPDBFilePeptide);

        List<String> contributioncontentPDBFileShape = makeContentRotatedPDBFileColoredMiniShape(fileName, algoParameters, result);
        contentPDBFile.addAll(contributioncontentPDBFileShape);
        writeLinesToPDBFile(contentPDBFile, fileName, algoParameters);
    }


    // -------------------------------------------------------------------
    // Getter and Setter
    // -------------------------------------------------------------------
    public void setPeptide(MyChainIfc peptide) {
        this.peptide = peptide;
    }
}

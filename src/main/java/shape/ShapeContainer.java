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

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;

import hits.HitTools;
import hits.StrikingPropertyToElement;
import math.AddToMap;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.biojava.nbio.structure.Element;
import parameters.AlgoParameters;
import pointWithProperties.CollectionOfPointsWithPropertiesIfc;
import pointWithProperties.PointIfc;
import pointWithProperties.PointWithPropertiesIfc;
import pointWithProperties.ShapeIOTools;
import pointWithProperties.StrikingProperties;
import shapeCompare.ResultsFromEvaluateCost;
import shapeReduction.TriangleInteger;
import mystructure.ExceptionInMyStructurePackage;
import mystructure.MyChain;
import mystructure.MyChainIfc;
import mystructure.MyMonomerIfc;
import mystructure.MyStructureIfc;


public class ShapeContainer implements ShapeContainerIfc, Serializable {
    //-------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------
    protected char[] fourLetterCode;
    protected String pdbFileHash;

    protected CollectionOfPointsWithPropertiesIfc shape;
    private List<PointIfc> listPointDefininingLigandUsedToComputeShape;
    private transient MyStructureIfc myStructureUsedToComputeShape;
    protected Map<Integer, PointWithPropertiesIfc> miniShape;
    private List<TriangleInteger> listTriangleOfPointsFromMinishape;

    private List<Integer> histogramStrikingProperties;
    private List<Integer> histogramD2;

    private boolean debug = false;

    private transient List<MyMonomerIfc> foreignMonomerToExclude;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public ShapeContainer(CollectionOfPointsWithPropertiesIfc shape, List<PointIfc> listPointDefininingLigandUsedToComputeShape, MyStructureIfc myStructureUsedToComputeShape, List<MyMonomerIfc> foreignMonomerToExclude, String pdbFileHash) {
        this.shape = shape;
        this.pdbFileHash = pdbFileHash;
        this.listPointDefininingLigandUsedToComputeShape = listPointDefininingLigandUsedToComputeShape;
        this.myStructureUsedToComputeShape = myStructureUsedToComputeShape;
        this.foreignMonomerToExclude = foreignMonomerToExclude;
        if (debug == true) {
            if (miniShape != null) {
                System.out.println("shape container building: " + miniShape.size() + " points  /  " + shape.getSize());
            }
            if (listTriangleOfPointsFromMinishape != null) {
                System.out.println("shape container triangle : " + listTriangleOfPointsFromMinishape.size());
            }
        }
    }

    //-------------------------------------------------------------
    // Override methods
    //-------------------------------------------------------------
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                        append(fourLetterCode).
                        append(pdbFileHash).
                        toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ShapeContainer))
            return false;
        if (obj == this)
            return true;

        ShapeContainer rhs = (ShapeContainer) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                        append(fourLetterCode, rhs.fourLetterCode).
                        append(pdbFileHash, rhs.pdbFileHash).
                        isEquals();
    }

    //-------------------------------------------------------------
    // Interface & Public methods
    //-------------------------------------------------------------
    @Override
    public String makeEndFileName() {
        return String.valueOf(fourLetterCode);

    }


    @Override
    public void exportShapeToPDBFile(String fileName, AlgoParameters algoParameters) {

        List<String> contributioncontentPDBFileShape = makeContentPDBFileShape(fileName, algoParameters);
        writeLinesToPDBFile(contributioncontentPDBFileShape, fileName, algoParameters);
    }


    @Override
    public void exportRotatedShapeToPDBFile(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result) {
        List<String> contributioncontentPDBFileShape = makeContentRotatedPDBFileShape(fileName, algoParameters, result);
        writeLinesToPDBFile(contributioncontentPDBFileShape, fileName, algoParameters);

    }


    @Override
    public void exportShapeColoredToPDBFile(String fileName, AlgoParameters algoParameters) {
        List<String> contributioncontentPDBFileShape = makeContentPDBFileColoredShape(fileName, algoParameters);
        writeLinesToPDBFile(contributioncontentPDBFileShape, fileName, algoParameters);
    }


    @Override
    public void exportRotatedShapeColoredToPDBFile(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result) {
        List<String> contributioncontentPDBFileShape = makeContentRotatedPDBFileColoredShape(fileName, algoParameters, result);
        writeLinesToPDBFile(contributioncontentPDBFileShape, fileName, algoParameters);
    }


    @Override
    public void exportMiniShapeToPDBFile(String fileName, AlgoParameters algoParameters) {

        List<String> contributioncontentPDBFileShape = makeContentPDBFileMiniShape(fileName, algoParameters);
        writeLinesToPDBFile(contributioncontentPDBFileShape, fileName, algoParameters);
    }


    @Override
    public void exportRotatedMiniShapeToPDBFile(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result) {
        List<String> contributioncontentPDBFileShape = makeContentRotatedPDBFileMiniShape(fileName, algoParameters, result);
        writeLinesToPDBFile(contributioncontentPDBFileShape, fileName, algoParameters);
    }


    @Override
    public void exportMiniShapeColoredToPDBFile(String fileName, AlgoParameters algoParameters) {

        List<String> contentPDBFileShape = makeContentPDBFileColoredMiniShape(fileName, algoParameters);
        writeLinesToPDBFile(contentPDBFileShape, fileName, algoParameters);
    }


    @Override
    public void exportRotatedMiniShapeColoredToPDBFile(String fileName,
                                                       AlgoParameters algoParameters, ResultsFromEvaluateCost result) {
        List<String> contributioncontentPDBFileShape = makeContentRotatedPDBFileColoredMiniShape(fileName, algoParameters, result);
        writeLinesToPDBFile(contributioncontentPDBFileShape, fileName, algoParameters);
    }


    @Override
    public PointWithPropertiesIfc get(int idPoint) {
        return shape.getPointFromId(idPoint);
    }


    @Override
    public List<MyMonomerIfc> getForeignMonomerToExclude() {
        return foreignMonomerToExclude;
    }


    //-------------------------------------------------------------
    // Implementation for this class and derived classes
    //-------------------------------------------------------------
    protected List<String> makeContentPDBFileShape(String fileName, AlgoParameters algoParameters) {

        List<PointIfc> listPoints = new ArrayList<>();
        for (int i = 0; i < this.shape.getSize(); i++) {
            listPoints.add(this.shape.getPointFromId(i).getCoords());
        }
        String elementNameForColor = "C";
        List<String> contributioncontentPDBFileShape = createPDBLinesFromListPoints(algoParameters, fileName, listPoints, elementNameForColor);
        return contributioncontentPDBFileShape;
    }


    protected List<String> makeContentPDBFileMiniShape(String fileName, AlgoParameters algoParameters) {

        List<PointIfc> listPoints = new ArrayList<>();
        for (Entry<Integer, PointWithPropertiesIfc> entry : this.miniShape.entrySet()) {
            listPoints.add(entry.getValue().getCoords());
        }
        String elementNameForColor = "C";
        List<String> contributioncontentPDBFileShape = createPDBLinesFromListPoints(algoParameters, fileName, listPoints, elementNameForColor);
        return contributioncontentPDBFileShape;
    }


    protected List<String> makeContentPDBFileColoredMiniShape(String fileName, AlgoParameters algoParameters) {

        Map<StrikingProperties, List<PointIfc>> mapPropertyPoints = groupPointsByStrikingProperty(this.miniShape);
        List<String> contentPDBFileShape = new ArrayList<>();
        for (Entry<StrikingProperties, List<PointIfc>> entry : mapPropertyPoints.entrySet()) {

            Element element = StrikingPropertyToElement.getAtomSymbol(entry.getKey());
            String elementNameForColor = element.toString();
            List<String> contributioncontentPDBFileShape = createPDBLinesFromListPoints(algoParameters, fileName, entry.getValue(), elementNameForColor);
            contentPDBFileShape.addAll(contributioncontentPDBFileShape);
        }
        return contentPDBFileShape;
    }


    protected List<String> makeContentPDBFileColoredShape(String fileName, AlgoParameters algoParameters) {

        Map<StrikingProperties, List<PointIfc>> mapPropertyPoints = groupPointsByStrikingProperty(shape);
        List<String> contentPDBFileShape = new ArrayList<>();
        for (Entry<StrikingProperties, List<PointIfc>> entry : mapPropertyPoints.entrySet()) {

            Element element = StrikingPropertyToElement.getAtomSymbol(entry.getKey());
            String elementNameForColor = element.toString();
            List<String> contributioncontentPDBFileShape = createPDBLinesFromListPoints(algoParameters, fileName, entry.getValue(), elementNameForColor);
            contentPDBFileShape.addAll(contributioncontentPDBFileShape);
        }
        return contentPDBFileShape;
    }


    protected List<String> makeContentRotatedPDBFileShape(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result) {

        CollectionOfPointsWithPropertiesIfc rotatedShape = HitTools.returnCloneRotatedShape(shape, result);

        List<PointIfc> listPoints = new ArrayList<>();
        for (int i = 0; i < rotatedShape.getSize(); i++) {
            listPoints.add(rotatedShape.getPointFromId(i).getCoords());
        }
        String elementNameForColor = "C";
        List<String> contributioncontentPDBFileShape = createPDBLinesFromListPoints(algoParameters, fileName, listPoints, elementNameForColor);
        return contributioncontentPDBFileShape;
    }


    protected List<String> makeContentRotatedPDBFileMiniShape(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result) {

        Map<Integer, PointWithPropertiesIfc> rotatedMinishape = HitTools.returnCloneRotatedMiniShape(miniShape, result);
        List<PointIfc> listPoints = new ArrayList<>();
        for (Entry<Integer, PointWithPropertiesIfc> entry : rotatedMinishape.entrySet()) {
            listPoints.add(entry.getValue().getCoords());
        }
        String elementNameForColor = "C";
        List<String> contributioncontentPDBFileShape = createPDBLinesFromListPoints(algoParameters, fileName, listPoints, elementNameForColor);
        return contributioncontentPDBFileShape;
    }


    protected List<String> makeContentRotatedPDBFileColoredShape(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result) {

        CollectionOfPointsWithPropertiesIfc rotatedShape = HitTools.returnCloneRotatedShape(shape, result);
        Map<StrikingProperties, List<PointIfc>> mapPropertyPoints = groupPointsByStrikingProperty(rotatedShape);

        List<String> contentPDBFileShape = new ArrayList<>();
        for (Entry<StrikingProperties, List<PointIfc>> entry : mapPropertyPoints.entrySet()) {

            Element element = StrikingPropertyToElement.getAtomSymbol(entry.getKey());
            String elementNameForColor = element.toString();
            List<String> contributioncontentPDBFileShape = createPDBLinesFromListPoints(algoParameters, fileName, entry.getValue(), elementNameForColor);
            contentPDBFileShape.addAll(contributioncontentPDBFileShape);
        }
        return contentPDBFileShape;
    }


    protected List<String> makeContentRotatedPDBFileColoredMiniShape(String fileName, AlgoParameters algoParameters, ResultsFromEvaluateCost result) {

        Map<Integer, PointWithPropertiesIfc> rotatedMinishape = HitTools.returnCloneRotatedMiniShape(miniShape, result);
        Map<StrikingProperties, List<PointIfc>> mapPropertyPoints = groupPointsByStrikingProperty(rotatedMinishape);
        List<String> contentPDBFileShape = new ArrayList<>();
        for (Entry<StrikingProperties, List<PointIfc>> entry : mapPropertyPoints.entrySet()) {

            Element element = StrikingPropertyToElement.getAtomSymbol(entry.getKey());
            String elementNameForColor = element.toString();
            List<String> contributioncontentPDBFileShape = createPDBLinesFromListPoints(algoParameters, fileName, entry.getValue(), elementNameForColor);
            contentPDBFileShape.addAll(contributioncontentPDBFileShape);
        }
        return contentPDBFileShape;
    }


    protected Map<StrikingProperties, List<PointIfc>> groupPointsByStrikingProperty(Map<Integer, PointWithPropertiesIfc> miniShape) {

        Map<StrikingProperties, List<PointIfc>> mapPropertyAndPoints = new HashMap<>();

        for (Entry<Integer, PointWithPropertiesIfc> entry : miniShape.entrySet()) {

            PointWithPropertiesIfc currentPoint = entry.getValue();
            List<StrikingProperties> listPropertiesFound = currentPoint.getStrikingProperties();
            for (StrikingProperties strikingProperty : listPropertiesFound) {
                AddToMap.addElementToAMapOfList(mapPropertyAndPoints, strikingProperty, currentPoint.getCoords());
            }

        }
        return mapPropertyAndPoints;
    }


    protected Map<StrikingProperties, List<PointIfc>> groupPointsByStrikingProperty(CollectionOfPointsWithPropertiesIfc shape) {

        Map<StrikingProperties, List<PointIfc>> mapPropertyAndPoints = new HashMap<>();

        for (int i = 0; i < shape.getSize(); i++) {

            PointWithPropertiesIfc currentPoint = shape.getPointFromId(i);
            List<StrikingProperties> listPropertiesFound = currentPoint.getStrikingProperties();
            for (StrikingProperties strikingProperty : listPropertiesFound) {
                AddToMap.addElementToAMapOfList(mapPropertyAndPoints, strikingProperty, currentPoint.getCoords());
            }

        }
        return mapPropertyAndPoints;
    }


    protected List<String> createPDBLinesFromListPoints(AlgoParameters algoParameters, String fileName, List<PointIfc> listPoints, String elementNameForColor) {


        MyMonomerIfc monomerMadeWithThePoints = null;
        try {
            monomerMadeWithThePoints = ShapeIOTools.convertAListOfPointIfcToAPseudoPDBFileForVisualization(listPoints, elementNameForColor);

        } catch (ExceptionInMyStructurePackage e) {
            e.printStackTrace();
            return null;
        }
        MyChainIfc mychain = new MyChain(monomerMadeWithThePoints, "X".toCharArray());
        monomerMadeWithThePoints.setParent(mychain);

        if (algoParameters.getQUERY_CHAIN_ID() == null) { // for case of query defined by Ids
            algoParameters.setQUERY_CHAIN_ID("X");
        }
        List<String> contributioncontentPDBFileShape = ShapeContainerTools.generateLinesFromMyMonomer(monomerMadeWithThePoints, algoParameters, algoParameters.getQUERY_CHAIN_ID().toCharArray());
        return contributioncontentPDBFileShape;
    }


    protected void writeLinesToPDBFile(List<String> listOfLines, String fileName, AlgoParameters algoParameters) {

        String pathForThisFile = algoParameters.getPATH_TO_RESULT_FILES();
        String fileNameForThisFile = fileName + ".ent.gz";

        try {
            File file = new File(pathForThisFile + fileNameForThisFile);
            if (file.exists()) {
                file.setWritable(true);
                file.delete();
            }

            BufferedWriter outputFile = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(pathForThisFile + fileNameForThisFile))));
            for (String str : listOfLines) {
                outputFile.write(str + "\n");
            }
            outputFile.write("END");
            outputFile.close();

        } catch (IOException e) {
            //System.out.println("Failure: in writting PDB file");
            //e.printStackTrace();
        }
    }


    // -------------------------------------------------------------------
    // Getter and Setter
    // -------------------------------------------------------------------
    public CollectionOfPointsWithPropertiesIfc getShape() {
        return shape;
    }

    public Map<Integer, PointWithPropertiesIfc> getMiniShape() {
        return miniShape;
    }

    public void setMiniShape(Map<Integer, PointWithPropertiesIfc> miniShape) {
        this.miniShape = miniShape;
    }

    public List<? extends PointIfc> getListPointDefininingLigandUsedToComputeShape() {
        return listPointDefininingLigandUsedToComputeShape;
    }

    public MyStructureIfc getMyStructureUsedToComputeShape() {
        return myStructureUsedToComputeShape;
    }


    public List<TriangleInteger> getListTriangleOfPointsFromMinishape() {
        return listTriangleOfPointsFromMinishape;
    }

    public void setListTriangleOfPointsFromMinishape(List<TriangleInteger> listTriangleOfPointsFromMinishape) {
        this.listTriangleOfPointsFromMinishape = listTriangleOfPointsFromMinishape;
    }

    public char[] getFourLetterCode() {
        return fourLetterCode;
    }

    public void setFourLetterCode(char[] fourLetterCode) {
        this.fourLetterCode = fourLetterCode;
    }

    public List<Integer> getHistogramStrikingProperties() {
        return histogramStrikingProperties;
    }

    public void setHistogramStrikingProperties(List<Integer> histogramStrikingProperties) {
        this.histogramStrikingProperties = histogramStrikingProperties;
    }

    public List<Integer> getHistogramD2() {
        return histogramD2;
    }

    public void setHistogramD2(List<Integer> histogramD2) {
        this.histogramD2 = histogramD2;
    }

    public String getPdbFileHash() {
        return pdbFileHash;
    }
}

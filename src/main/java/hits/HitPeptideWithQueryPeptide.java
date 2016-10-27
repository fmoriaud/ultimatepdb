package hits;

import shape.ShapeContainerIfc;
import shapeCompare.ResultsFromEvaluateCost;


public class HitPeptideWithQueryPeptide extends Hit {
    //-------------------------------------------------------------
// Class members
//-------------------------------------------------------------
    private float rmsdBackboneWhencomparingPeptideToPeptide;


//-------------------------------------------------------------
// Constructor
//-------------------------------------------------------------
    public HitPeptideWithQueryPeptide(ShapeContainerIfc shapeContainer, ResultsFromEvaluateCost resultsFromEvaluateCost, float rmsdBackboneWhencomparingPeptideToPeptide) {
        super(shapeContainer, resultsFromEvaluateCost);
        this.rmsdBackboneWhencomparingPeptideToPeptide = rmsdBackboneWhencomparingPeptideToPeptide;
    }


//-------------------------------------------------------------
// Getters & Setters
//-------------------------------------------------------------
    public float getRmsdBackboneWhencomparingPeptideToPeptide() {
        return rmsdBackboneWhencomparingPeptideToPeptide;
    }
}

package shapeCompare;

import hits.Hit;
import mystructure.MyStructureIfc;
import mystructure.MyStructureTools;
import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shape.ShapeContainerWithLigand;
import shape.ShapeContainerWithPeptide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fabrice on 14/11/16.
 */
public class CompareCompleteCheck {


    //------------------------
    // Class variables
    //------------------------
    private MyStructureIfc myStructureGlobalQuery;
    private ShapeContainerIfc shapeContainerQuery;
    private ShapeContainerIfc shapeContainerAnyShape;
    private AlgoParameters algoParameters;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public CompareCompleteCheck(MyStructureIfc myStructureGlobalQuery, ShapeContainerIfc shapeContainerQuery, ShapeContainerIfc shapeContainerWithLigandOrPeptide, AlgoParameters algoParameters) {

        this.myStructureGlobalQuery = myStructureGlobalQuery;
        this.shapeContainerQuery = shapeContainerQuery;
        this.shapeContainerAnyShape = shapeContainerWithLigandOrPeptide;
        this.algoParameters = algoParameters;
    }

    public List<Hit> computeResults() throws NullResultFromAComparisonException {


        List<Hit> hitsExtendedPairing = new ArrayList<>();

        if (!(shapeContainerAnyShape instanceof ShapeContainerWithPeptide || shapeContainerAnyShape instanceof ShapeContainerWithLigand)) {

            String message = "CompareCompleteCheck can be used only if a peptide or a ligand in target shape";
            NullResultFromAComparisonException exception = new NullResultFromAComparisonException(message);
            throw exception;
        }


        // shapeContainerQuery can be any
        // shapeContainerAnyShape have to have a ligand

        // overlay the two shape
        // I dont need extension, minishape overlay is fine
        List<ResultsFromEvaluateCost> resultsPairingTriangleSeed = CompareTools.compareShapesBasedOnTriangles(shapeContainerQuery, shapeContainerAnyShape, algoParameters);
        System.out.println("Found " + resultsPairingTriangleSeed.size() + " triangles matches");

        resultsPairingTriangleSeed = resultsPairingTriangleSeed.subList(0,1);
        for (ResultsFromEvaluateCost result : resultsPairingTriangleSeed) {

            // cost of overlay of query to hit shape, based on paired points,
            // not very informative as relative to the number of points, a good local overlay and a large good overlay
            // scores the same. Good thing is that cosly overlay is detected.
            double cost = result.getCost();


            // get the hit ligand in the query global structure
            MyStructureIfc rotatedLigandOrPeptide = CompareTools.getProtonatedLigandOrPeptideInReferenceOfQuery(shapeContainerAnyShape, result, algoParameters);


            // I could compute neighbors by representative distance and then use the same code for shape ??
            // know what was removed to build MyStructureLocal

            MyStructureIfc myStructureLocalQuery = shapeContainerAnyShape.getMyStructureUsedToComputeShape();
            //MyStructureTools.computeAndStoreNeighBorhingAminoMonomersByDistanceBetweenRepresentativeMyAtom(myStructureLocalQuery, rotatedLigandOrPeptide);







            // Check if rotated hit ligand fits in Query structure global without the query ligand (if there is)
            // Could be a chain, a segment, an ignore chain in shape with ids (then there is a ligand kind of)
            // Check clashes, if too many it is not worth computing the shape as anyway the hit ligand doesnt fit.



            // compute the shape
            //StructureLocalToBuildShapeAroundForeignLigand structureLocalToBuildShapeAroundForeignLigand = new StructureLocalToBuildShapeAroundForeignLigand(myStructureGlobalQuery, rotatedLigandOrPeptide, algoParameters);

            // overlay this shape with hit shape
            // get the cost: at best if additional shape from hit doesnt overlap with query,
            // then the cost is the same as the cost computed before
            // if it gets worse then there is bad overlap: I can use the difference, if cost increases then bad,


        }


        return hitsExtendedPairing;
    }


}
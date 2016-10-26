package hits;

import java.util.ArrayList;
import java.util.List;

import shape.ShapeContainerIfc;
import shape.ShapeContainerWithLigand;
import shape.ShapeContainerWithPeptide;
import shapeCompare.ResultsFromEvaluateCost;
import ultiJmol1462.ResultsUltiJMolMinimizedHitLigandOnTarget;


public class Hit {
	//------------------------
	// Class variables
	//------------------------
	private ShapeContainerIfc shapeContainer;
	private ResultsFromEvaluateCost resultsFromEvaluateCost;
	private ResultsUltiJMolMinimizedHitLigandOnTarget hitScore;



	
	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public Hit(ShapeContainerIfc shapeContainer, ResultsFromEvaluateCost resultsFromEvaluateCost){
		this.shapeContainer = shapeContainer;
		this.resultsFromEvaluateCost = resultsFromEvaluateCost;
	}




	// -------------------------------------------------------------------
	// Public && Interface method
	// -------------------------------------------------------------------
	@Override public String toString() {
		StringBuilder result = new StringBuilder();
		String NEW_LINE = System.getProperty("line.separator");
		List<String> listResSeq = new ArrayList<>();

		result.append("PDB = " + String.valueOf(this.getShapeContainer().getFourLetterCode()));
		if (shapeContainer instanceof ShapeContainerWithPeptide){

			ShapeContainerWithPeptide shapeContainerWithPeptide = (ShapeContainerWithPeptide) shapeContainer;

			for (char[] resInSeq: shapeContainerWithPeptide.getPeptideSequence()){
				listResSeq.add(String.valueOf(resInSeq));
			}

			result.append(" chain id = " + String.valueOf(shapeContainerWithPeptide.getPeptide().getChainId()));
			result.append(" index = " + String.valueOf(shapeContainerWithPeptide.getStartingRankId()));
			result.append(" sequence = " + listResSeq.toString());


		}
		if (shapeContainer instanceof ShapeContainerWithLigand){

			ShapeContainerWithLigand shapeContainerWithLigand = (ShapeContainerWithLigand) shapeContainer;
			result.append(" chain id = " + String.valueOf(shapeContainerWithLigand.getHetatmLigand().getParent().getChainId()));
			result.append(" occurrence id = " + String.valueOf(shapeContainerWithLigand.getOccurenceId()));

		}
		result.append(" cost = " + this.getResultsFromEvaluateCost().getCost() +
				" minishape size = " + this.getShapeContainer().getMiniShape().size() + " shape size = " + this.getShapeContainer().getShape().getSize()
				+ NEW_LINE);

		result.append("RatioPairedPointToHitPoints = " + resultsFromEvaluateCost.getRatioPairedPointInQuery());
		result.append(" CountOfLongDistanceChange = " + hitScore.getCountOfLongDistanceChange());
		result.append(" InteractionEFinal = " + hitScore.getInteractionEFinal());
		result.append(" RmsdLigand = " + hitScore.getRmsdLigand());
		result.append(" LigandStrainedEnergy = " + hitScore.getLigandStrainedEnergy() + NEW_LINE);

		return result.toString();
	}

	// -------------------------------------------------------------------
	// Getter & Setter
	// -------------------------------------------------------------------
	public ShapeContainerIfc getShapeContainer() {
		return shapeContainer;
	}

	public ResultsFromEvaluateCost getResultsFromEvaluateCost() {
		return resultsFromEvaluateCost;
	}
	
	public ResultsUltiJMolMinimizedHitLigandOnTarget getHitScore() {
		return hitScore;
	}
	
	public void setHitScore(ResultsUltiJMolMinimizedHitLigandOnTarget hitScore) {
		this.hitScore = hitScore;
	}
}
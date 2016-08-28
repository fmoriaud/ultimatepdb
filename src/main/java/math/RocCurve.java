package math;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import math.ToolsMath.ROCMOD;

public class RocCurve {
	//------------------------
	// Class variables
	//------------------------
	private List<Double> pointsPositives;
	private List<Double> pointsNegatives;
	private ROCMOD rocmod;

	private double minValue;
	private double maxValue;

	private int countOfIntervalsOnXaxis = 100;

	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public RocCurve(List<Double> pointsPositives, List<Double> pointsNegatives, ROCMOD rocmod){
		this.pointsPositives = pointsPositives;
		this.pointsNegatives = pointsNegatives;
		this.rocmod = rocmod;
	}




	// -------------------------------------------------------------------
	// Public methods
	// -------------------------------------------------------------------
	public double getAUC(){

		computeMaxMinCost();

		double auc = computeAUCofROCcurve();
		return auc;
	}




	// -------------------------------------------------------------------
	// Implementation
	// -------------------------------------------------------------------
	private double computeAUCofROCcurve(){


		// TODO introduce exception
		if ((pointsPositives.size() == 0) || (pointsNegatives.size() == 0)){
			return 0.0;
		}


		// loop on cost from min to max
		double interval = (maxValue - minValue) / countOfIntervalsOnXaxis;

		Map<Double,Double> rocCurve = new TreeMap<>();

		for (int i=0; i<=countOfIntervalsOnXaxis; i++){
			double threshold = minValue + i * interval;
			// for each cost use it as threshold
			int TP = computeTruePositiveCount(threshold);
			double TPR = TP / (1.0 * pointsPositives.size());
			int FP = computeFalsePositiveCount(threshold);
			double FPR = FP / (1.0 * pointsNegatives.size());
			rocCurve.put(FPR,  TPR);
		}

		List<Double> xAxis = new ArrayList<>();
		List<Double> yAxis = new ArrayList<>();
		for (Entry<Double, Double> entry: rocCurve.entrySet()){
			xAxis.add(entry.getKey());
			yAxis.add(entry.getValue());
		}

		double auc = 0.0;
		for (int i=0; i<xAxis.size() -1; i++){
			double deltaX = xAxis.get(i+1) - xAxis.get(i);
			double deltaY = (yAxis.get(i+1) + yAxis.get(i)) / 2.0;
			auc += deltaX * deltaY;
		}
		return auc;
	}



	private void computeMaxMinCost(){

		double minCost = Double.MAX_VALUE;
		double maxCost = Double.MIN_VALUE;

		List<Double> allPoints = new ArrayList<>();
		allPoints.addAll(pointsNegatives);
		allPoints.addAll(pointsPositives);

		for (Double cost: allPoints){
			if (cost < minCost){
				minCost = cost;
			}
			if (cost > maxCost){
				maxCost = cost;
			}
		}

		this.minValue = minCost;
		this.maxValue = maxCost;
	}



	int computeTruePositiveCount(double costThreshold){

		int countTruePositives = 0;
		for (Double cost: pointsPositives){

			if (rocmod == ROCMOD.SCORE){
				if (cost > costThreshold){
					countTruePositives += 1;
				}
			}
			if (rocmod == ROCMOD.COST){
				if (cost < costThreshold){
					countTruePositives += 1;
				}
			}

		}
		return countTruePositives;
	}



	int computeFalsePositiveCount(double costThreshold){

		int countFalsePositives = 0;
		for (Double cost: pointsNegatives){

			if (rocmod == ROCMOD.SCORE){
				if (cost > costThreshold){
					countFalsePositives += 1;
				}
			}
			if (rocmod == ROCMOD.COST){
				if (cost < costThreshold){
					countFalsePositives += 1;
				}
			}
		}
		return countFalsePositives;
	}
}

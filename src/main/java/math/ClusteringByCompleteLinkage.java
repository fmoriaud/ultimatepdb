package math;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import parameters.AlgoParameters;
import pointWithProperties.PointWithProperties;

public class ClusteringByCompleteLinkage {
	//------------------------
	// Class variables
	//------------------------
	private List<PointWithProperties> listStartingPoint;
	private AlgoParameters algoParameters;




	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public ClusteringByCompleteLinkage(List<PointWithProperties> listStartingPoint, AlgoParameters algoParameters){
		this.listStartingPoint = listStartingPoint;
		this.algoParameters = algoParameters;
	}




	// -------------------------------------------------------------------
	// Public & Interface Methods
	// -------------------------------------------------------------------
	public List<List<PointWithProperties>> getClusteredPoints(){

		List<List<PointWithProperties>> listClusteredPoints = putEachPointInADifferentCluster();
		if (listStartingPoint.size() < 2){
			return listClusteredPoints;
		}

		double limiteOfMaxDistanceToStopMerging = algoParameters.getLIMIT_MAX_DISTANCE_TO_STOP_MERGING();

		List<List<PointWithProperties>> currentClustering = listClusteredPoints;

		boolean clusteringIsDone = false;

		//long startTimeMs = System.currentTimeMillis();
		while(clusteringIsDone == false){

			List<List<PointWithProperties>> closestPairCluster = findClosestPairOfCluster(currentClustering, limiteOfMaxDistanceToStopMerging);

			if (closestPairCluster != null){
				// Then accept this new clustering
				updateClusteringByAddingANewMerge(closestPairCluster, currentClustering);
			}else{
				clusteringIsDone = true;
			}
		}
		//long taskTimeMs  = System.currentTimeMillis( ) - startTimeMs;
		//System.out.println("computational time getClusteredPoints = " + (double) taskTimeMs / (1000.0 * 60.0 ) + " minutes");


		return currentClustering;
	}




	// -------------------------------------------------------------------
	// Implementation
	// -------------------------------------------------------------------
	private void updateClusteringByAddingANewMerge(List<List<PointWithProperties>> pairCluster, List<List<PointWithProperties>> clustering){

		List<PointWithProperties> cluster1 = pairCluster.get(0);
		List<PointWithProperties> cluster2 = pairCluster.get(1);

		List<PointWithProperties> newCluster = new ArrayList<>();

		boolean cluster1Found = false;
		boolean cluster2Found = false;

		Iterator<List<PointWithProperties>> itr = clustering.iterator();
		while(itr.hasNext()) {
			List<PointWithProperties> cluster = itr.next();

			if (cluster == cluster1){
				newCluster.addAll(cluster);
				itr.remove();
				cluster1Found = true;
			}
			if (cluster == cluster2){
				newCluster.addAll(cluster);
				itr.remove();
				cluster2Found = true;
			}
		}

		clustering.add(newCluster);

		if ((cluster1Found == false) || (cluster2Found == false)){
			System.out.println("problem cluster to merge is/are not found ");
			System.exit(0);
		}
	}



	private List<List<PointWithProperties>> putEachPointInADifferentCluster(){

		List<List<PointWithProperties>> listClusteredPoints = new ArrayList<>();

		for (PointWithProperties point: listStartingPoint){

			List<PointWithProperties> list = new ArrayList<>();
			list.add(point);
			listClusteredPoints.add(list);
		}

		return listClusteredPoints;
	}



	private List<List<PointWithProperties>> findClosestPairOfCluster(List<List<PointWithProperties>> listClusteredPoints, double cutoffDistance){

		List<List<PointWithProperties>> closestPairCluster = null;

		double shortestDistance = Double.MAX_VALUE;
		List<PointWithProperties> cluster1Min;
		List<PointWithProperties> cluster2Min;

		for (int i=0; i<listClusteredPoints.size(); i++){
			for (int j=i+1; j<listClusteredPoints.size(); j++){

				List<PointWithProperties> cluster1 = listClusteredPoints.get(i);
				List<PointWithProperties> cluster2 = listClusteredPoints.get(j);

				double distance = computeMaxDistanceBetweenTwoCluster(cluster1, cluster2);
				if (distance < shortestDistance){
					shortestDistance = distance;
					cluster1Min = listClusteredPoints.get(i);
					cluster2Min = listClusteredPoints.get(j);
					List<List<PointWithProperties>> pairCluster = new ArrayList<>();
					pairCluster.add(cluster1Min);
					pairCluster.add(cluster2Min);
					closestPairCluster = pairCluster;
				}
			}
		}
		if (shortestDistance < cutoffDistance){
			return closestPairCluster;
		} else{
			return null;
		}
	}



	private double computeMaxDistanceBetweenTwoCluster(List<PointWithProperties> cluster1, List<PointWithProperties> cluster2){

		double maxDistance = Double.MIN_VALUE;

		for (PointWithProperties point1: cluster1){
			for (PointWithProperties point2: cluster2){

				double distance = ToolsMath.computeDistance(point1.getCoords().getCoords(), point2.getCoords().getCoords());
				if (distance > maxDistance){
					maxDistance = distance;
				}
			}
		}
		return maxDistance;
	}
}

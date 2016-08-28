package math;

import java.util.ArrayList;
import java.util.List;

public class GeometricToolsToProtonate {

	public static float[] getCoordinatesOfHydrogenAtTipOfABond(float[] coordsAtomToProtonate, float[] coordsneighbor){

		float distanceToH = 1.02f / 2.0f;
		float[] vectoratomBoundToAtomHoldingHatomHoldingH = ToolsMath.normalizeVector( ToolsMath.v1minusV2(coordsAtomToProtonate, coordsneighbor));
		float[] vectorAtomHoldinHtoAddedH1 = ToolsMath.multiplyByScalar(ToolsMath.normalizeVector(vectoratomBoundToAtomHoldingHatomHoldingH), distanceToH);
		float[] coordAddedH = ToolsMath.v1plusV2(coordsAtomToProtonate, vectorAtomHoldinHtoAddedH1);

		return coordAddedH;
	}



	public static float[] getCoordinateOfHydrogenAtTheTipOfAVshape(float[] coordsAtom1, float[] coordsAtomToProtonate, float[] coordsAtom2 ){

		float distanceAtomToProtonateToH = 1.02f;

		float[] vectorAtom1ToAtomToProtonate = ToolsMath.normalizeVector( ToolsMath.v1minusV2(coordsAtomToProtonate, coordsAtom1));
		float[] vectorAtom2ToAtomToProtonate = ToolsMath.normalizeVector( ToolsMath.v1minusV2(coordsAtomToProtonate, coordsAtom2));
		float[] vectorAtomToProtonateToHydrogen = ToolsMath.multiplyByScalar(ToolsMath.normalizeVector(ToolsMath.v1plusV2(vectorAtom1ToAtomToProtonate, vectorAtom2ToAtomToProtonate)), distanceAtomToProtonateToH);
		float[] coordsH = ToolsMath.v1plusV2(coordsAtomToProtonate, vectorAtomToProtonateToHydrogen);

		return coordsH;
	}



	public static List<float[]> getCoordinateOfTwoHydrogenAtTheTipOfAVshape(float[] coordsAtomToProtonate, float[] neighbor,  float[] neighbor1, float[] neighbor2){

		List<float[]> twoHydrogen = new ArrayList<>();

		float distanceNH = 1.02f;

		float[] vectorNeighbor2Neighbor = ToolsMath.normalizeVector( ToolsMath.v1minusV2(neighbor, neighbor2));
		float[] vectorNeighbor1Neighbor = ToolsMath.normalizeVector( ToolsMath.v1minusV2(neighbor, neighbor1));

		float[] vectorAtomHoldinHtoAddedH1 = ToolsMath.multiplyByScalar(ToolsMath.normalizeVector(vectorNeighbor2Neighbor), distanceNH);
		float[] coordH1 = ToolsMath.v1plusV2(coordsAtomToProtonate, vectorAtomHoldinHtoAddedH1);

		float[] vectorNH1toH2NH1 = ToolsMath.multiplyByScalar(ToolsMath.normalizeVector(vectorNeighbor1Neighbor), distanceNH);
		float[] coordH2 = ToolsMath.v1plusV2(coordsAtomToProtonate, vectorNH1toH2NH1);

		twoHydrogen.add(coordH1);
		twoHydrogen.add(coordH2);

		return twoHydrogen;
	}
}

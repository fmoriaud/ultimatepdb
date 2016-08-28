package structure;

import org.openscience.cdk.interfaces.IAtom;

public class AtomContainerTools {

	
	
	public static double[] make3dCoordFromIAtom(IAtom iatom){

		double[] coords = new double[3];
		if (iatom.getPoint2d() != null){
			coords[0] = iatom.getPoint2d().x;
			coords[1] = iatom.getPoint2d().y;
			coords[2] = 0.0;
		}
		if (iatom.getPoint3d() != null){
			coords[0] = iatom.getPoint3d().x;
			coords[1] = iatom.getPoint3d().y;
			coords[2] = iatom.getPoint3d().z;
		}
		return coords;
	}
}

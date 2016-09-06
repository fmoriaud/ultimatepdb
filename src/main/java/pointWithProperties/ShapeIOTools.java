package pointWithProperties;

import java.util.ArrayList;
import java.util.List;

import structure.ExceptionInMyStructurePackage;
import structure.MyAtom;
import structure.MyAtomIfc;
import structure.MyBondIfc;
import structure.MyMonomer;
import structure.MyMonomerIfc;
import structure.MyMonomerType;



public class ShapeIOTools{


	public static MyMonomerIfc convertAListOfPointIfcToAPseudoPDBFileForVisualization(List<PointIfc> listPoints, String elementNameForColor) throws ExceptionInMyStructurePackage{

		List<MyAtomIfc> listAtom = new ArrayList<>();
		int pointId = 0;
		for( PointIfc point: listPoints) { 
			MyAtomIfc atom = new MyAtom(elementNameForColor.toCharArray(), point.getCoords(), elementNameForColor.toCharArray(), pointId);
			MyBondIfc[] bonds = new MyBondIfc[0];
			atom.setBonds(bonds);
			listAtom.add(atom);
		}

		MyAtomIfc[] myAtoms = listAtom.toArray(new MyAtomIfc[listAtom.size()]);

		MyMonomerIfc myMonomer = new MyMonomer(myAtoms, "XXX".toCharArray(), 999, MyMonomerType.AMINOACID, " ".toCharArray()[0]);
		for (MyAtomIfc atom: myAtoms){
			atom.setParent(myMonomer);
		}
		return myMonomer;
	}



//	public static MyMonomerIfc convertAListOfPointIfcToAPseudoPDBFileForVisualization(List<PointIfc> listPoints, Element element){
//
//		String elementString = element.toString();
//		return convertAListOfPointIfcToAPseudoPDBFileForVisualization(listPoints, elementString);
//	}
}

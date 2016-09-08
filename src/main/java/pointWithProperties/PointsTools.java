package pointWithProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mystructure.MyAtomIfc;
import mystructure.MyChainIfc;
import mystructure.MyMonomerIfc;

public class PointsTools {

	public static List<PointIfc> createListOfPointIfcFromShape(CollectionOfPointsWithPropertiesIfc  shape){

		List<PointIfc> listPoints = new ArrayList<>();
		for (int i=0; i<shape.getSize(); i++){
			listPoints.add(shape.getPointFromId(i).getCoords());
		}
		return listPoints;
	}
	
	
	
	public static List<PointIfc> createListOfPointIfcFromShape(Map<Integer, PointWithPropertiesIfc>  shape){

		List<PointIfc> listPoints = new ArrayList<>();
		for (Entry<Integer, PointWithPropertiesIfc> entry: shape.entrySet()){
			listPoints.add(entry.getValue().getCoords());
		}
		return listPoints;
	}
	
	public static PointIfc computeLigandBarycenter(List<? extends PointIfc> ligandPoints){

		float[] coord = new float[3];
		PointIfc barycenter = new Point(coord);

		for (PointIfc point: ligandPoints){
			barycenter.getCoords()[0] += point.getCoords()[0];
			barycenter.getCoords()[1] += point.getCoords()[1];
			barycenter.getCoords()[2] += point.getCoords()[2];
		}
		for(int i=0; i<3; i++){
			barycenter.getCoords()[i] /= ligandPoints.size();
		}
		return barycenter;
	}



	public static List<PointIfc> createListOfPointIfcFromPeptide(MyChainIfc peptide){

		List<PointIfc> listOfPointsFromPeptide = new ArrayList<>();

		for (MyMonomerIfc monomer: peptide.getMyMonomers()){
			List<PointIfc> listPoint =  createListOfPointIfcFromMonomer(monomer);
			listOfPointsFromPeptide.addAll(listPoint);
		}

		return listOfPointsFromPeptide;
	}


	// TODO FMM move to PointIfc[]
	public static List<PointIfc> createListOfPointIfcFromMonomer(MyMonomerIfc monomer){

		List<PointIfc> listOfPoints = new ArrayList<>();
		for (MyAtomIfc atom: monomer.getMyAtoms()){
			float[] coords = atom.getCoords();
			PointIfc point = new Point(coords);
			listOfPoints.add(point);
		}
		return listOfPoints;
	}



	//	public static List<PointIfc> createListOfPointIfcFromCalphaPeptide(MyStructureIfc myStructure){
	//		List<PointIfc> listOfPointsFromCalphaPeptide = new ArrayList<>();
	//		for (MyChainIfc chain: myStructure.getAllAminochains()){
	//			listOfPointsFromCalphaPeptide.addAll(createListOfPointIfcFromCalphaPeptide(chain));
	//		}
	//		return listOfPointsFromCalphaPeptide;
	//	}



	public static List<PointIfc> createListOfPointIfcFromCalphaPeptide(MyChainIfc peptide){

		List<PointIfc> listOfPointsFromCalphaPeptide = new ArrayList<>();

		for (MyMonomerIfc monomer: peptide.getMyMonomers()){
			for (MyAtomIfc atom: monomer.getMyAtoms()){
				if (String.valueOf(atom.getAtomName()).equals("CA")){
					float[] coords = atom.getCoords();
					PointIfc point = new Point(coords);
					listOfPointsFromCalphaPeptide.add(point);
				}
			}
		}
		return listOfPointsFromCalphaPeptide;
	}
}

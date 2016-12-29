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
package pointWithProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mystructure.MyAtomIfc;
import mystructure.MyChainIfc;
import mystructure.MyMonomerIfc;

public class PointsTools {
    //-------------------------------------------------------------
    // Static methods
    //-------------------------------------------------------------
    public static List<PointIfc> createListOfPointIfcFromShape(CollectionOfPointsWithPropertiesIfc shape) {

        List<PointIfc> listPoints = new ArrayList<>();
        for (int i = 0; i < shape.getSize(); i++) {
            listPoints.add(shape.getPointFromId(i).getCoords());
        }
        return listPoints;
    }


    public static List<PointIfc> createListOfPointIfcFromShape(Map<Integer, PointWithPropertiesIfc> shape) {

        List<PointIfc> listPoints = new ArrayList<>();
        for (Entry<Integer, PointWithPropertiesIfc> entry : shape.entrySet()) {
            listPoints.add(entry.getValue().getCoords());
        }
        return listPoints;
    }


    public static PointIfc computeLigandBarycenter(List<? extends PointIfc> ligandPoints) {

        float[] coord = new float[3];
        PointIfc barycenter = new Point(coord);

        for (PointIfc point : ligandPoints) {
            barycenter.getCoords()[0] += point.getCoords()[0];
            barycenter.getCoords()[1] += point.getCoords()[1];
            barycenter.getCoords()[2] += point.getCoords()[2];
        }
        for (int i = 0; i < 3; i++) {
            barycenter.getCoords()[i] /= ligandPoints.size();
        }
        return barycenter;
    }


    public static List<PointIfc> createListOfPointIfcFromPeptide(MyChainIfc peptide) {

        List<PointIfc> listOfPointsFromPeptide = new ArrayList<>();

        for (MyMonomerIfc monomer : peptide.getMyMonomers()) {
            List<PointIfc> listPoint = createListOfPointIfcFromMonomer(monomer);
            listOfPointsFromPeptide.addAll(listPoint);
        }

        return listOfPointsFromPeptide;
    }


    public static List<PointIfc> createListOfPointIfcFromMonomer(MyMonomerIfc monomer) {

        List<PointIfc> listOfPoints = new ArrayList<>();
        for (MyAtomIfc atom : monomer.getMyAtoms()) {
            float[] coords = atom.getCoords();
            PointIfc point = new Point(coords);
            listOfPoints.add(point);
        }
        return listOfPoints;
    }
}

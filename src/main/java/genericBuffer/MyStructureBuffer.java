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
package genericBuffer;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.util.Pair;

import mystructure.MyStructureIfc;


public class MyStructureBuffer {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private int capacity;
    private List<Pair<String, MyStructureIfc>> queue = new LinkedList<Pair<String, MyStructureIfc>>();


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public MyStructureBuffer(int capacity) {

        this.capacity = capacity;
    }


    //-------------------------------------------------------------
    // Interface & Public methods
    //-------------------------------------------------------------
    public synchronized MyStructureIfc getStructure(char[] fourLettercode) {

        String fourLettercodeString = String.valueOf(fourLettercode);
        for (Pair<String, MyStructureIfc> storeElement : queue) {
            if (storeElement.getFirst().equals(fourLettercodeString)) {
                return storeElement.getValue();
            }
        }
        return null;
    }


    public synchronized void putStructure(MyStructureIfc myStructure) {

        boolean alreadyStored = false;
        for (Pair<String, MyStructureIfc> storeElement : queue) {
            if (storeElement.getFirst().equals(String.valueOf(myStructure.getFourLetterCode()))) {
                alreadyStored = true;
            }
        }
        if (alreadyStored == false) {
            if (queue.size() == capacity) {
                queue.remove(queue.remove(0));
            }
            Pair<String, MyStructureIfc> newElementToStore = new Pair<>(String.valueOf(myStructure.getFourLetterCode()), myStructure);
            queue.add(newElementToStore);
        }
    }
}
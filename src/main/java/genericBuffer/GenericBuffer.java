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
import java.util.Queue;

public class GenericBuffer<T> {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private Queue<T> queue = new LinkedList<T>();
    private int capacity;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public GenericBuffer(int capacity) {

        this.capacity = capacity;
    }


    //-------------------------------------------------------------
    // Interface & Public methods
    //-------------------------------------------------------------
    public synchronized void put(T item) throws InterruptedException {

        while (1 + queue.size() > capacity) {
            wait();
        }
        queue.add(item);
        notifyAll();
    }


    public synchronized T get() throws InterruptedException {

        while (1 > queue.size()) {
            wait();
        }
        T item = queue.remove();
        notifyAll();
        return item;
    }


    public synchronized int getSize() {

        return queue.size();
    }
}

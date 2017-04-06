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
package multithread;

import database.DoMyDbTaskIfc;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.concurrent.Callable;

public class StoreInSequenceDbPDBFileCallable implements Callable<Boolean> {
    //-------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------
    private DoMyDbTaskIfc doMyDbTaskIfc;
    private Connection connexion;
    private String pathToFile;


    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public StoreInSequenceDbPDBFileCallable(DoMyDbTaskIfc doMyDbTaskIfc, Connection connexion, String pathToFile) {

        this.doMyDbTaskIfc = doMyDbTaskIfc;
        this.connexion = connexion;
        this.pathToFile = pathToFile;
    }


    //-------------------------------------------------------------
    // Public & Override methods
    //-------------------------------------------------------------
    @Override
    public Boolean call() throws Exception {

        boolean success = doMyDbTaskIfc.doAndReturnSuccessValue(connexion, pathToFile);
        //System.out.println("doMyDbTaskIfc.doAndReturnSuccessValue = " + success);
        return success;
    }
}

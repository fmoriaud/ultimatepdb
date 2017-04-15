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
package protocols;

import database.HashTablesTools;
import database.HitInSequenceDb;
import database.SequenceTools;
import mystructure.MyChainIfc;
import parameters.AlgoParameters;
import shape.ShapeContainerIfc;
import shape.ShapeContainerWithPeptide;
import shapeBuilder.ShapeBuildingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;

public class RunProtocolBindingVsFoldingWithExecutor {
    //-------------------------------------------------------------
    // Static methods
    //-------------------------------------------------------------
    public static void main(String[] args) throws ParsingConfigFileException {

        AlgoParameters algoParameters = ProtocolTools.prepareAlgoParameters();

        FileHandler fh = null;
        try {
            fh = new FileHandler(algoParameters.getPATH_TO_RESULT_FILES() + ControllerLoger.LOGGER_FILE_NAME);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        fh.setFormatter(new OptimizerFormater());
        ControllerLoger.logger.addHandler(fh);

        ShapeContainerDefined query = new ShapecontainerDefinedByWholeChain("1be9".toCharArray(), "B".toCharArray(), algoParameters);
        ShapeContainerIfc queryShape = null;
        try {
            queryShape = query.getShapecontainer();
        } catch (ShapeBuildingException e) {
            e.printStackTrace();
            System.exit(0);
        }

        List<ShapeContainerDefined> targets = buildTargets(queryShape, algoParameters);

        ProtocolBindingVsFoldingWithExecutor protocol = new ProtocolBindingVsFoldingWithExecutor(queryShape, targets, algoParameters);
        protocol.run();
    }


    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------
    private static List<ShapeContainerDefined> buildTargets(ShapeContainerIfc queryShape, AlgoParameters algoParameters) {

        List<ShapeContainerDefined> targets = new ArrayList<>();

        List<char[]> sequenceToFind = null;
        if (queryShape instanceof ShapeContainerWithPeptide) {
            ShapeContainerWithPeptide shapeContainerWithPeptide = (ShapeContainerWithPeptide) queryShape;
            sequenceToFind = shapeContainerWithPeptide.getPeptideSequence();
        } else {
            System.out.println("queryShape is not instanceof ShapeContainerWithPeptide. Program terminated.");
            System.exit(0);
        }
        String sequenceToFindAsString = ProtocolTools.makeSequenceString(sequenceToFind);

        int peptideLength = sequenceToFindAsString.length() / 3;

        boolean useSimilarSequences = false;
        List<HitInSequenceDb> hitsInDatabase = SequenceTools.find(HashTablesTools.tableSequenceName, HashTablesTools.tableSequenceFailureName, peptideLength, 1000, sequenceToFindAsString, useSimilarSequences);
        System.out.println("Found " + hitsInDatabase.size() + "  sequence hits in the Sequence Database");

        /*
        if (queryShape instanceof ShapeContainerWithPeptide) {

            ShapeContainerWithPeptide query = (ShapeContainerWithPeptide) queryShape;
            MyChainIfc ligand = query.getPeptide();
            List<HitInSequenceDb> hitsInDatabaseUsingInteractions = SequenceTools.findUsingQueryPeptide(ligand, peptideLength, 1000, sequenceToFindAsString, algoParameters);
            System.out.println("Found " + hitsInDatabaseUsingInteractions.size() + "  sequence hits in the Sequence Database using contacts");
            System.out.println("Found " + hitsInDatabase.size() + "  sequence hits in the Sequence Database using equivalent");

            hitsInDatabase = hitsInDatabaseUsingInteractions;
        }
        */

        String fourLetterCodeTarget;
        String chainIdFromDB;

        for (HitInSequenceDb hitInSequenceDb : hitsInDatabase) {

            fourLetterCodeTarget = hitInSequenceDb.getFourLetterCode();
            chainIdFromDB = hitInSequenceDb.getChainIdFromDB();
            List<Integer> listRankIds = hitInSequenceDb.getListRankIds();

            for (int i = 0; i < listRankIds.size(); i++) {

                Integer startingRankId = listRankIds.get(i);
                ShapeContainerDefined target = new ShapecontainerDefinedBySegmentOfChain(fourLetterCodeTarget.toCharArray(), chainIdFromDB.toCharArray(), startingRankId, peptideLength, algoParameters);
                targets.add(target);

            }
        }
        return targets;
    }
}

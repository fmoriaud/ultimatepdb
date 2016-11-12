package shapeBuilder;

import java.util.Arrays;

import mystructure.MyChain;
import mystructure.MyChainIfc;
import mystructure.MyMonomerIfc;
import mystructure.MyStructureIfc;
import parameters.AlgoParameters;


public class StructureLocalToBuildShapeHetAtm implements StructureLocalToBuildShapeIfc {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private MyStructureIfc myStructureGlobalBrut;

    private MyStructureIfc myStructureLocal;
    private MyMonomerIfc hetAtomsGroup;

    private char[] hetAtomsLigandId;
    private int occurrenceId;
    private AlgoParameters algoParameters;

    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public StructureLocalToBuildShapeHetAtm(MyStructureIfc myStructureGlobalBrut,
                                            char[] hetAtomsLigandId, int occurrenceId, AlgoParameters algoParameters) {

        this.myStructureGlobalBrut = myStructureGlobalBrut;
        this.hetAtomsLigandId = hetAtomsLigandId;
        this.occurrenceId = occurrenceId;
        this.algoParameters = algoParameters;
    }


    //-------------------------------------------------------------
    // Interface & Public methods
    //-------------------------------------------------------------
    public void compute() throws ShapeBuildingException {

        hetAtomsGroup = findHetAtomLigand(hetAtomsLigandId, occurrenceId, myStructureGlobalBrut);

        if (hetAtomsGroup == null) {
            System.out.println("ligand hetatm not found");
            String message = "ligand hetatm not found : " + String.valueOf(hetAtomsLigandId) + " " + occurrenceId + " in " + String.valueOf(myStructureGlobalBrut.getFourLetterCode());
            ShapeBuildingException exception = new ShapeBuildingException(message);
            throw exception;
        }

        myStructureLocal = StructureLocalTools.makeStructureLocalAroundAndExcludingMyMonomersFromInputMyChain(myStructureGlobalBrut, hetAtomsGroup, algoParameters);

    }


    //-------------------------------------------------------------
    // Implementation
    //-------------------------------------------------------------
    private MyMonomerIfc findHetAtomLigand(char[] hetAtomsLigandId, int occurrenceId, MyStructureIfc myStructure) {

        int countOfFoundRightHetAtomsLigand = 0;
        MyChainIfc[] allHetAtomsChains = myStructure.getAllHetatmchains(); // then the hetatm which are part of aminochains (e.g. 2qlj E)
        for (MyChainIfc myChain : allHetAtomsChains) {
            for (MyMonomerIfc myMonomer : myChain.getMyMonomers()) {
                if (Arrays.equals(myMonomer.getThreeLetterCode(), hetAtomsLigandId)) {
                    countOfFoundRightHetAtomsLigand += 1;
                    if (countOfFoundRightHetAtomsLigand == occurrenceId) {
                        return myMonomer;
                    }
                }
            }
        }
        return null;
    }


    //-------------------------------------------------------------
    // Getters & Setters
    //-------------------------------------------------------------
    @Override
    public MyStructureIfc getMyStructureLocal() {
        return myStructureLocal;
    }


    public MyMonomerIfc getHetAtomsGroup() {
        return hetAtomsGroup;
    }
}

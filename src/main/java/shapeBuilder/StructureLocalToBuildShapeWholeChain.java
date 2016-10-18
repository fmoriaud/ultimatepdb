package shapeBuilder;

import mystructure.MyChainIfc;
import mystructure.MyStructureIfc;
import parameters.AlgoParameters;


public class StructureLocalToBuildShapeWholeChain implements StructureLocalToBuildShapeIfc {
    //-------------------------------------------------------------
    // Class variables
    //-------------------------------------------------------------
    private MyStructureIfc myStructureGlobalBrut;

    private char[] chainId;

    private MyChainIfc ligand;
    private MyStructureIfc myStructureLocal;

    private AlgoParameters algoParameters;


    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public StructureLocalToBuildShapeWholeChain(MyStructureIfc myStructureGlobalBrut,
                                                char[] chainId, AlgoParameters algoParameters) {

        this.myStructureGlobalBrut = myStructureGlobalBrut;

        this.chainId = chainId;
        this.algoParameters = algoParameters;
    }


    //-------------------------------------------------------------
    // Interface & Public methods
    //-------------------------------------------------------------
    public void compute() throws ShapeBuildingException {

        ligand = myStructureGlobalBrut.getAminoMyChain(chainId);

        myStructureLocal = StructureLocalTools.makeStructureLocalAroundAndExcludingMyMonomersFromInputMyChain(myStructureGlobalBrut, ligand, algoParameters); // to skip some monomers at tip is not implemented

        if (myStructureLocal.getAllAminochains().length == 0) {
            ShapeBuildingException exception = new ShapeBuildingException("getShapeAroundAChain return no amino chain: likely that the chain has no neighboring chain in that case");
            throw exception;
        }
    }


    //-------------------------------------------------------------
    // Getters & Setters
    //-------------------------------------------------------------
    public MyChainIfc getLigand() {
        return ligand;
    }


    @Override
    public MyStructureIfc getMyStructureLocal() {
        return myStructureLocal;
    }
}
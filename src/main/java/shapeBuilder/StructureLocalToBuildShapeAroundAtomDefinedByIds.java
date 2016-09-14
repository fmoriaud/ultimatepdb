package shapeBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import parameters.AlgoParameters;
import parameters.QueryAtomDefinedByIds;
import mystructure.ExceptionInMyStructurePackage;
import mystructure.MyAtomIfc;
import mystructure.MyChain;
import mystructure.MyChainIfc;
import mystructure.MyMonomerIfc;
import mystructure.MyStructureIfc;

public class StructureLocalToBuildShapeAroundAtomDefinedByIds implements StructureLocalToBuildShapeIfc {
    //-------------------------------------------------------------
    // Class variables
    //-----------------------------------------------------
    private MyStructureIfc myStructureGlobalBrut;
    private List<QueryAtomDefinedByIds> queryAtomsDefinedByIds;
    private List<String> chainToIgnore;
    private MyStructureIfc myStructureLocal;
    private AlgoParameters algoParameters;


    //-------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------
    public StructureLocalToBuildShapeAroundAtomDefinedByIds(MyStructureIfc myStructureGlobalBrut,
                                                            List<QueryAtomDefinedByIds> queryAtomsDefinedByIds, AlgoParameters algoParameters, List<String> chainToIgnore) {

        this.chainToIgnore = chainToIgnore;
        this.myStructureGlobalBrut = myStructureGlobalBrut;
        this.queryAtomsDefinedByIds = queryAtomsDefinedByIds;
        this.algoParameters = algoParameters;

    }


    //-------------------------------------------------------------
    // Interface & Public methods
    //-------------------------------------------------------------
    public void compute() throws ShapeBuildingException {

        List<MyMonomerIfc> monomersContainingAtomsDefinedByIds = findMyMonomersOnlyInAminoChainsContainingAtomsDefinedByIds(myStructureGlobalBrut, queryAtomsDefinedByIds);
        if (monomersContainingAtomsDefinedByIds.isEmpty()){
            return;
        }
        MyChainIfc correspondingChain = new MyChain(monomersContainingAtomsDefinedByIds);
        myStructureLocal = makeStructureLocalAroundAndWithChain(correspondingChain, chainToIgnore);
    }


    //-------------------------------------------------------------
    // Implementation
    //-------------------------------------------------------------
    private MyStructureIfc makeStructureLocalAroundAndWithChain(MyChainIfc myChain, List<String> chainToIgnore) {

        Set<MyMonomerIfc> queryMonomers = StructureLocalTools.makeMyMonomersLocalAroundAndWithChain(myChain);
        MyStructureIfc myStructureLocal;
        try {
            myStructureLocal = myStructureGlobalBrut.cloneWithSameObjectsWhileKeepingOnlyMyMonomerInThisSet(queryMonomers);
        } catch (ExceptionInMyStructurePackage e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        ShapeBuildingTools.deleteChains(chainToIgnore, myStructureLocal);

        return myStructureLocal;
    }


    private List<MyMonomerIfc> findMyMonomersOnlyInAminoChainsContainingAtomsDefinedByIds(MyStructureIfc myStructure, List<QueryAtomDefinedByIds> queryAtomsDefinedByIds) {

        List<MyMonomerIfc> monomersFound = new ArrayList<>();

        for (QueryAtomDefinedByIds atomDefinedByIds : queryAtomsDefinedByIds) {

            char[] chainIdToFind = atomDefinedByIds.getChainQuery().toCharArray();
            MyChainIfc[] chains = myStructure.getAllChainsRelevantForShapeBuilding();
            for (MyChainIfc foundMyChain : chains) {
                if (Arrays.equals(foundMyChain.getChainId(), chainIdToFind)) {
                    int residueIdToFind = atomDefinedByIds.getResidueId();
                    MyMonomerIfc foundMyMonomer = foundMyChain.getMyMonomerFromResidueId(residueIdToFind);
                    char[] atomNameToFind = atomDefinedByIds.getAtomName().toCharArray();
                    MyAtomIfc foundMyAtom = foundMyMonomer.getMyAtomFromMyAtomName(atomNameToFind);
                    if (foundMyAtom != null) {
                        monomersFound.add(foundMyMonomer);
                        break;
                    }
                }
            }
            if (monomersFound.isEmpty()) {
                System.out.println("Monomer not found ");
            }
        }
        return monomersFound;
    }


    //-------------------------------------------------------------
    // Getters & Setters
    //-------------------------------------------------------------
    @Override
    public MyStructureIfc getMyStructureLocal() {
        return myStructureLocal;
    }
}

package mystructure;

import parameters.AlgoParameters;

/**
 * Created by Fabrice on 21/09/16.
 */
public class Merger {


    private MyStructureIfc merge;

    public Merger(MyChainIfc myChain1, MyChainIfc myChain2, AlgoParameters algoParameters) {


        // assume two different amino chain
        MyChainIfc[] aminoChains = new MyChainIfc[2];
        aminoChains[0] = myChain1;
        aminoChains[1] = myChain2;

        Cloner cloner = new Cloner(aminoChains, algoParameters);
        merge = cloner.getClone();

    }


    public MyStructureIfc getMerge() {
        return merge;
    }
}

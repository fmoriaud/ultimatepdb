package gui;

import parameters.AlgoParameters;
import protocols.ParsingConfigFileException;
import protocols.ProtocolTools;

public class Controller {


    private AlgoParameters algoParameters;

    /**
     * The Controller takes all GUI input and process them
     * So JUnit tests can be done on the Controller
     */
    public Controller() {

        try {
            algoParameters = ProtocolTools.prepareAlgoParameters();
        } catch (ParsingConfigFileException e) {
            e.printStackTrace();
        }

    }
}

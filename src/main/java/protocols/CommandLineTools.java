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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import parameters.AlgoParameters;
import parameters.QueryAtomDefinedByIds;
import mystructure.EnumMyReaderBiojava;

public class CommandLineTools {
    //-------------------------------------------------------------
    // Static methods
    //-------------------------------------------------------------
    public static AlgoParameters analyzeArgs(String[] args, EnumMyReaderBiojava enumMyReaderBiojava) throws CommandLineException, ParsingConfigFileException {

        if (args.length != 1) {
            String message = "There can be only one args for this command line: the path to the ultimateParam file";
            CommandLineException exception = new CommandLineException(message);
            throw exception;
        }
        String pathToUltimateXmlFile = args[0];
        return generateModifiedAlgoParameters(pathToUltimateXmlFile, enumMyReaderBiojava);
    }


    public static AlgoParameters generateModifiedAlgoParameters(String pathToUltimateXmlFile, EnumMyReaderBiojava enumMyReaderBiojava) throws ParsingConfigFileException {

        // Load parameters
        AlgoParameters algoParameters = new AlgoParameters();

        AlgoParameters modifiedAlgoParameters = readParameterFile(pathToUltimateXmlFile, algoParameters, enumMyReaderBiojava);

        return modifiedAlgoParameters;
    }


    // -------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------
    private static String returnContent(String tag, Element rootElement) throws ParsingConfigFileException {

        List<Element> element1 = rootElement.elements(tag);

        if (element1.size() != 1) {
            String message = "tag not found : " + tag;
            ParsingConfigFileException exception = new ParsingConfigFileException(message);
            throw exception;
        }

        return element1.get(0).getText();
    }


    private static AlgoParameters readParameterFile(String fileName, AlgoParameters algoParameters, EnumMyReaderBiojava enumMyReaderBiojava) throws ParsingConfigFileException {

        Document document = null;
        try {
            SAXReader reader = new SAXReader();
            File fileToRead = new File(fileName);
            document = reader.read(fileToRead);
        } catch (Exception e) {
            String message = "Unspecified error in parsing the config file";
            ParsingConfigFileException exception = new ParsingConfigFileException(message);
            throw exception;
        }

        if (document == null) {
            String message = "Unspecified error in parsing the config file";
            ParsingConfigFileException exception = new ParsingConfigFileException(message);
            throw exception;
        }

        Element rootElement = document.getRootElement();

        // Admin
        List<Element> elementsBuildQueryBoolean = rootElement.elements("ADMIN").get(0).elements("BUILD_SEQ_DB");
        if (elementsBuildQueryBoolean.size() == 1) {
            String buildQueryBooleanAsString = elementsBuildQueryBoolean.get(0).getText();
            Boolean buildQueryBoolean = null;
            if (buildQueryBooleanAsString.equals("TRUE")) {
                buildQueryBoolean = true;
            }
            if (buildQueryBooleanAsString.equals("FALSE")) {
                buildQueryBoolean = false;
            }

            List<Element> elementsPath = rootElement.elements("ADMIN").get(0).elements("PATH_TO_TARGET_LIST_FILE_TO_BE_PROCESSED_BUILD_SEQ_DB");
            if (elementsBuildQueryBoolean.size() == 1) {
                String pathtoFile = elementsPath.get(0).getText();
                algoParameters.setPATH_TO_TARGET_LIST_FILE_TO_BE_PROCESSED_BUILD_SEQ_DB(pathtoFile);
            }
        }


        algoParameters.setPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER(returnContent("PATH_TO_REMEDIATED_PDB_MMCIF_FOLDER", rootElement));
        algoParameters.setPATH_TO_CHEMCOMP_FOLDER(returnContent("PATH_TO_CHEMCOMP_FOLDER", rootElement));
        algoParameters.setPATH_TO_RESULT_FILES(returnContent("PATH_TO_RESULT_FILES", rootElement));

        List<Element> optimizeElements = rootElement.elements("OPTIMIZE_HIT_GEOMETRY");
        String optimizeString = optimizeElements.get(0).getText();
        Boolean optimize = null;
        if (optimizeString.equals("TRUE")) {
            optimize = true;
        }
        if (optimizeString.equals("FALSE")) {
            optimize = false;
        }
        algoParameters.setOPTIMIZE_HIT_GEOMETRY(optimize);

        String threadCountAsString = returnContent("SHAPE_COMPARISON_THREAD_COUNT", rootElement);
        algoParameters.setSHAPE_COMPARISON_THREAD_COUNT(Integer.valueOf(threadCountAsString));
        String subThreadCountAsString = returnContent("SUB_THREAD_COUNT_FORK_AND_JOIN", rootElement);
        algoParameters.setSUB_THREAD_COUNT_FORK_AND_JOIN(Integer.valueOf(subThreadCountAsString));

        // Parse Query Informations
        List<Element> queryTag = rootElement.elements("QUERY");
        algoParameters.setQUERY_PDB_FOUR_LETTER_CODE(queryTag.get(0).elements("QUERY_PDB_FOUR_LETTER_CODE").get(0).getText());

        String queryType = rootElement.elements("QUERY").get(0).elements("QUERY_TYPE").get(0).getText();
        switch (queryType) {
            case "WHOLE_CHAIN":
                algoParameters.setQUERY_CHAIN_ID(rootElement.elements("QUERY").get(0).elements("WHOLE_CHAIN").get(0).elements("QUERY_CHAIN_ID").get(0).getText());
                ;
                break;
            case "SEGMENT_OF_CHAIN":
                algoParameters.setSTARTING_RANK_ID(Integer.valueOf(rootElement.elements("QUERY").get(0).elements("SEGMENT_OF_CHAIN").get(0).elements("STARTING_RANK_ID").get(0).getText()));
                algoParameters.setQUERY_CHAIN_ID(rootElement.elements("QUERY").get(0).elements("SEGMENT_OF_CHAIN").get(0).elements("QUERY_CHAIN_ID").get(0).getText());
                algoParameters.setPEPTIDE_LENGTH(Integer.valueOf(rootElement.elements("QUERY").get(0).elements("SEGMENT_OF_CHAIN").get(0).elements("PEPTIDE_LENGTH").get(0).getText()));
                ;
                break;
            case "HETATM":
                algoParameters.setOCCURENCE_ID(Integer.valueOf(rootElement.elements("QUERY").get(0).elements("HETATM").get(0).elements("OCCURENCE_ID").get(0).getText()));
                algoParameters.setQUERY_PDB_THREE_LETTER_CODE(rootElement.elements("QUERY").get(0).elements("HETATM").get(0).elements("QUERY_PDB_THREE_LETTER_CODE").get(0).getText());
                algoParameters.setQUERY_CHAIN_ID(rootElement.elements("QUERY").get(0).elements("HETATM").get(0).elements("QUERY_CHAIN_ID").get(0).getText());
                ;
                break;
            case "ATOMIDS_WITHIN_SHAPE":
                List<Element> queryAtoms = rootElement.elements("QUERY").get(0).elements("ATOMIDS_WITHIN_SHAPE").get(0).elements("QUERY_ATOMS_DEFINED_BY_IDS").get(0).elements("ATOM_DEFINED_BY_IDS");
                List<QueryAtomDefinedByIds> listAtoms = new ArrayList<>();
                for (int i = 0; i < queryAtoms.size(); i++) {
                    Element queryAtom = queryAtoms.get(i);
                    String atomChainId = queryAtom.elements("CHAINID").get(0).getText();
                    int atomMonomerId = Integer.valueOf(queryAtom.elements("MONOMERID").get(0).getText());
                    String atomId = queryAtom.elements("ATOMID").get(0).getText();
                    float radius = Float.valueOf(queryAtom.elements("RADIUS_FOR_QUERY_ATOMS_DEFINED_BY_IDS").get(0).getText());
                    QueryAtomDefinedByIds atom = new QueryAtomDefinedByIds(algoParameters.getQUERY_PDB_FOUR_LETTER_CODE(), atomChainId, atomMonomerId, atomId, radius);
                    listAtoms.add(atom);
                }
                algoParameters.setQUERY_ATOMS_DEFINED_BY_IDS(listAtoms);
                ;
                break;
        }
        algoParameters.setQUERY_TYPE(queryType);

        String chainsToIgnore = queryTag.get(0).elements("CHAIN_TO_IGNORE").get(0).getText();
        String cleaned = chainsToIgnore.replaceAll("\\s+", "");
        StringTokenizer tok = new StringTokenizer(cleaned, ",");
        List<String> chainsToDelete = new ArrayList<>();
        while (tok.hasMoreElements()) {
            String next = (String) tok.nextElement();
            chainsToDelete.add(next);
        }
        algoParameters.setCHAIN_TO_IGNORE(chainsToDelete);

        return algoParameters;
    }
}


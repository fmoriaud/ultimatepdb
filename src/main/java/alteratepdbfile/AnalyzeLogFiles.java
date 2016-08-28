package alteratepdbfile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class AnalyzeLogFiles {

	//------------------------
	// Class variables
	//------------------------
	private List<String> pathsToLogFiles;



	// -------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------
	public AnalyzeLogFiles(List<String> pathsToLogFiles){

		this.pathsToLogFiles = pathsToLogFiles;

	}


	// -------------------------------------------------------------------
	// Public methods
	// -------------------------------------------------------------------
	public void analyze(){

		double rmsdLigand;
		double cost;
		double ratioPairedPointToQueryPoints;
		List<Double> listCost = new ArrayList<>();
		List<Double> listRmsdLigand = new ArrayList<>();
		List<Double> listRatioPairedPointToQueryPoints = new ArrayList<>();

		List<String> listHitsName = new ArrayList<>();
		for (String pathToLogfile: pathsToLogFiles){

			try(BufferedReader br = new BufferedReader(new FileReader(pathToLogfile))) {
				String line = br.readLine();
				System.out.println("line = " + line);

				while (line != null ) {

					if (line.isEmpty()){
						line = br.readLine();
						continue;
					}
					String fourLetterCode = null;
					String chain = null;
					String ResidueId = null;

					if (line.contains("cost")){
						String[] lineSplitWithCost = line.split("cost");
						StringTokenizer tok = new StringTokenizer(lineSplitWithCost[1], " "); 

						List<String> splittedLine = new ArrayList<>();
						while ( tok.hasMoreElements() )  
						{  
							String next = (String) tok.nextElement();
							splittedLine.add(next);
						}
						for (int i=0; i<splittedLine.size(); i++){
							//System.out.println(i + "  " + splittedLine.get(i));
							if (i == 1){
								cost = Double.valueOf(splittedLine.get(i));
								listCost.add(cost);

							}
						}
					}

					if (line.contains("rmsd")){
						String[] lineSplitWithCost = line.split("rmsd");
						StringTokenizer tok = new StringTokenizer(lineSplitWithCost[1], " "); 

						List<String> splittedLine = new ArrayList<>();
						while ( tok.hasMoreElements() )  
						{  
							String next = (String) tok.nextElement();
							splittedLine.add(next);
						}
						for (int i=0; i<splittedLine.size(); i++){
							//System.out.println(i + "  " + splittedLine.get(i));
							if (i == 2){
								rmsdLigand = Double.valueOf(splittedLine.get(i));
								listRmsdLigand.add(rmsdLigand);
							}
							if (i == 5){
								ratioPairedPointToQueryPoints = Double.valueOf(splittedLine.get(i));
								listRatioPairedPointToQueryPoints.add(ratioPairedPointToQueryPoints);
							}
						}
					}

					if (line.contains("PDB = ")){
						String[] lineSplitWithCost = line.split("PDB = ");
						StringTokenizer tok = new StringTokenizer(lineSplitWithCost[1], " "); 

						List<String> splittedLine = new ArrayList<>();
						while ( tok.hasMoreElements() )  
						{  
							String next = (String) tok.nextElement();
							splittedLine.add(next);
						}

						for (int i=0; i<splittedLine.size(); i++){
							//System.out.println(i + "  " + splittedLine.get(i));
							if (i == 0){
								fourLetterCode = splittedLine.get(i);
							}
							if (i == 4){
								chain = splittedLine.get(i);
							}
							if (i == 7){
								ResidueId = splittedLine.get(i);
							}
						}
						listHitsName.add(fourLetterCode + "_" + chain + " " + ResidueId);

					}


					line = br.readLine();
				}

			} catch (IOException e) {

				e.printStackTrace();
			}
		}


		for (int i=0; i<listCost.size(); i++){
			System.out.println(listHitsName.get(i) + "  " + listCost.get(i) + "  " + listRatioPairedPointToQueryPoints.get(i) + "  " + listRmsdLigand.get(i));
		}

		double cutoffCost = 0.04;
		double cutoffCoverage = 0.80;
		System.out.println("Desired hits : ");
		for (int i=0; i<listCost.size(); i++){
			//if (listCost.get(i) < cutoffCost && listRatioPairedPointToQueryPoints.get(i)  > cutoffCoverage){
			if (listRmsdLigand.get(i) < 2.0){	
				System.out.println(listHitsName.get(i) + "  " + listCost.get(i) + "  " + listRatioPairedPointToQueryPoints.get(i) + "  " + listRmsdLigand.get(i));
			}
		}


	}


}
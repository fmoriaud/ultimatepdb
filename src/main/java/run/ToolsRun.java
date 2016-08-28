package run;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ToolsRun {

	public static List<Pair3LetterCodeAndMW> realListHetatmQueriesFromTextFile(String pathToFile){

		List<Pair3LetterCodeAndMW> listQueries = new ArrayList<>();

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(pathToFile));
			String line;
			while ((line = br.readLine()) != null) {
				StringTokenizer tok = new StringTokenizer(line, "\t"); 

				List<String> splittedLine = new ArrayList<>();
				while ( tok.hasMoreElements() )  
				{  
					String next = (String) tok.nextElement();
					splittedLine.add(next);
				}

				if (splittedLine.size() != 5){
					continue; // invalid line
				}
				Pair3LetterCodeAndMW pair3LetterCodeAndMW = new Pair3LetterCodeAndMW(splittedLine.get(2).toCharArray(), Float.valueOf(splittedLine.get(3)), splittedLine.get(0).toCharArray(), splittedLine.get(1).toCharArray());
				listQueries.add(pair3LetterCodeAndMW);
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listQueries;
	}
}

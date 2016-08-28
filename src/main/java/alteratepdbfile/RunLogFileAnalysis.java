package alteratepdbfile;

import java.util.ArrayList;
import java.util.List;

public class RunLogFileAnalysis {

	public static void main(String[] args) {

		String path = "C://Users//fabrice//Documents//ultimate//results//log_Project - Kopie.txt";
		//String path = "C://Users//fabrice//Documents//ultimate//results_1NLNnoColorInScoring//log_Project.txt";
		
		List<String> pathsToLogFiles = new ArrayList<>();
		pathsToLogFiles.add(path);
		AnalyzeLogFiles analyzeLogFiles = new AnalyzeLogFiles(pathsToLogFiles);
		analyzeLogFiles.analyze();

	}

}

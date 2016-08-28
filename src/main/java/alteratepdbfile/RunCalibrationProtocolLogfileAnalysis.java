package alteratepdbfile;

import java.util.ArrayList;
import java.util.List;

public class RunCalibrationProtocolLogfileAnalysis {

	public static void main(String[] args) {

		String path = "C://Users//fabrice//Documents//ultimate//results_CalibrationProtocol//log_Project.txt";
		//String path = "C://Users//fabrice//Documents//ultimate//results_1NLNnoColorInScoring//log_Project.txt";
		
		List<String> pathsToLogFiles = new ArrayList<>();
		pathsToLogFiles.add(path);
		AnalyzeCalibrationProtocolLogfile analyzeLogFiles = new AnalyzeCalibrationProtocolLogfile(pathsToLogFiles);
		analyzeLogFiles.analyze();

	}
	
}

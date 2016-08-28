package alteratepdbfile;

import java.io.FileInputStream;
import java.io.IOException;

public class ReadTextFile {

	public static String readTextFile(String pathToFile){

		StringBuilder builder = new StringBuilder();

		try (FileInputStream input = new FileInputStream(pathToFile)){
			int ch;
			while((ch = input.read()) != -1){
				builder.append((char)ch);
			}
		}catch(IOException e){
			return null;
		}
		return builder.toString();
	}
}

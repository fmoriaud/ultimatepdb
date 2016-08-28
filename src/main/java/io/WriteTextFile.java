package io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class WriteTextFile {

	public static void writeTextFile(String content, String pathToFile){

		File file = new File(pathToFile);

		try (FileOutputStream fop = new FileOutputStream(pathToFile)) {

			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// get the content in bytes
			byte[] contentInBytes = content.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

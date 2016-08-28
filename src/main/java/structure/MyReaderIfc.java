package structure;

import java.nio.file.Path;

public interface MyReaderIfc {

	public MyStructureIfc read(Path path, char[] fourLetterCode) throws ReadingStructurefileException, ExceptionInMyStructurePackage;
}

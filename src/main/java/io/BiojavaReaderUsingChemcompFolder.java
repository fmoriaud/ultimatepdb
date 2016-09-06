package io;

import org.biojava.bio.structure.Structure;
import org.biojava.bio.structure.io.FileParsingParameters;
import org.biojava.bio.structure.io.MMCIFFileReader;
import org.biojava.bio.structure.io.mmcif.DownloadChemCompProvider;
import parameters.AlgoParameters;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Fabrice on 05/09/16.
 */
public class BiojavaReaderUsingChemcompFolder implements BiojavaReaderIfc {

    private AlgoParameters algoParameters;

    public BiojavaReaderUsingChemcompFolder(AlgoParameters algoParameters) {
        this.algoParameters = algoParameters;
    }


    @Override
    public Structure read(Path path) throws IOException {
        // It is static but used anyway
        DownloadChemCompProvider.setPath(algoParameters.getPATH_TO_CHEMCOMP_FOLDER());
        DownloadChemCompProvider c = new DownloadChemCompProvider();
        c.setDownloadAll(true);
        //check if directory empty
        try {
            if (isDirEmpty(Paths.get(algoParameters.getPATH_TO_CHEMCOMP_FOLDER()))) {
                c.checkDoFirstInstall();
            }
        } catch (IOException e) {
        }
        MMCIFFileReader mMCIFileReader = new MMCIFFileReader();
        mMCIFileReader.setPdbDirectorySplit(true);
        mMCIFileReader.setPath(algoParameters.getPATH_TO_REMEDIATED_PDB_MMCIF_FOLDER());
        FileParsingParameters params = new FileParsingParameters();
        params.setAlignSeqRes(false);
        params.setParseSecStruc(true);
        params.setLoadChemCompInfo(true);
        params.setCreateAtomBonds(true);

        mMCIFileReader.setFileParsingParameters(params);

        Structure structure = mMCIFileReader.getStructure(path.toString());

        return structure;
    }



    private static boolean isDirEmpty(final Path directory) throws IOException {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
            return !dirStream.iterator().hasNext();
        }
    }
}

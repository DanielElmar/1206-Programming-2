import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.io.ConcatenateJavaFiles;

import java.io.*;
import java.util.ArrayList;

public class JavaFileUtil implements ConcatenateJavaFiles {


    @Override
    public void concatenateJavaFiles(String dirName, String fileName) throws IOException {

        // Creates an array in which we will store the names of files and directories
        String[] subFiles;
        ArrayList<String> javaFiles = new ArrayList<>();

        // Creates a new File obj with the given directory argument
        File dir = new File(dirName);

        if (!dir.isDirectory()){
            throw new IllegalArgumentException("Given argument is not a Directory");
        }

        // adds names of files and directories to subFiles
        subFiles = dir.list();

        // For each subFile in the Given directory
        for (String subFile : subFiles) {
            if (subFile.endsWith(".java")){
                javaFiles.add(subFile);
            }
        }


        OutputStream out = new FileOutputStream(dirName + File.separator +fileName);
        byte[] buffer = new byte[1];

        // for each file ending in .java it will read it 1 byte at a time into buffer and then write buffer to the output file
        // this proccess is repeated untill the entire file has been read at which point the next file to be read is loaded in
        for (String file : javaFiles) {

            InputStream in = new FileInputStream(dirName + File.separator + file);
            int numOfBytesToWrite = 0;
            while ( (numOfBytesToWrite = in.read(buffer)) >= 0)
                out.write(buffer, 0, numOfBytesToWrite);
            in.close();

        }
        out.close();

    }
}

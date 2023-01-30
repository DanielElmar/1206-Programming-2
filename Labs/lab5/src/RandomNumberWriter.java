import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.io.RandomIO;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Random;

public class RandomNumberWriter implements RandomIO {

    Random random;

    // Constructor with seed parameter for Random int generation
    RandomNumberWriter(long seed){
        random = new Random(seed);
    }


    @Override
    public void writeRandomChars(String filePath) throws IOException {

        // Create File obj with given path argument
        File file = new File(filePath);

        // Create Char output Streamer
        Writer out = new FileWriter(file, false);

        // generate random numbers using nextInt() and add them to the file
        for (int i = 0; i < 10000; i++) {
            out.write(  Integer.toString( random.nextInt(100000) ) + "\n" );
        }

        // close output streamer
        out.close();

    }

    @Override
    public void writeRandomByte(String filePath) throws IOException {

        // Create File obj with given path argument
        File file = new File(filePath);

        // Create Byte output Streamer
        OutputStream out = new FileOutputStream(file, false);

        byte[] bytes;

        // generate random numbers using nextInt() and add them to the file
        for (int i = 0; i < 10000; i++) {

            // ensures bytes contains 4 bytes
            bytes = ByteBuffer.allocate(4).putInt( random.nextInt(100000) ).array();

            out.write(  bytes );
        }

        // close output streamer
        out.close();

    }
}

package console;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Class operates with files
 */
public class FileManager {

    public static String readFile(InputStream is) throws IOException {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        isr.close(); br.close();
        return sb.toString();
    }

    /**
     * Read file to Queue
     * @param path path to file
     * @return Queue of String (each row - element)
     * @throws IOException raise if file not found
     */
    public static Queue<String> readCommandFile(String path) throws IOException {
        InputStreamReader isr = new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);

        Queue<String> q = new ArrayDeque<>();

        String line;
        while ((line = br.readLine()) != null) q.add(line);
        isr.close(); br.close();
        return q;
    }

    public String readResourcesFile(String fileName) throws IOException {
        // Getting ClassLoader obj
        ClassLoader classLoader = this.getClass().getClassLoader();
        // Getting resource(File) from class loader
        InputStream is = classLoader.getResourceAsStream(fileName);

        return readFile(is);
    }
}

package console;

import collection.MovieCollection;
import collection.Movie;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.PriorityQueue;
import java.util.Queue;

public class FileManager {
    private final JsonParser jp;

    public FileManager() {
        this.jp = new JsonParser();
    }

    public void writeToJsonFile(String path, MovieCollection mc) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(path);
        pw.println(jp.toJson(mc.getPQ()));
        pw.close();
    }

    public PriorityQueue<Movie> readJsonFile(String path) throws IOException {
        String text = readFile(path);
        return jp.jsonToCollection(text);
    }

    public String readFile(String path) throws IOException {
        InputStreamReader isr = new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        return sb.toString();
    }

    public Queue<String> readCommandFile(String path) throws IOException {
        InputStreamReader isr = new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);

        Queue<String> q = new ArrayDeque<>();

        String line;
        while ((line = br.readLine()) != null) q.add(line);
        return q;
    }
}

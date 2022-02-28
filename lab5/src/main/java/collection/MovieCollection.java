package collection;

import console.Console;
import console.FileManager;
import exceptions.InvalidArgumentException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class MovieCollection {
    private PriorityQueue<Movie> pq;
    private final java.time.LocalDate initDate;
    private String startFilePath;

    public MovieCollection(FileManager fm) {
        this.pq = new PriorityQueue<>();
        this.initDate = LocalDate.now();
        this.startFilePath = null;
    }

    public MovieCollection(FileManager fm, String startFilePath) {
        this(fm);

        // initialize values from start file
        try {
            this.pq = fm.readJsonFile(startFilePath);
            int maxId = pq.stream()
                            .max(Comparator.comparing(Movie::getId))
                            .orElse(new Movie())
                            .getId();
            Movie.setCounter(maxId);
            this.startFilePath = startFilePath;
            Console.println("-> Collection with " + pq.size() + " movies was loaded from file '" + startFilePath + "'");
        } catch (IOException e) {
            Console.println("-> Program couldn't find json file to load the collection:(");
        }
    }

    public int size() {
        return pq.size();
    }

    public PriorityQueue<Movie> getPQ() {
        return pq;
    }

    public String getStartFilePath() {
        return startFilePath;
    }

    public Movie getMovieById(int id) throws InvalidArgumentException {
        return pq.stream()
                .filter(movie -> movie.getId() == id)
                .findFirst()
                .orElseThrow(() -> new InvalidArgumentException("Film with id " + id + " not found."));
    }

    public Movie getLowest() {
        return pq.stream()
                .min(Comparator.comparing(Movie::getOscarsCount))
                .orElse(null);
    }

    public void addMovie(Movie m){
        pq.add(m);
    }

    public boolean removeMovieById(int id) {
        int oldSize = pq.size();
        pq = pq.stream()
                .filter(movie -> movie.getId() != id)
                .collect(Collectors.toCollection(PriorityQueue<Movie>::new));
        return oldSize != pq.size();
    }

    public boolean removeGreater(int id) throws InvalidArgumentException {
        Movie m = getMovieById(id);
        int oldSize = pq.size();
        pq = pq.stream()
                .filter(movie -> movie.compareTo(m) <= 0)
                .collect(Collectors.toCollection(PriorityQueue<Movie>::new));
        return oldSize != pq.size();
    }

    public boolean removeLower(int id) throws InvalidArgumentException {
        Movie m = getMovieById(id);
        int oldSize = pq.size();
        pq = pq.stream()
                .filter(movie -> movie.compareTo(m) >= 0)
                .collect(Collectors.toCollection(PriorityQueue<Movie>::new));
        return oldSize != pq.size();
    }

    public void clear() {
        pq.clear();
    }

    @Override
    public String toString() {
        if (size() == 0) return "Collection is empty.";
        StringBuilder sb = new StringBuilder("Movies:\n");
        sb.append("ID NAME\n");
        pq.forEach(movie -> sb.append(movie).append('\n'));
        return sb.toString();
    }

    public String getInfo() {
        return "*Collection type: " + pq.getClass().getName() +
                "\n*Initialization date: " + initDate.toString() +
                "\n*Total elements: " + pq.size();
    }
}

package collection;

import commands.InputValidator;
import console.Console;
import console.FileManager;
import data.*;
import exceptions.ExecuteScriptFailedException;
import exceptions.InvalidArgumentException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Class operates collection of movies using PriorityQueue collection
 */
public class MovieCollection {
    private PriorityQueue<Movie> pq;
    private final LocalDate initDate;

    private String startFilePath;

    public MovieCollection(FileManager fm) {
        this(fm, null);
    }

    /**
     * Create MovieCollection and initialize it with values in json file
     * @param startFilePath path to json file with initial values
     */
    public MovieCollection(FileManager fm, String startFilePath) {
        this.pq = new PriorityQueue<>();
        this.initDate = LocalDate.now();
        this.startFilePath = null;
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

    /**
     * Find movie in collection by id and return it
     * @param id movie id
     * @return found movie
     * @throws InvalidArgumentException if argument not specified or has wrong format
     */
    public Movie getMovieById(int id) throws InvalidArgumentException {
        return pq.stream()
                .filter(movie -> movie.getId() == id)
                .findFirst()
                .orElseThrow(() -> new InvalidArgumentException("Film with id " + id + " not found."));
    }

    public int generateId() {
        int maxId = pq.stream()
                .max(Comparator.comparing(Movie::getId))
                .orElse(new Movie())
                .getId();
        return maxId + 1;
    }

    /**
     * Get the Lowest Movie (by OscarsCount) in collection
     * @return Movie if movie found OR null if movie not found
     */
    public Movie getLowestByOscars() {
        return pq.stream()
                .min(Comparator.comparing(Movie::getOscarsCount))
                .orElse(null);
    }

    /**
     * Add new Movie in collection
     * @param m Movie to add
     */
    public void addMovie(Movie m){
        pq.add(m);
    }

    /**
     * Remove movie from collection by id
     * @param id id of Movie that will be removed
     * @return true if Movie was in collection OR false if Movie wasn't in collection (so nothing will be removed)
     */
    public boolean removeMovieById(int id) {
        int oldSize = pq.size();
        pq = pq.stream()
                .filter(movie -> movie.getId() != id)
                .collect(Collectors.toCollection(PriorityQueue<Movie>::new));
        return oldSize != pq.size();
    }

    /**
     * Remove greater Movie in collection (see {@link Movie#compareTo(Movie)})
     * @param id id of Movie to base
     * @return true if Movie was in collection OR false if Movie wasn't in collection (so nothing will be removed)
     * @throws InvalidArgumentException if argument not specified or has wrong format
     */
    public boolean removeGreater(int id) throws InvalidArgumentException {
        Movie m = getMovieById(id);
        int oldSize = pq.size();
        pq = pq.stream()
                .filter(movie -> movie.compareTo(m) <= 0)
                .collect(Collectors.toCollection(PriorityQueue<Movie>::new));
        return oldSize != pq.size();
    }

    /**
     * Remove lower Movie in collection (see {@link Movie#compareTo(Movie)})
     * @param id id of Movie to base
     * @return true if Movie was in collection OR false if Movie wasn't in collection (so nothing will be removed)
     * @throws InvalidArgumentException if argument not specified or has wrong format
     */
    public boolean removeLower(int id) throws InvalidArgumentException {
        Movie m = getMovieById(id);
        int oldSize = pq.size();
        pq = pq.stream()
                .filter(movie -> movie.compareTo(m) >= 0)
                .collect(Collectors.toCollection(PriorityQueue<Movie>::new));
        return oldSize != pq.size();
    }

    /**
     * Clear all Movies in collection
     */
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

    /**
     * Get main info about collection
     * @return String of collection type, init date, total elements count
     */
    public String getInfo() {
        return "*Collection type: " + pq.getClass().getName() +
                "\n*Initialization date: " + initDate.toString() +
                "\n*Total elements: " + pq.size();
    }


}

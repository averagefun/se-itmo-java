package collection;

import com.google.gson.JsonSyntaxException;
import commands.InputValidator;
import console.Console;
import console.FileManager;
import data.*;
import exceptions.ExecuteScriptFailedException;
import exceptions.InitialFileInvalidValuesException;
import exceptions.InvalidArgumentException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
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

        // try to initialize values from start file
        loadJsonFile(fm, startFilePath);
    }

    private void loadJsonFile(FileManager fm, String filePath) {
        try {
            this.pq = fm.readJsonFile(filePath);

            // Check for unique ids
            HashSet<Integer> ids = new HashSet<>();
            for (Movie m: pq) {
                if (!ids.add(m.getId()))
                    throw new InitialFileInvalidValuesException(
                            "ERROR: -> Movie's ids in json file must have unique values.");
            }

            pq.forEach(movie -> {
                try {
                    inputAndUpdateMovie(true, movie, false, () -> "#validate_initial");
                } catch (ExecuteScriptFailedException e) {
                    pq = new PriorityQueue<>();
                    throw new InitialFileInvalidValuesException();
                }
            });

            this.startFilePath = (filePath != null) ? filePath : FileManager.DEFAULT_START_FILE;
            if (filePath == null) Console.println("-> Using default file '" + FileManager.DEFAULT_START_FILE + "' to save collection.");
            else Console.println("-> Collection with " + pq.size() + " movies was loaded from file '" + startFilePath + "'");
        } catch (JsonSyntaxException e) {
            Console.println("ERROR: Syntax error in json file. File not loaded.");
            System.exit(0);
        } catch (IOException e) {
            Console.println("ERROR: Program couldn't find json file to load the collection:(");
            System.exit(0);
        } catch (InitialFileInvalidValuesException e) {
            if (e.getMessage() != null) {
                Console.println(e.getMessage());
            } else {
                Console.println("ERROR: -> Json file not loaded.");
            }
            System.exit(0);
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

    public int getMaxId() {
        return pq.stream()
                .max(Comparator.comparing(Movie::getId))
                .orElse(new Movie())
                .getId();
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

    /**
     * Global process of input values to add, update, validate Movies
     */
    public static void inputAndUpdateMovie(boolean updMode, Movie movie, boolean printMode, Supplier<String> valueGetter) throws ExecuteScriptFailedException {
        String name = (String) new InputValidator(String.class, false)
                .loadPreviousValue(updMode, updMode ? movie.getName() : null)
                .interactiveInput("movie name", printMode, valueGetter);

        Console.println("Type coordinates:", printMode);
        Float x = (Float) new InputValidator(Float.class, false)
                .loadPreviousValue(updMode, updMode ? movie.getCoordinates().getX() : null)
                .interactiveInput("x (float)", printMode, valueGetter);

        long y = (long) new InputValidator(long.class, false, -863, Double.MAX_VALUE)
                .loadPreviousValue(updMode, updMode ? movie.getCoordinates().getY() : null)
                .interactiveInput("y (long > -863)", printMode, valueGetter);
        Coordinates coordinates = new Coordinates(x,y);

        int oscarsCount = (int) new InputValidator(int.class, false, 0, Integer.MAX_VALUE)
                .loadPreviousValue(updMode, updMode ? movie.getOscarsCount() : null)
                .interactiveInput("number of Oscars", printMode, valueGetter);

        MovieGenre movieGenre = (MovieGenre) new InputValidator(MovieGenre.class, false)
                .loadPreviousValue(updMode, updMode ? movie.getMovieGenre() : null)
                .interactiveInput("movieGenre", MovieGenre.values(), printMode, valueGetter);

        Object obj = new InputValidator(MpaaRating.class, true)
                .loadPreviousValue(updMode, updMode ? movie.getMpaaRating() : null)
                .interactiveInput("mpaaRating", MpaaRating.values(), printMode, valueGetter);
        MpaaRating mpaaRating = (obj != null) ? ((MpaaRating) obj) : null;

        Console.println("Type director data:", printMode);
        String directorName = (String) new InputValidator(String.class, false)
                .loadPreviousValue(updMode, updMode ? movie.getDirector().getName() : null)
                .interactiveInput("director name", printMode, valueGetter);

        double directorWeight = (double) new InputValidator(double.class, false, 0, Double.MAX_VALUE)
                .loadPreviousValue(updMode, updMode ? movie.getDirector().getWeight() : null)
                .interactiveInput("director weight (double)", printMode, valueGetter);

        Color hairColor = (Color) new InputValidator(Color.class, false)
                .loadPreviousValue(updMode, updMode ? movie.getDirector().getHairColor() : null)
                .interactiveInput("hair color", Color.values(), printMode, valueGetter);

        Double locationX = (Double) new InputValidator(Double.class, false)
                .loadPreviousValue(updMode, updMode ? movie.getDirector().getLocation().getX() : null)
                .interactiveInput("location X (double)", printMode, valueGetter);

        double locationY = (double) new InputValidator(double.class, false)
                .loadPreviousValue(updMode, updMode ? movie.getDirector().getLocation().getY() : null)
                .interactiveInput("location Y (double)", printMode, valueGetter);

        String locationName = (String) new InputValidator(String.class, true)
                .loadPreviousValue(updMode, updMode ? movie.getDirector().getLocation().getName() : null)
                .interactiveInput("location name", printMode, valueGetter);

        Location location = new Location(locationX, locationY, locationName);
        Person director = new Person(directorName, directorWeight, hairColor, location);

        if (updMode) {
            movie.updateMovie(name, coordinates, oscarsCount, movieGenre, mpaaRating, director);
        }
    }
}

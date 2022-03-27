package collection;

import data.Coordinates;
import data.MovieGenre;
import data.MpaaRating;
import data.Person;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import exceptions.InvalidRangeException;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Class represent movie (film) that storage in MovieCollection.
 */
public class Movie implements Comparable<Movie> {
    private static int counter = 0;
    private final int id;
    private String name;
    private Coordinates coordinates;
    private final LocalDate creationDate;
    private int oscarsCount;
    private MovieGenre movieGenre;
    private MpaaRating mpaaRating;
    private Person director;

    public Movie() {
        this.id = 0;
        this.creationDate = LocalDate.now();
    }

    public Movie(@NotNull String name, @NotNull Coordinates coordinates, int oscarsCount, @NotNull MovieGenre movieGenre,
                 @Nullable MpaaRating mpaaRating, @Nullable Person director) {
        this.id = ++counter;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = LocalDate.now();
        this.oscarsCount = oscarsCount;
        this.movieGenre = movieGenre;
        this.mpaaRating = mpaaRating;
        this.director = director;
    }

    public void updateMovie(@NotNull String name, @NotNull Coordinates coordinates, int oscarsCount, @NotNull MovieGenre movieGenre,
                 @Nullable MpaaRating mpaaRating, @NotNull Person director) {
        this.name = name;
        this.coordinates = coordinates;
        this.oscarsCount = oscarsCount;
        this.movieGenre = movieGenre;
        this.mpaaRating = mpaaRating;
        this.director = director;
    }

    /**
     * Set counter of all Movie instances
     * @param c must be larger than previous because of unique
     */
    public static void setCounter(int c){
        if (c < counter) throw new InvalidRangeException("New counter must be bigger than previous!");
        counter = c;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public int getOscarsCount() {
        return oscarsCount;
    }

    public MovieGenre getMovieGenre() {
        return movieGenre;
    }

    public MpaaRating getMpaaRating() {
        return mpaaRating;
    }

    public Person getDirector() {
        return director;
    }

    /**
     * Compare with another movie by id
     * @param m movie to compare
     * @return -1(lower m), 0(equal m), 1(greater m)
     */
    @Override
    public int compareTo(Movie m) {
        return Integer.compare(getId(), m.getId());
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%2d %s", id, name);
    }
}

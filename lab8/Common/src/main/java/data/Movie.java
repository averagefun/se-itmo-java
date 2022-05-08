package data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Locale;

/**
 * Class represent movie (film) that storage in MovieCollection.
 */
public class Movie implements Comparable<Movie>, Serializable {
    private int id;
    private String username;
    private String name;
    private Coordinates coordinates;
    private final LocalDate creationDate;
    private int oscarsCount;
    private MovieGenre movieGenre;
    private MpaaRating mpaaRating;
    private Person director;

    public Movie(@NotNull String username, @NotNull String name, @NotNull Coordinates coordinates, int oscarsCount, @NotNull MovieGenre movieGenre,
                 @Nullable MpaaRating mpaaRating, @Nullable Person director) {
        this.username = username;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = LocalDate.now();
        this.oscarsCount = oscarsCount;
        this.movieGenre = movieGenre;
        this.mpaaRating = mpaaRating;
        this.director = director;
    }

    public Movie(int id, @NotNull String username, @NotNull String name, @NotNull Coordinates coordinates, int oscarsCount, @NotNull MovieGenre movieGenre,
                 @Nullable MpaaRating mpaaRating, @Nullable Person director) {
        this(username, name, coordinates, oscarsCount, movieGenre, mpaaRating, director);
        this.id = id;
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

    public void updateMovie(Movie m) {
        updateMovie(m.name, m.coordinates, m.oscarsCount, m.movieGenre, m.mpaaRating, m.director);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
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

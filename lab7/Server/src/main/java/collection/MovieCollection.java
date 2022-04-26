package collection;

import data.*;
import database.Database;
import exceptions.InvalidArgumentException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import exceptions.MyExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class operates collection of movies using PriorityQueue collection
 */
public class MovieCollection {
    private PriorityQueue<Movie> pq;
    private final LocalDate initDate;
    private final Database db;
    private final Logger log = LoggerFactory.getLogger(MovieCollection.class);

    /**
     * Create MovieCollection and initialize it with values from database
     */
    public MovieCollection(Database db) {
        this.pq = new PriorityQueue<>(Comparator.comparing(movie -> movie.getCoordinates().getX()));
        this.initDate = LocalDate.now();
        this.db = db;

        // try to initialize values from database
        try {
            int n = initMoviesFromDB();
            if (n > 0)
                log.info("load {} movies from database", n);
            else
                log.info("database collection is empty, so no movies loaded");
        } catch (SQLException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            log.error("failed to load collection from database:\n{}", sw);
        }
    }

    private int initMoviesFromDB() throws SQLException {
        ResultSet m = db.executeQuery(
                "SELECT movies.id AS id, movies.name AS name, coordinates.x AS c_x, coordinates.y AS c_y,\n" +
                        "       movies.creation_date AS creation_date, movies.oscars_count AS oscars_count,\n" +
                        "       movies.movie_genre AS movie_genre, movies.mpaa_rating AS mpaa_rating,\n" +
                        "       persons.name AS p_name, persons.weight AS p_weight, persons.hair_color AS p_color,\n" +
                        "       locations.name AS l_name, locations.x AS l_x, locations.y AS l_y FROM movies\n" +
                        "JOIN coordinates on movies.coordinates = coordinates.id\n" +
                        "JOIN persons on movies.director = persons.id\n" +
                        "JOIN locations on persons.location = locations.id");
        int i;
        for (i = 0; m.next(); i++) {
            Movie movie = new Movie(
                    m.getInt("id"),
                    m.getString("name"),
                    new Coordinates(m.getFloat("c_x"), m.getLong("c_y")),
                    m.getInt("oscars_count"),
                    MovieGenre.valueOf(m.getString("movie_genre")),
                    m.getString("mpaa_rating") != null ? MpaaRating.valueOf(m.getString("mpaa_rating")) : null,
                    new Person(m.getString("p_name"), m.getDouble("p_weight"), Color.valueOf(m.getString("p_color")),
                            new Location(m.getDouble("l_x"), m.getDouble("l_y"), m.getString("l_name")))
                    );
            pq.add(movie);
        }
        return i;
    }

    public int size() {
        return pq.size();
    }

    public PriorityQueue<Movie> getPQ() {
        return pq;
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
     * @return true in case of successful adding else false
     */
    public boolean addMovie(Movie m, int userId) throws SQLException {
        ResultSet rs = db.executeQuery("INSERT INTO coordinates (x, y) VALUES (?, ?) RETURNING id",
                m.getCoordinates().getX(), m.getCoordinates().getY());
        rs.next();
        int coordinatesId = rs.getInt("id");
        db.closeQuery();

        rs = db.executeQuery("INSERT INTO locations (x, y, name) VALUES (?, ?, ?) RETURNING id",
                m.getDirector().getLocation().getX(), m.getDirector().getLocation().getY(),
                m.getDirector().getLocation().getName());
        rs.next();
        int locationsId = rs.getInt("id");
        db.closeQuery();

        rs = db.executeQuery("INSERT INTO persons (name, weight, hair_color, location) VALUES (?, ?, ?, ?) RETURNING id",
                m.getDirector().getName(), m.getDirector().getWeight(), m.getDirector().getHairColor(), locationsId);
        rs.next();
        int personsId = rs.getInt("id");
        db.closeQuery();

        rs = db.executeQuery("INSERT INTO movies (name, user_id, coordinates, creation_date, oscars_count, movie_genre, mpaa_rating, director)" +
                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id",
                m.getName(), userId, coordinatesId, m.getCreationDate(), m.getOscarsCount(), m.getMovieGenre(), m.getMpaaRating(), personsId);
        rs.next();
        m.setId(rs.getInt("id"));
        return pq.add(m);
    }

    public boolean updateMovie(Movie m, int userId) throws InvalidArgumentException, SQLException {
        int movieId = m.getId();
        ResultSet rs = db.executeQuery(
                "SELECT movies.coordinates AS coordinates, movies.user_id AS user_id, movies.director AS director, persons.location AS location FROM movies\n" +
                "JOIN persons on movies.director = persons.id WHERE movies.id = ?", movieId);
        rs.next();
        int dbUserId = rs.getInt("user_id");
        if (userId != dbUserId) return false;
        int coordinatesId = rs.getInt("coordinates");
        int directorId = rs.getInt("director");
        int locationId = rs.getInt("location");
        db.closeQuery();

        db.executeUpdate("UPDATE coordinates SET x = ?, y = ? WHERE id = ?",
                m.getCoordinates().getX(), m.getCoordinates().getY(), coordinatesId);

        db.executeUpdate("UPDATE locations SET x = ?, y = ?, name = ? WHERE id = ?",
                m.getDirector().getLocation().getX(), m.getDirector().getLocation().getY(),
                m.getDirector().getLocation().getName(), locationId);

        db.executeUpdate("UPDATE persons SET name = ?, weight = ?, hair_color = ? WHERE id = ?",
                m.getDirector().getName(), m.getDirector().getWeight(), m.getDirector().getHairColor(), directorId);

        db.executeUpdate("UPDATE movies SET name = ?, oscars_count = ?, movie_genre = ?, mpaa_rating = ? WHERE id = ?",
                m.getName(), m.getOscarsCount(), m.getMovieGenre(), m.getMpaaRating(), movieId);
        rs.next();
        m.setId(rs.getInt("id"));

        getMovieById(movieId).updateMovie(m);
        return true;
    }

    /**
     * Remove movie from collection by id
     * @param id id of Movie that will be removed
     * @return true if Movie was in collection OR false if Movie wasn't in collection (so nothing will be removed)
     */
    public boolean removeMovieById(int id, int userId) throws SQLException {
        int n = db.executeUpdate("DELETE FROM movies WHERE id = ? AND user_id = ?", id, userId);
        if (n > 0) {
            pq = pq.stream()
                    .filter(movie -> movie.getId() != id)
                    .collect(Collectors.toCollection(PriorityQueue<Movie>::new));
            return true;
        }
        return false;
    }

    /**
     * Remove greater Movie in collection (see {@link Movie#compareTo(Movie)})
     * @param id id of Movie to base
     * @return true if Movie was in collection OR false if Movie wasn't in collection (so nothing will be removed)
     * @throws InvalidArgumentException if argument not specified or has wrong format
     */
    public boolean removeGreater(int id, int userId) throws InvalidArgumentException, SQLException {
        Movie m = getMovieById(id);
        int n = db.executeUpdate("DELETE FROM movies WHERE user_id = ? AND id > ?", userId, m.getId());
        initMoviesFromDB();
        return n > 0;
    }

    /**
     * Remove lower Movie in collection (see {@link Movie#compareTo(Movie)})
     * @param id id of Movie to base
     * @return true if Movie was in collection OR false if Movie wasn't in collection (so nothing will be removed)
     * @throws InvalidArgumentException if argument not specified or has wrong format
     */
    public boolean removeLower(int id, int userId) throws SQLException, InvalidArgumentException {
        Movie m = getMovieById(id);
        int n = db.executeUpdate("DELETE FROM movies WHERE user_id = ? AND id < ?", userId, m.getId());
        initMoviesFromDB();
        return n > 0;
    }

    /**
     * Clear all Movies in collection
     */
    public boolean clear(int userId) {
        try {
            db.executeUpdate("DELETE FROM movies WHERE user_id = ?", userId);
            initMoviesFromDB();
            return true;
        } catch (SQLException e) {
            log.error("SQLException occurred while cleaning collection:\n{}", MyExceptions.getStringStackTrace(e));
            return false;
        }
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

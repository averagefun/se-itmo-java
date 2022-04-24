package commands;

import collection.MovieCollection;
import data.Movie;
import data.Person;
import database.Database;
import database.SHA224;
import exceptions.*;
import network.CommandPacket;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that operates Command
 */
public class CommandManager {
    private final Map<String, Command> commands;
    private final MovieCollection mc;
    private final Database db;
    private final Map<String, Boolean> onlyAuthorized;
    private final Logger log = LoggerFactory.getLogger(MovieCollection.class);

    public CommandManager(MovieCollection mc, Database db) {
        this.commands = new HashMap<>();
        this.onlyAuthorized = new HashMap<>();
        this.mc = mc;
        this.db = db;
        initCommands();
    }

    /**
     * Add new command
     * @param name name of command that used to execute command
     * @param action what this command going to do (action of command)
     */
    public void putCommand(String name, boolean onlyAuthorized, Command action){
        commands.put(name, action);
        this.onlyAuthorized.put(name, onlyAuthorized);
    }

    /**
     * Get command by name
     * @param name the name of command that defined in {@link #putCommand(String, boolean, Command)}
     * @return Command
     */
    public Command getCommand(String name) {
        return commands.get(name);
    }

    /**
     * Run command by name defined in {@link #putCommand(String, boolean, Command)}
     * @param cp Packet that contain command name and command argument
     */
    public Object runCommand(CommandPacket cp) {
        try {
            Command command = getCommand(cp.getName());
            int userId = 0;
            if (command == null) {
                log.debug("processing command '{}': command not found.", cp.getName());
                throw new CommandNotFindException("Command not found.");
            } else if (onlyAuthorized.get(cp.getName())) {
                if (cp.getUsername().isEmpty())
                    throw new AuthorizationException("Authorization error: to run this command you need to be authorized.");
                ResultSet rs = db.executeQuery("SELECT id, salt FROM users WHERE username = ?", cp.getUsername());
                rs.next();
                userId = rs.getInt("id");
                String userSalt = rs.getString("salt");
                db.closeQuery();

                SHA224 sha = new SHA224(db.getDbSalt(), userSalt);
                rs = db.executeQuery(
                        "SELECT exists(SELECT 1 FROM users WHERE username = ? AND password = ?)",
                        cp.getUsername(), sha.getHashString(cp.getPassword()));
                rs.next();
                if (!rs.getBoolean(1)) {
                    throw new AuthorizationException("Authorization error: wrong login or password.");
                }
            }
            return command.run( userId, cp.getCount(), cp.getArg());
        } catch (NullPointerException e) {
            log.debug("processing command '{}': command did not run successfully, problem detected.", cp.getName());
            return "Command did not run successfully, problem detected.";
        }
        catch (InvalidArgumentException | AuthorizationException |
                ExecuteScriptFailedException|CommandNotFindException e) {
            if (e.getMessage() != null) return e.getMessage();
        } catch (IOException e) {
            log.debug("processing command '{}': file not found.", cp.getName());
            return "Error: file not found";
        } catch (SQLException e) {
            log.error(MyExceptions.getStringStackTrace(e));
            return "Some problems detected with databases. Please try command later.";
        }
        return null;
    }

    private void initCommands(){
        putCommand("sign_in", false, (userId, count, argObject) -> {
            if (count == 0) {
                ResultSet rs = db.executeQuery("SELECT exists(SELECT 1 FROM users WHERE username = ?)", argObject);
                rs.next();
                if (rs.getBoolean(1)) {
                    db.closeQuery();
                    return "success";
                } else {
                    db.closeQuery();
                    return "Current username does not exists.";
                }
            } else {
                String[] data = ((String) argObject).split("::");
                String username = data[0], password = "";
                if (data.length == 2) password = data[1];
                    ResultSet rs = db.executeQuery("SELECT salt FROM users WHERE username = ?", username);
                    rs.next();
                    String userSalt = rs.getString(1);
                    db.closeQuery();

                    SHA224 sha = new SHA224(db.getDbSalt(), userSalt);
                    rs = db.executeQuery(
                            "SELECT exists(SELECT 1 FROM users WHERE username = ? AND password = ?)",
                            username, sha.getHashString(password));
                    rs.next();
                    if (rs.getBoolean(1)) {
                        db.closeQuery();
                        return "success";
                    } else {
                        db.closeQuery();
                        return "Wrong password: permission denied.";
                    }
            }
        });

        putCommand("sign_up", false, (userId, count, argObject) -> {
            if (count == 0) {
                ResultSet rs = db.executeQuery("SELECT exists(SELECT 1 FROM users WHERE username = ?)", argObject);
                rs.next();
                if (!rs.getBoolean(1)) {
                    db.closeQuery();
                    return "success";
                } else {
                    db.closeQuery();
                    return "Current username already exists.";
                }
            } else {
                String[] data = ((String) argObject).split("::");
                String username = data[0], password = "";
                if (data.length == 2) password = data[1];
                SHA224 sha = new SHA224(db.getDbSalt());
                String userSalt = sha.getUserSalt();
                int n = db.executeUpdate(
                        "INSERT INTO users (username, password, salt) VALUES (?, ?, ?)",
                        username, sha.getHashString(password), userSalt);
                if (n > 0) return "success";
                else return "Oops, some problems with registration. Please, try it later.";
            }
        });

        putCommand("info", false, (userId, count, argObject) -> mc.getInfo());

        putCommand("show", false, (userId, count, argObject) -> {
            if (argObject == null) return mc.toString();
            else {
                int id = (int) argObject;
                Movie m = mc.getMovieById(id);

                // Format creating date
                final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MMMM.yyyy", Locale.US);

                return String.format("Id: %d\nName: %s\nCoordinates:\n\tx: %f\n\ty: %d\nCreation Date: %s\nOscarsCount: %d\nMovieGenre: %s\nMpaaRating: %s\n" +
                                "Director:\n\tName: %s\n\tweight: %f\n\tHairColor: %s\n\tLocation:\n\t\tx: %f\n\t\ty: %f\n\t\tName: %s\n",
                        m.getId(), m.getName(), m.getCoordinates().getX(), m.getCoordinates().getY(),
                        m.getCreationDate().format(dtf), m.getOscarsCount(), m.getMovieGenre(), m.getMpaaRating(), m.getDirector().getName(),
                        m.getDirector().getWeight(), m.getDirector().getHairColor(), m.getDirector().getLocation().getX(),
                        m.getDirector().getLocation().getY(), m.getDirector().getLocation().getName());
            }
        });

        putCommand("add", true, (userId, count, argObject) -> {
            Movie movie = (Movie) argObject;
            return mc.addMovie(movie, userId) ? "Successfully added element!" : "Error occurred while adding element: element did not add.";
        });

        putCommand("update", true, (userId, count, argObject) -> {
            if (count == 0) {
                int id = (int) argObject;
                return mc.getMovieById(id);
            } else {
                return mc.updateMovie((Movie) argObject, userId) ? "Successfully updated element!" :  "Error with update movie: permission denied.";
            }
        });

        putCommand("remove_by_id", true, (userId, count, argObject) -> {
            int id = (int) argObject;
            return mc.removeMovieById(id, userId) ? "Movie successfully deleted!" : "Movie with current id doesn't exists in your collection.";
        });

        putCommand("clear", true, (userId, count, argObject) -> mc.clear(userId) ? "Collection cleared successfully!" : "Error occurred while cleaning collection: collection did not clean.");

        putCommand("add_if_min", true, (userId, count, argObject) -> {
            Movie movie = (Movie) argObject;
            if (movie.compareTo(mc.getLowestByOscars()) < 0) {
                return mc.addMovie(movie, userId) ? "Successfully added element!" : "Error occurred while adding element: element did not add.";
            } else {
                return "Element wasn't added to collection because it's not the lowest one.";
            }
        });

        putCommand("remove_greater", true, (userId, count, argObject) -> {
            int id = (int) argObject;
            return mc.removeGreater(id, userId) ? "Greater movies successfully deleted!" : "There are no movies greater than this.";
        });

        putCommand("remove_lower", true, (userId, count, argObject) -> {
            int id = (int) argObject;
            return (mc.removeLower(id, userId)) ? "Greater movies successfully deleted!" : "There are no movies lower than this.";
        });

        putCommand("filter_less_than_oscars_count", false, (userId, count, argObject) -> {
            int oscarsCount = (int) argObject;
            List<Movie> subCollection = mc.getPQ().stream()
                    .filter(movie -> movie.getOscarsCount() < oscarsCount)
                    .collect(Collectors.toList());

            if (subCollection.isEmpty()) {
                return "Films with less oscars was not found.";
            } else {
                StringBuilder sb = new StringBuilder("ID OSCARS NAME");
                subCollection.forEach(movie -> sb.append(String.format(Locale.US, "%2d %6d %s\n", movie.getId(), movie.getOscarsCount(), movie.getName())));
                return sb.toString();
            }
        });

        putCommand("filter_greater_than_director", false, (userId, count, argObject) -> {
            int id = (int) argObject;
            Person d = mc.getMovieById(id).getDirector();
            List<Movie> subCollection = mc.getPQ().stream()
                    .filter(movie -> movie.getDirector().compareTo(d) > 0)
                    .collect(Collectors.toList());

            if (subCollection.isEmpty()) {
                return "Films with greater directors was not found.";
            } else {
                StringBuilder sb = new StringBuilder("ID DIRECTOR NAME");
                subCollection.forEach(movie -> sb.append(String.format(Locale.US, "%2d %8.2f %s\n", movie.getId(), movie.getDirector().getWeight(), movie.getName())));
                return sb.toString();
            }
        });

        putCommand("print_unique_oscars_count", false, (userId, count, argObject) -> {
            Set<Integer> uniqOscar = new HashSet<>();
            mc.getPQ().forEach(movie -> uniqOscar.add(movie.getOscarsCount()));

            if (uniqOscar.isEmpty()) {
                return "Collection is empty, so no unique oscars.";
            } else {
                StringBuilder sb = new StringBuilder("All unique oscars count values (total " + uniqOscar.size() + "): ");
                uniqOscar.forEach(item -> sb.append(item).append(" "));
                return sb.toString();
            }
        });
    }
}

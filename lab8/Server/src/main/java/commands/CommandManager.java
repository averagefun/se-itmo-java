package commands;

import collection.MovieCollection;
import data.Movie;
import data.Person;
import database.Database;
import console.SHA224;
import exceptions.*;
import network.CommandRequest;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import network.CommandResponse;
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

    public CommandManager(MovieCollection mc) {
        this.commands = new HashMap<>();
        this.onlyAuthorized = new HashMap<>();
        this.mc = mc;
        this.db = Database.getInstance();
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

    public Database getDb() {
        return db;
    }

    public CommandResponse runCommand(CommandRequest cReq) {
        CommandResponse cRes = new CommandResponse();
        try {
            Command command = getCommand(cReq.getName());
            if (command == null) {
                log.debug("processing command '{}': command not found.", cReq.getName());
                throw new CommandNotFindException();
            } else if (onlyAuthorized.get(cReq.getName())) {
                if (cReq.getUsername() == null || cReq.getUsername().isEmpty())
                    throw new AuthorizationException("Authorization error: to run this command you need to be authorized.");
                ResultSet rs = db.executeQuery("SELECT salt FROM users WHERE username = ?", cReq.getUsername());
                rs.next();
                String userSalt = rs.getString("salt");
                rs.close();

                SHA224 sha = new SHA224(db.getDbSalt(), userSalt);
                rs = db.executeQuery(
                        "SELECT exists(SELECT 1 FROM users WHERE username = ? AND password = ?)",
                        cReq.getUsername(), sha.getHashString(cReq.getPassword()));
                rs.next();
                if (!rs.getBoolean(1)) {
                    throw new AuthorizationException("Authorization error: wrong login or password.");
                }
            }
            String username = cReq.getUsername() == null ? "" : cReq.getUsername();
            return command.run(username, cReq.getCount(), cReq.getArg());
        } catch (CommandNotFindException e) {
            cRes.setExitCode(5);
            cRes.setMessage("Command not found.");
        } catch (AuthorizationException e) {
            cRes.setExitCode(9);
            if (e.getMessage() != null) cRes.setMessage(e.getMessage());
        } catch (InvalidArgumentException | ExecuteScriptFailedException e) {
            cRes.setExitCode(10);
            if (e.getMessage() != null) cRes.setMessage(e.getMessage());
        } catch (NullPointerException | IOException | SQLException e) {
            log.error(MyExceptions.getStringStackTrace(e));
            cRes.setExitCode(1);
            cRes.setMessage("Command did not run successfully, problem detected.");
        }
        return cRes;
    }

    private void initCommands(){
        putCommand("/sign_in", false, (username, count, argObject) -> {
            if (count == 0) {
                ResultSet rs = db.executeQuery("SELECT exists(SELECT 1 FROM users WHERE username = ?)", argObject);
                rs.next();
                if (rs.getBoolean(1)) {
                    rs.close();
                    return new CommandResponse();
                } else {
                    rs.close();
                    return new CommandResponse(10, "usernameDoesntExists");
                }
            } else {
                String[] data = ((String) argObject).split("::");
                String currUsername = data[0], password = "";
                if (data.length == 2) password = data[1];
                    ResultSet rs = db.executeQuery("SELECT salt FROM users WHERE username = ?", currUsername);
                    rs.next();
                    String userSalt = rs.getString(1);
                    rs.close();

                    SHA224 sha = new SHA224(db.getDbSalt(), userSalt);
                    rs = db.executeQuery(
                            "SELECT exists(SELECT 1 FROM users WHERE username = ? AND password = ?)",
                            currUsername, sha.getHashString(password));
                    rs.next();
                    if (rs.getBoolean(1)) {
                        rs.close();
                        return new CommandResponse();
                    } else {
                        rs.close();
                        return new CommandResponse(10, "wrongPassword");
                    }
            }
        });

        putCommand("/sign_up", false, (username, count, argObject) -> {
            if (count == 0) {
                ResultSet rs = db.executeQuery("SELECT exists(SELECT 1 FROM users WHERE username = ?)", argObject);
                rs.next();
                if (!rs.getBoolean(1)) {
                    rs.close();
                    return new CommandResponse();
                } else {
                    rs.close();
                    return new CommandResponse(10, "usernameAlreadyExists");
                }
            } else {
                String[] data = ((String) argObject).split("::");
                String currUsername = data[0], password = "";
                if (data.length == 2) password = data[1];
                SHA224 sha = new SHA224(db.getDbSalt(), true);
                String userSalt = sha.getUserSalt();
                int n = db.executeUpdate(
                        "INSERT INTO users (username, password, salt) VALUES (?, ?, ?)",
                        currUsername, sha.getHashString(password), userSalt);
                if (n > 0) return new CommandResponse();
                else return new CommandResponse(1, "Oops, some problems with registration. Please, try it later.");
            }
        });

        putCommand("info", false, (username, count, argObject) ->
                new CommandResponse(mc.getInfo()));

        putCommand("$get", false, ((username, count, arg) ->
                new CommandResponse(mc.getPqCopy())));

        putCommand("show", false, (username, count, argObject) -> {
            if (argObject == null) {
                StringBuilder sb = new StringBuilder("All movies in collection:\n");
                sb.append("| ID |      MOVIE NAME |   AUTHOR NAME |\n");
                mc.getQueueStream().forEach(m -> {
                    String row = String.format(Locale.US, "| %2d | %15s | %13s |",
                            m.getId(),
                            m.getName().length() <= 15 ? m.getName() : m.getName().substring(0, 15),
                            m.getUsername().length() <= 13 ? m.getUsername() : m.getUsername().substring(0, 13));
                    if (username.equals(m.getUsername())) row += " <-";
                    sb.append(row).append("\n");
                });
                return new CommandResponse(mc.size() == 0 ? "Collection is empty." : sb.toString());
            } else {
                int id = (int) argObject;
                Movie m = mc.getMovieById(id);

                // Format creating date
                final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MMMM.yyyy", Locale.US);

                return new CommandResponse(String.format("Author: %s\nId: %d\nName: %s\nCoordinates:\n\tx: %f\n\ty: %d\nCreation Date: %s\nOscarsCount: %d\nMovieGenre: %s\nMpaaRating: %s\n" +
                                "Director:\n\tName: %s\n\tweight: %f\n\tHairColor: %s\n\tLocation:\n\t\tx: %f\n\t\ty: %f\n\t\tName: %s\n",
                        m.getUsername(), m.getId(), m.getName(), m.getCoordinates().getX(), m.getCoordinates().getY(),
                        m.getCreationDate().format(dtf), m.getOscarsCount(), m.getMovieGenre(), m.getMpaaRating(), m.getDirector().getName(),
                        m.getDirector().getWeight(), m.getDirector().getHairColor(), m.getDirector().getLocation().getX(),
                        m.getDirector().getLocation().getY(), m.getDirector().getLocation().getName()));
            }
        });

        putCommand("add", true, (username, count, argObject) -> {
            Movie movie = (Movie) argObject;
            return mc.addMovie(movie, username) ?
                    new CommandResponse("successfullyAdd") :
                    new CommandResponse(10, "errorAdd");
        });

        putCommand("update", true, (username, count, argObject) -> {
            if (count == 0) {
                int id = (int) argObject;
                Movie m = mc.getMovieById(id);
                ResultSet rs = db.executeQuery("SELECT username FROM movies WHERE id = ?", m.getId());
                rs.next();
                String dbUsername = rs.getString(1);
                rs.close();
                if (!username.equals(dbUsername)) {
                    return new CommandResponse(10,"Permission denied: you have no rights to edit this film.");
                } else {
                    return new CommandResponse(m);
                }
            } else {
                return mc.updateMovie((Movie) argObject, username) ?
                        new CommandResponse("successfullyUpd") :
                        new CommandResponse(10, "errorUpd");
            }
        });

        putCommand("remove_by_id", true, (username, count, argObject) -> {
            int id = (int) argObject;
            return mc.removeMovieById(id, username) ?
                    new CommandResponse("successfullyRem") :
                    new CommandResponse("errorRem");
        });

        putCommand("clear", true, (username, count, argObject) -> mc.clear(username) ?
                new CommandResponse("successfullyClean") :
                new CommandResponse(10, "errorClean"));

        putCommand("add_if_min", true, (username, count, argObject) -> {
            Movie movie = (Movie) argObject;
            if (movie.compareTo(mc.getLowestByOscars()) < 0) {
                return mc.addMovie(movie, username) ?
                        new CommandResponse("Successfully added element!") :
                        new CommandResponse("Error occurred while adding element: element did not add.");
            } else {
                return new CommandResponse("Element wasn't added to collection because it's not the lowest one.");
            }
        });

        putCommand("remove_greater", true, (username, count, argObject) -> {
            int id = (int) argObject;
            return mc.removeGreater(id, username) ?
                    new CommandResponse("Greater movies successfully deleted!") :
                    new CommandResponse("There are no movies greater than this.");
        });

        putCommand("remove_lower", true, (username, count, argObject) -> {
            int id = (int) argObject;
            return mc.removeLower(id, username) ?
                    new CommandResponse("Greater movies successfully deleted!") :
                    new CommandResponse("There are no movies lower than this.");
        });

        putCommand("filter_less_than_oscars_count", false, (username, count, argObject) -> {
            int oscarsCount = (int) argObject;
            List<Movie> subCollection = mc.getQueueStream()
                    .filter(movie -> movie.getOscarsCount() < oscarsCount)
                    .collect(Collectors.toList());

            if (subCollection.isEmpty()) {
                return new CommandResponse("Films with less oscars was not found.");
            } else {
                StringBuilder sb = new StringBuilder("| ID | OSCARS |      MOVIE NAME |\n");
                subCollection.forEach(movie -> sb.append(
                        String.format(Locale.US, "| %2d | %6d | %15s |\n", movie.getId(), movie.getOscarsCount(),
                               movie.getName().length() <= 15 ? movie.getName() : movie.getName().substring(0, 15))));
                return new CommandResponse(sb.toString());
            }
        });

        putCommand("filter_greater_than_director", false, (username, count, argObject) -> {
            int id = (int) argObject;
            Person d = mc.getMovieById(id).getDirector();
            List<Movie> subCollection = mc.getQueueStream()
                    .filter(movie -> movie.getDirector().compareTo(d) > 0)
                    .collect(Collectors.toList());

            if (subCollection.isEmpty()) {
                return new CommandResponse("Films with greater directors was not found.");
            } else {
                StringBuilder sb = new StringBuilder("| ID | DIRECTOR |      MOVIE NAME |\n");
                subCollection.forEach(
                        movie -> sb.append(String.format(Locale.US, "| %2d | %8.2f | %15s |\n",
                                movie.getId(), movie.getDirector().getWeight(),
                                movie.getName().length() <= 15 ? movie.getName() : movie.getName().substring(0, 15))));
                return new CommandResponse(sb.toString());
            }
        });

        putCommand("print_unique_oscars_count", false, (username, count, argObject) -> {
            Set<Integer> uniqOscar = new HashSet<>();
            mc.getQueueStream().forEach(movie -> uniqOscar.add(movie.getOscarsCount()));

            if (uniqOscar.isEmpty()) {
                return new CommandResponse("Collection is empty, so no unique oscars.");
            } else {
                StringBuilder sb = new StringBuilder("All unique oscars count values (total " + uniqOscar.size() + "): ");
                uniqOscar.forEach(item -> sb.append(item).append(" "));
                return new CommandResponse(sb.toString());
            }
        });
    }
}

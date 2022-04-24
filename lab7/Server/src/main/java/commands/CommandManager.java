package commands;

import collection.MovieCollection;
import data.Movie;
import data.Person;
import exceptions.CommandNotFindException;
import exceptions.ExecuteScriptFailedException;
import exceptions.InvalidArgumentException;
import network.CommandPacket;

import java.io.IOException;
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
    private final Logger log = LoggerFactory.getLogger(MovieCollection.class);
    private String serverCode = "000000";

    public CommandManager(MovieCollection mc) {
        this.commands = new HashMap<>();
        this.mc = mc;

        initCommands();
    }

    /**
     * Add new command
     * @param name name of command that used to execute command
     * @param action what this command going to do (action of command)
     */
    public void putCommand(String name, Command action){
        commands.put(name, action);
    }

    /**
     * Get command by name
     * @param name the name of command that defined in {@link #putCommand(String, Command)}
     * @return Command
     */
    public Command getCommand(String name) {
        return commands.get(name);
    }

    /**
     * Run command by name defined in {@link #putCommand(String, Command)}
     * @param cp Packet that contain command name and command argument
     */
    public Object runCommand(CommandPacket cp) {
        try {
            Command command = getCommand(cp.getName());
            if (command == null) {
                log.debug("processing command '{}': command not found.", cp.getName());
                throw new CommandNotFindException("Command not found.");
            }
            return command.run(cp.getArg());
        } catch (NullPointerException e) {
            log.debug("processing command '{}': command did not run successfully, problem detected.", cp.getName());
            return "Command did not run successfully, problem detected.";
        }
        catch (InvalidArgumentException e) {
            if (e.getMessage() != null) return e.getMessage();
        } catch (ExecuteScriptFailedException|CommandNotFindException e) {
            return e.getMessage();
        } catch (IOException e) {
            log.debug("processing command '{}': file not found.", cp.getName());
            return "Error: file not found";
        }
        return null;
    }

    private void initCommands(){
        putCommand("info", (argObject) -> mc.getInfo());

        putCommand("show", (argObject) -> {
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

        putCommand("add", (argObject) -> {
            Movie movie = (Movie) argObject;
            return mc.addMovie(movie) ? "Successfully added element!" : "Error occurred while adding element: element did not add.";
        });

        putCommand("update", (argObject) -> {
            if (argObject instanceof Integer) {
                int id = (int) argObject;
                return mc.getMovieById(id);
            } else {
                return mc.updateMovie((Movie) argObject) ? "Successfully updated element!" :  "Error occurred while updating element: element did not update.";
            }
        });

        putCommand("remove_by_id", (argObject) -> {
            int id = (int) argObject;
            return mc.removeMovieById(id) ? "Movie successfully deleted!" : "Movie with current id doesn't exists.";
        });

        putCommand("clear", (arg) -> {
            return mc.clear() ? "Collection cleared successfully!" : "Error occurred while cleaning collection: collection did not clean.";
        });

        putCommand("add_if_min", (argObject) -> {
            Movie movie = (Movie) argObject;
            if (movie.compareTo(mc.getLowestByOscars()) < 0) {
                return mc.addMovie(movie) ? "Successfully added element!" : "Error occurred while adding element: element did not add.";
            } else {
                return "Element wasn't added to collection because it's not the lowest one.";
            }
        });

        putCommand("remove_greater", (arg) -> {
            int id = (int) arg;
            return mc.removeGreater(id) ? "Greater movies successfully deleted!" : "There are no movies greater than this.";
        });

        putCommand("remove_lower", (arg) -> {
            int id = (int) arg;
            return (mc.removeLower(id)) ? "Greater movies successfully deleted!" : "There are no movies lower than this.";
        });

        putCommand("filter_less_than_oscars_count", (arg) -> {
            int oscarsCount = (int) arg;
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

        putCommand("filter_greater_than_director", (arg) -> {
            int id = (int) arg;
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

        putCommand("print_unique_oscars_count", (arg) -> {
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

        // Server command (Admins only)
        putCommand("ADMIN:shutdown_server", (argObject) -> {
            String str = (String) argObject;
            if (str.equals("request_code")) {
                return sendRandomCode();
            } else if (str.equals(serverCode)) {
                System.exit(0);
            }
            return "Invalid code: permission denied.";
        });
    }

    private String sendRandomCode() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        serverCode = String.format("%06d", number);
        log.info("CODE TO ACCESS SERVER COMMAND: {}", serverCode);
        return "Enter the 6 digit code, that was shown in server's logs:";
    }
}

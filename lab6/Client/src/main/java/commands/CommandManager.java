package commands;

import collection.Movie;
import collection.MovieCollection;
import console.Console;
import console.FileManager;
import console.MyFile;
import data.Person;
import exceptions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Class that operates Command
 */
public class CommandManager {
    private final Map<String, Command> commands;
    private final Map<String, String> descriptions;
    private final Scanner sc;
    private final MovieCollection mc;
    private final FileManager fm;

    private boolean printMode;
    private Supplier<String> valueGetter;

    private final HashSet<MyFile> fileHistory;
    
    public CommandManager(Scanner sc, MovieCollection mc, FileManager fm) {
        this.sc = sc;
        this.commands = new HashMap<>();
        this.descriptions = new HashMap<>();
        this.mc = mc;
        this.fm = fm;
        this.fileHistory = new HashSet<>();

        this.printMode = true;
        this.valueGetter = sc::nextLine;
        initCommands();
    }

    /**
     * Add new command
     * @param name name of command that used to execute command
     * @param description description of command showed in help mode
     * @param action what this command going to do (action of command)
     */
    public void putCommand(String name, String description, Command action){
        commands.put(name, action);
        descriptions.put(name, description);
    }

    /**
     * Get command by name
     * @param name the name of command that defined in {@link #putCommand(String, String, Command)}
     * @return Command
     */
    public Command getCommand(String name) {
        return commands.get(name);
    }

    /**
     * Run command by name defined in {@link #putCommand(String, String, Command)}
     * @param name command name
     * @param arg argument, given to command
     */
    public void runCommand(String name, String arg) {
        try {
            Command command = getCommand(name);
            if (command == null) throw new CommandNotFindException("Command not find.");
            getCommand(name).run(arg);
        } catch (NullPointerException e) {
            Console.println("Command did not run successfully, problem detected.");
        }
        catch (InvalidArgumentException e) {
            if (e.getMessage() != null) Console.println(e.getMessage());
        } catch (ExecuteScriptFailedException|CommandNotFindException e) {
            Console.println(e.getMessage());
        } catch (IOException e) {
            Console.println("Error: file not found.");
        }
    }

    /**
     * Run command with no argument
     * @param name command name
     */
    public void runCommand(String name) {
        runCommand(name, null);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Here is command list:\n*");
        for (String key: descriptions.keySet()) {
            sb.append(key).append(": ").append(descriptions.get(key)).append("\n*");
        }
        return sb.substring(0,sb.length()-2);
    }

    private void setPrintMode() {
        this.printMode = true;
        this.valueGetter = sc::nextLine;
    }

    private void offPrintMode(Queue<String> q) {
        this.printMode = false;
        valueGetter = q::poll;
    }

    private void initCommands(){
        putCommand("help", "get information about all available commands", (arg) -> Console.println(this)); 

        putCommand("info", "get information about movies collection (type, data, size)", (arg) -> Console.println(mc.getInfo())); 

        putCommand("show", "show all movies OR argument -> {id}, show specific movie", (arg) -> {
            if (arg == null) Console.println(mc);
            else {
                int id;
                try {
                    id = (int) new InputValidator(int.class, false, 0, Double.MAX_VALUE)
                            .validate(arg, null, true);
                } catch (ValidateException e) {
                    Console.println(e.getMessage());
                    throw new InvalidArgumentException();
                }
                Movie m = mc.getMovieById(id);

                // Format creating date
                final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MMMM.yyyy", Locale.US);

                System.out.printf("Id: %d\nName: %s\nCoordinates:\n\tx: %f\n\ty: %d\nCreation Date: %s\nOscarsCount: %d\nMovieGenre: %s\nMpaaRating: %s\n" +
                                "Director:\n\tName: %s\n\tweight: %f\n\tHairColor: %s\n\tLocation:\n\t\tx: %f\n\t\ty: %f\n\t\tName: %s\n",
                        m.getId(), m.getName(), m.getCoordinates().getX(), m.getCoordinates().getY(),
                        m.getCreationDate().format(dtf), m.getOscarsCount(), m.getMovieGenre(), m.getMpaaRating(), m.getDirector().getName(),
                        m.getDirector().getWeight(), m.getDirector().getHairColor(), m.getDirector().getLocation().getX(),
                        m.getDirector().getLocation().getY(), m.getDirector().getLocation().getName());
            }
        });

        putCommand("add", "add movie to collection", (arg) -> {
            Console.println("To add movie lead the instruction below:", printMode);
            Movie movie = MovieCollection.inputAndUpdateMovie(false, null, printMode, valueGetter);
            mc.addMovie(movie);
            Console.println("Successfully added element!", printMode);
        });

        putCommand("update", "argument -> {id}, update movie by id", (arg) -> {
            int id;
            try {
                id = (int) new InputValidator(int.class, false, 0, Double.MAX_VALUE)
                        .validate(arg, null, true);
            } catch (ValidateException e) {
                Console.println(e.getMessage());
                throw new InvalidArgumentException();
            }
            Movie oldMovie = mc.getMovieById(id);
            if (oldMovie == null) throw new InvalidArgumentException("Film with id " + id + " not found.");
            Console.println("To update movie lead the instruction below, to save previous value type '<':", printMode);
            MovieCollection.inputAndUpdateMovie(true, oldMovie, printMode, valueGetter);
            Console.println("Successfully updated element!", printMode);
        });

        putCommand("remove_by_id", "argument -> {id}, remove movie by id", (arg) -> {
            int id;
            try {
                id = (int) new InputValidator(int.class, false, 0, Double.MAX_VALUE)
                        .validate(arg, null, true);
            }
            catch (ValidateException e) {
                Console.println(e.getMessage());
                throw new InvalidArgumentException();
            }
            Console.println((mc.removeMovieById(id)) ? "Movie successfully deleted!" : "Movie with current id doesn't exist.", printMode);
        });

        putCommand("clear", "clear collection", (arg) -> {
            mc.clear();
            Console.println("Collection cleared successfully!", printMode);
        });

        putCommand("save", "save collection to file", (arg) -> {
            if (mc.getStartFilePath() != null) {
                try {
                    fm.writeToJsonFile(mc.getStartFilePath(), mc);
                    Console.println("Successfully saved to file!", printMode);
                } catch (FileNotFoundException e) {
                    Console.println("Error: saving file not found.", printMode);
                }
            } else {
                Console.println("Error: saving file not specified.", printMode);
            }
        });

        putCommand("execute_script", "argument -> {file_name}, execute script file", (arg) -> {

            String filePath;
            try {
                filePath = (String) new InputValidator(String.class, false)
                                    .validate(arg, null, true);
            } catch (ValidateException e) {
                Console.println(e.getMessage());
                throw new InvalidArgumentException();
            }

            MyFile myFile = new MyFile(filePath);
            if (!fileHistory.add(myFile)) {
                throw new FileRecursionException("File '" + filePath + "' referring to itself.");
            }
            Queue<String> q = fm.readCommandFile(filePath);
            offPrintMode(q);

            while (q.peek() != null) {
                String[] splitLine = q.poll().trim().split(" ");

                String command = null;
                String newArg = null;
                if (splitLine.length >= 1) {
                    command = splitLine[0];
                }
                if (splitLine.length >= 2) {
                    newArg = splitLine[1];
                }
                runCommand(command, newArg);
            }

            fileHistory.remove(myFile);
            setPrintMode();
        });

        putCommand("exit", "exit the program without saving data", (arg) -> { 
            Console.println("Bye, have a great time:)", printMode);
            System.exit(0);
        });

        putCommand("add_if_min", "add movie if it oscars count lower that the other collection", (arg) -> {
            Movie movie = MovieCollection.inputAndUpdateMovie(false, null, printMode, valueGetter);
            if (movie.compareTo(mc.getLowestByOscars()) < 0) {
                mc.addMovie(movie);
                Console.println("Successfully added element!", printMode);
            } else {
                Console.println("Element wasn't added to collection because it's not the lowest one.", printMode);
            }
        });

        putCommand("remove_greater", "argument -> {id}, remove from collection all movies if its oscars count greater than current movie", (arg) -> {
            int id;
            try {
                id = (int) new InputValidator(int.class, false, 0, Double.MAX_VALUE)
                        .validate(arg, null, false);
            } catch (ValidateException e) {
                Console.println(e.getMessage());
                throw new InvalidArgumentException();
            }
            Console.println((mc.removeGreater(id)) ? "Greater movies successfully deleted!" : "There are no movies greater than this.", printMode);
        });

        putCommand("remove_lower", "argument -> {id}, remove from collection all movies if its oscars count lower than current movie", (arg) -> {
            int id;
            try {
                id = (int) new InputValidator(int.class, false, 0, Double.MAX_VALUE)
                        .validate(arg, null, false);
            } catch (ValidateException e) {
                Console.println(e.getMessage());
                throw new InvalidArgumentException();
            }
            Console.println((mc.removeLower(id)) ? "Greater movies successfully deleted!" : "There are no movies lower than this.", printMode);
        });

        putCommand("filter_less_than_oscars_count", "argument -> {oscarsCount}, display all movies where oscars count lower than current", (arg) -> {
            int oscarsCount;
            try {
                oscarsCount = (int) new InputValidator(int.class, false, 0, Double.MAX_VALUE)
                        .validate(arg, null, false);
            } catch (ValidateException e) {
                Console.println(e.getMessage());
                throw new InvalidArgumentException();
            }
            List<Movie> subCollection = mc.getPQ().stream()
                    .filter(movie -> movie.getOscarsCount() < oscarsCount)
                    .collect(Collectors.toList());

            if (subCollection.isEmpty()) {
                Console.println("Films with less oscars was not found.");
            } else {
                Console.println("ID OSCARS NAME");
                subCollection.forEach(movie -> System.out.printf(Locale.US, "%2d %6d %s\n", movie.getId(), movie.getOscarsCount(), movie.getName()));
            }
        });

        putCommand("filter_greater_than_director", "argument -> {id}, display all movies where director greater than current", (arg) -> {
            int id;
            try {
                id = (int) new InputValidator(int.class, false, 0, Double.MAX_VALUE)
                        .validate(arg, null, false);
            } catch (ValidateException e) {
                Console.println(e.getMessage());
                throw new InvalidArgumentException();
            }
            Person d = mc.getMovieById(id).getDirector();
            List<Movie> subCollection = mc.getPQ().stream()
                    .filter(movie -> movie.getDirector().compareTo(d) > 0)
                    .collect(Collectors.toList());

            if (subCollection.isEmpty()) {
                Console.println("Films with greater directors was not found.");
            } else {
                Console.println("ID DIRECTOR NAME");
                subCollection.forEach(movie -> System.out.printf(Locale.US, "%2d %8.2f %s\n", movie.getId(), movie.getDirector().getWeight(), movie.getName()));
            }
        });

        putCommand("print_unique_oscars_count", "print all unique values of oscars count in collection", (arg) -> {
            Set<Integer> uniqOscar = new HashSet<>();
            mc.getPQ().forEach(movie -> uniqOscar.add(movie.getOscarsCount()));

            if (uniqOscar.isEmpty()) {
                Console.println("Collection is empty, so no unique oscars.");
            } else {
                Console.println("All unique oscars count values (total " + uniqOscar.size() + "):");
                uniqOscar.forEach(Console::printWithSpace);
                Console.println();
            }
        });
    }
}

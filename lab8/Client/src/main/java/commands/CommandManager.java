package commands;

import client.Client;
import console.*;
import data.Movie;
import exceptions.*;
import network.CommandRequest;
import network.CommandResponse;
import network.Common;

import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.NoConnectionPendingException;
import java.util.*;
import java.util.function.Supplier;

/**
 * Class that operates Command
 */
public class CommandManager {
    private final Map<String, Command> commands;
    private final Map<String, String> descriptions;
    private final Map<String, Boolean> onlyAuthorized;
    private final Properties props;
    private final Client client;

    private final Console console;
    private Supplier<String> valueGetter;

    private final HashSet<MyFile> fileHistory;


    public CommandManager(Console console, Client client, String configFile) throws IOException {
        this.commands = new HashMap<>();
        this.descriptions = new HashMap<>();
        this.onlyAuthorized = new HashMap<>();
        this.client = client;

        this.props = new Properties();
        props.load(new FileManager().getResourcesStream(configFile));

        this.console = console;
        this.valueGetter = console::readLine;

        this.fileHistory = new HashSet<>();
        initCommands();
    }

    /**
     * Add new command
     * @param name name of command that used to execute command
     * @param description description of command showed in help mode
     * @param action what this command going to do (action of command)
     */
    public void putCommand(String name, boolean onlyAuthorized, String description, Command action){
        commands.put(name, action);
        if (description != null) descriptions.put(name, description);
        this.onlyAuthorized.put(name, onlyAuthorized);
    }

    /**
     * Get command by name
     * @param name the name of command that defined in {@link #putCommand(String, boolean, String, Command)}
     * @return Command
     */
    public Command getCommand(String name) {
        return commands.get(name);
    }

    /**
     * Run command by name defined in {@link #putCommand(String, boolean, String, Command)}
     * @param name command name
     * @param arg argument, given to command
     */
    public CommandResponse runCommand(String name, String arg) {
        CommandResponse cRes = new CommandResponse();
        try {
            Command command = getCommand(name);
            if (command == null) throw new CommandNotFindException("Command not found.");
            if (onlyAuthorized.get(name) && !client.isAuthorized()) {
                throw new AuthorizationException("This command is allowed only for authorized users. " +
                        "(Try '/sign_in' or '/sign_up' to log in or create new account)");
            } else {
                return getCommand(name).run(name, arg);
            }
        } catch (NoConnectionPendingException e) {
            cRes.setExitCode(2);
            cRes.setMessage("Server is not responding. Try to run command later.");
        } catch (CommandNotFindException e) {
            cRes.setExitCode(5);
            cRes.setMessage("Command not found.");
        } catch (CommandInterruptedException e) {
            cRes.setExitCode(6);
            if (e.getMessage() != null) cRes.setMessage(e.getMessage());
        } catch (AuthorizationException e) {
            cRes.setExitCode(9);
            if (e.getMessage() != null) cRes.setMessage(e.getMessage());
        } catch (InvalidArgumentException | ExecuteScriptFailedException e) {
            cRes.setExitCode(10);
            if (e.getMessage() != null) cRes.setMessage(e.getMessage());
        } catch (IOException e) {
            cRes.setExitCode(10);
            cRes.setMessage("Error: file not found.");
        } catch (NullPointerException e) {
            cRes.setExitCode(10);
            cRes.setMessage("Command did not run successfully, problem detected.");
        }
        return cRes;
    }

    public CommandResponse runCommand(String name) {
        return runCommand(name, null);
    }

    private CommandResponse getResponseFromServer(String name) {
        return getResponseFromServer(name, null, 0);
    }

    private <T extends Serializable> CommandResponse getResponseFromServer(String name, T arg) {
        return getResponseFromServer(name, arg, 0);
    }

    private <T extends Serializable> CommandResponse getResponseFromServer(String name, T arg, int count) {
        return client.sendThenReceive(new CommandRequest(name, count, arg, client.getUsername(), client.getPassword()));
    }

    private void setPrintMode() {
        console.setPrintMode(true);
        valueGetter = console::readLine;
    }

    public void setUIMode(Queue<String> q) {
        console.setPrintMode(false);
        valueGetter = q::poll;
    }

    private void offPrintMode(Queue<String> q) {
        console.setPrintMode(false);
        valueGetter = q::poll;
    }

    private int intValidator(String arg) throws InvalidArgumentException {
        try {
            return (int) new InputValidator(int.class, false, 0, Double.MAX_VALUE)
                    .validate(arg, null, true);
        }
        catch (ValidateException e) {
            Console.println(e.getMessage());
            throw new InvalidArgumentException();
        }
    }

    private void initCommands(){
        putCommand("help", false,"get information how to use app", (name, arg) -> {
            StringBuilder sb = new StringBuilder("All available commands:\n*");
            descriptions.keySet().stream()
                    .filter(key -> onlyAuthorized.get(key).compareTo(client.isAuthorized()) <= 0)
                    .filter(descriptions::containsKey)
                    .sorted()
                    .forEach(key -> sb.append(key).append(": ").append(descriptions.get(key)).append("\n*"));
            return new CommandResponse(sb.substring(0,sb.length()-2));
        });

        putCommand("info", false,"get information about movies collection (type, data, size)", (name, arg) ->
                getResponseFromServer(name));

        putCommand("show", false,"show all movies OR argument -> {id}, show specific movie", (name, arg) -> {
            Integer id = (arg!=null) ? intValidator(arg) : null;
            return getResponseFromServer(name, id);
        });

        putCommand("$get", false, null, (name, arg) -> getResponseFromServer(name));

        putCommand("exit", false,"exit the client program", (name, arg) -> {
            Console.println("Closing client app...");
            System.exit(0);
            return new CommandResponse();
        });

        putCommand("filter_less_than_oscars_count", false, "argument -> {oscarsCount}, display all movies where oscars count lower than current", (name, arg) -> {
            int oscarsCount = intValidator(arg);
            return getResponseFromServer(name, oscarsCount);
        });

        putCommand("filter_greater_than_director", false,"argument -> {id}, display all movies where director greater than current", (name, arg) -> {
            int id = intValidator(arg);
            return getResponseFromServer(name, id);
        });

        putCommand("print_unique_oscars_count", false,"print all unique values of oscars count in collection", (name, arg) ->
                getResponseFromServer(name));

        putCommand("/sign_in", false,"sign in to account", (name, arg) -> {
            if (client.isAuthorized()) {
                return new CommandResponse(11, "To sign in another account, you need to log out from current account ('/sign_out').");
            }
            console.printMode("Enter username: ");
            String username = valueGetter.get();
            if (username.isEmpty()) {
                return new CommandResponse(12, "Username can't be empty.");
            }

            CommandResponse cRes = getResponseFromServer(name, username);
            if (cRes.getExitCode() != 0) return cRes;

            console.printMode("Enter password [keep empty if no password set]: ");
            String password = valueGetter.get();

            SHA224 sha224 = new SHA224(props.getProperty("pepper"), false);
            password = sha224.getHashString(password);
            cRes = getResponseFromServer(name, username + "::" + password, 1);
            if (cRes.getExitCode() == 0) {
                client.setUsername(username);
                client.setPassword(password);
                client.authorize();
                return new CommandResponse("You have successfully logged in as " + username + ".");
            }
            return cRes;
        });

        putCommand("/sign_up", false,"create new account", (name, arg) -> {
            if (client.isAuthorized()) {
                return new CommandResponse(11, "To sign up, you need to log out from current account ('/sign_out').");
            }
            console.printMode("Create a username: ");

            String username = valueGetter.get();
            if (username.isEmpty()) {
                console.printlnMode("Username can't be empty.");
            } else {
                CommandResponse cRes = getResponseFromServer(name, username);
                if (cRes.getExitCode() != 0) return cRes;
            }
            console.printMode("Enter password [keep empty if you don't want password]: ");
            String password;
            while(true) {
                password = valueGetter.get();
                console.printMode("Repeat password [keep empty if you don't want password]: ");
                if (valueGetter.get().equals(password)) {
                    break;
                }
                console.printMode("Passwords do not match. Enter password again: ");
            }
            SHA224 sha224 = new SHA224(props.getProperty("pepper"), false);
            password = sha224.getHashString(password);
            CommandResponse cRes = getResponseFromServer(name, username + "::" + password, 1);
            if (cRes.getExitCode() == 0) {
                client.setUsername(username);
                client.setPassword(password);
                client.authorize();
                return new CommandResponse("You have successfully create account and logged in as " + username + ".");
            }
            return cRes;
        });

        putCommand("/sign_out", true, "sign out from account", (name, arg) -> {
            client.signOut();
            return new CommandResponse("You have successfully sign out.");
        });

        putCommand("add", true,"add movie to collection", (name, arg) -> {
            console.printlnMode("To add movie lead the instruction below:");
            Movie movie = Common.inputAndUpdateMovie(false, null, console.isPrintMode(), valueGetter);
            movie.setUsername(client.getUsername());
            return getResponseFromServer(name, movie);
        });

        putCommand("update", true,"argument -> {id}, update movie by id", (name, arg) -> {
            int id = intValidator(arg);
            CommandResponse cRes = getResponseFromServer(name, id);
            if (cRes.getObject() == null) {
                return cRes;
            }
            else {
                Movie oldMovie = (Movie) cRes.getObject();
                console.printlnMode("To update movie lead the instruction below, to save previous value type '<':");
                Movie movie = Common.inputAndUpdateMovie(true, oldMovie, console.isPrintMode(), valueGetter);
                return getResponseFromServer(name, movie, 1);
            }
        });

        putCommand("remove_by_id", true,"argument -> {id}, remove movie by id", (name, arg) -> {
            int id = intValidator(arg);
            return getResponseFromServer(name, id);
        });

        putCommand("clear", true,"clear collection", (name, arg) ->
                getResponseFromServer(name));

        putCommand("execute_script", true,"argument -> {file_name}, execute script file", (name, arg) -> {
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
            Queue<String> q = FileManager.readCommandFile(filePath);
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
            return null;
        });

        putCommand("add_if_min", true, "add movie if it oscars count lower that the other collection", (name, arg) -> {
            console.printlnMode("To add movie lead the instruction below:");
            Movie movie = Common.inputAndUpdateMovie(false, null, console.isPrintMode(), valueGetter);
            movie.setUsername(client.getUsername());
            return getResponseFromServer(name, movie);
        });

        putCommand("remove_greater", true,"argument -> {id}, remove from collection all movies if its oscars count greater than current movie", (name, arg) -> {
            int id = intValidator(arg);
            return getResponseFromServer(name, id);
        });

        putCommand("remove_lower", true,"argument -> {id}, remove from collection all movies if its oscars count lower than current movie", (name, arg) -> {
            int id = intValidator(arg);
            return getResponseFromServer(name, id);
        });
    }
}

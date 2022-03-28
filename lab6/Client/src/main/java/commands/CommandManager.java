package commands;

import console.Client;
import console.Console;
import console.FileManager;
import console.MyFile;
import exceptions.*;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

/**
 * Class that operates Command
 */
public class CommandManager {
    private final Map<String, Command> commands;
    private final Map<String, String> descriptions;
    private final Scanner sc;
    private final Client client;

    private boolean printMode;
    private Supplier<String> valueGetter;


    public CommandManager(Scanner sc, Client client, FileManager fm) {
        this.sc = sc;
        this.commands = new HashMap<>();
        this.descriptions = new HashMap<>();
        this.client = client;

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
            getCommand(name).run(name, arg);
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

    private <T> String runOnServer(CommandPacket<T> commandPacket) {
        return client.sendThenReceive(commandPacket);
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
        putCommand("help", "get information about all available commands", (name, arg) -> Console.println(this)); 
        
        putCommand("info", "get information about movies collection (type, data, size)", (name, arg) -> runOnServer(new CommandPacket<>(name)));

        putCommand("show", "show all movies OR argument -> {id}, show specific movie", (name, arg) -> {

        });

        putCommand("add", "add movie to collection", (name, arg) -> {

        });

        putCommand("update", "argument -> {id}, update movie by id", (name, arg) -> {

        });

        putCommand("remove_by_id", "argument -> {id}, remove movie by id", (name, arg) -> {

        });

        putCommand("clear", "clear collection", (name, arg) -> {

        });

        putCommand("execute_script", "argument -> {file_name}, execute script file", (name, arg) -> {

        });

        putCommand("exit", "exit the program without saving data", (name, arg) -> { 
            Console.println("Bye!", printMode);
            System.exit(0);
        });

        putCommand("add_if_min", "add movie if it oscars count lower that the other collection", (name, arg) -> {

        });

        putCommand("remove_greater", "argument -> {id}, remove from collection all movies if its oscars count greater than current movie", (name, arg) -> {

        });

        putCommand("remove_lower", "argument -> {id}, remove from collection all movies if its oscars count lower than current movie", (name, arg) -> {

        });

        putCommand("filter_less_than_oscars_count", "argument -> {oscarsCount}, display all movies where oscars count lower than current", (name, arg) -> {

        });

        putCommand("filter_greater_than_director", "argument -> {id}, display all movies where director greater than current", (name, arg) -> {

        });

        putCommand("print_unique_oscars_count", "print all unique values of oscars count in collection", (name, arg) -> {

        });
    }
}

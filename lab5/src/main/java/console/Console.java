package console;

import commands.CommandManager;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Class represent console, that produce interactive input/output with user
 */
public class Console {
    public final Scanner sc;
    private final CommandManager cm;

    public Console(Scanner sc, CommandManager cm) {
        this.sc = sc;
        this.cm = cm;
    }

    /**
     * Cycle, that listen user input before exit from program
     */
    public void interactiveMode() {
        //noinspection InfiniteLoopStatement
        while (true) {
            Console.print("$ ");
            String[] input = {};
            try {
                input = sc.nextLine().trim().split(" ");
            } catch (NoSuchElementException e) {
                cm.runCommand("exit");
            }
            String command = null;
            String arg = null;
            if (input.length >= 1) {
                command = input[0];
            }
            if (input.length >= 2) {
                arg = input[1];
            }
            cm.runCommand(command, arg);
        }
    }

    public static void print(Object printable){
        System.out.print(printable);
    }

    public static void printWithSpace(Object printable){
        System.out.print(printable + " ");
    }

    public static void println(){
        System.out.println();
    }

    public static void println(Object printable){
        System.out.println(printable);
    }

    public static void println(Object printable, boolean printMode){
        if (printMode) println(printable);
    }


}

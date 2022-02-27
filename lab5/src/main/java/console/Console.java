package console;

import commands.CommandManager;
import java.util.Scanner;

public class Console {
    public final Scanner sc;
    private final CommandManager cm;

    public Console(Scanner sc, CommandManager cm) {
        this.sc = sc;
        this.cm = cm;
    }

    public void interactiveMode() {
        //noinspection InfiniteLoopStatement
        while (true) {
            Console.print("$ ");
            String[] input = sc.nextLine().trim().split(" ");
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

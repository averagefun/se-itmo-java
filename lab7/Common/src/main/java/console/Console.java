package console;

import exceptions.CommandInterruptedException;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Class represent console, that produce input/output
 */
public class Console {
    private final Scanner sc;
    private boolean printMode;

    public Console(Scanner sc, boolean printMode) {
        this.sc = sc;
        this.printMode = printMode;
    }

    public Scanner getSc() {
        return sc;
    }

    public boolean isPrintMode() {
        return printMode;
    }

    public void setPrintMode(boolean printMode) {
        this.printMode = printMode;
    }

    public static void print(Object printable){
        System.out.print(printable);
    }

    public static void print(Object printable, boolean printMode){
        if (printMode) print(printable);
    }

    public static void println(Object printable){
        System.out.println(printable);
    }

    public static void println(Object printable, boolean printMode){
        if (printMode) println(printable);
    }

    public void printMode(Object printable){
        print(printable, printMode);
    }

    public void printlnMode(Object printable){
        println(printable, printMode);
    }

    public String readLine() throws CommandInterruptedException {
        try {
            String line = sc.nextLine().trim();
            if (line.equals("$home")) {
                throw new CommandInterruptedException("Command interrupted, returning back to home...");
            }
            return line;
        }
        catch (NoSuchElementException e) {
            System.exit(0);
        }
        return null;
    }

}

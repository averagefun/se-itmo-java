package console;

import exceptions.CommandInterruptedException;
import localization.MyBundle;
import localization.MyLocale;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Class represent console, that produce input/output
 */
public class Console {
    private final Scanner sc;
    private boolean printMode;
    private final static MyBundle bundle = MyBundle.getBundle("console", MyLocale.ENGLISH);

    public Console(Scanner sc, boolean printMode) {
        this.sc = sc;
        this.printMode = printMode;
    }

    public boolean isPrintMode() {
        return printMode;
    }

    public void setPrintMode(boolean printMode) {
        this.printMode = printMode;
    }

    public static void print(Object printable){
        String message = printable.toString();
        String bundleMessage = bundle.getString(message);
        System.out.print(bundleMessage.isEmpty() ? message : bundleMessage);
    }

    public static void print(Object printable, boolean printMode){
        if (printMode) print(printable);
    }

    public static void println(){
        System.out.println();
    }

    public static void println(Object printable){
        String message = printable.toString();
        String bundleMessage = bundle.getString(message);
        System.out.println(bundleMessage.isEmpty() ? message : bundleMessage);
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

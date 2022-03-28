package console;

import commands.CommandManager;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Class represent console, that produce interactive input/output with user
 */
public class Console {
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

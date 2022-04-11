package console;

/**
 * Class represent console, that produce output
 */
public class Console {
    public static void print(Object printable){
        System.out.print(printable);
    }

    public static void println(Object printable){
        System.out.println(printable);
    }

    public static void println(Object printable, boolean printMode){
        if (printMode) println(printable);
    }
}

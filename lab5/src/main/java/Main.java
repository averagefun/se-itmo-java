import collection.MovieCollection;
import commands.CommandManager;
import console.Console;
import console.FileManager;

import java.util.Scanner;

public class Main {

    // Lab5, variant 3117500
    public static void main(String[] args) {

        printLogo();
        Scanner sc = new Scanner(System.in);

        FileManager fm = new FileManager();

        MovieCollection mc;
        if (args.length >= 1) {
            mc = new MovieCollection(fm, args[0]);
        } else {
            mc = new MovieCollection();
        }

        CommandManager cmm = new CommandManager(sc, mc, fm);

        Console.println("Type 'help' for display commands list.");
        Console console = new Console(sc, cmm);
        console.interactiveMode();
    }

    public static void printLogo() {
        Console.println(
                        "      ##    ###    ##     ##    ###    ##          ###    ########  ######## \n" +
                        "      ##   ## ##   ##     ##   ## ##   ##         ## ##   ##     ## ##       \n" +
                        "      ##  ##   ##  ##     ##  ##   ##  ##        ##   ##  ##     ## ##       \n" +
                        "      ## ##     ## ##     ## ##     ## ##       ##     ## ########  #######  \n" +
                        "##    ## #########  ##   ##  ######### ##       ######### ##     ##       ## \n" +
                        "##    ## ##     ##   ## ##   ##     ## ##       ##     ## ##     ## ##    ## \n" +
                        " ######  ##     ##    ###    ##     ## ######## ##     ## ########   ######  \n"
        );
    }
}

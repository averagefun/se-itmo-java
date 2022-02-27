import collection.MovieCollection;
import commands.CommandManager;
import console.Console;
import console.FileManager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Lab5, variant

        printLogo();
        Scanner sc = new Scanner(System.in);

        FileManager fm = new FileManager();
        MovieCollection mc = new MovieCollection(fm, "db.json");
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

import collection.MovieCollection;
import commands.CommandManager;
import console.Console;
import console.FileManager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Lab5, variant 3117500

        printLogo();
        Scanner sc = new Scanner(System.in);


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

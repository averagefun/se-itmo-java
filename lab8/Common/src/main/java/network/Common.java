package network;

import console.Console;
import console.InputValidator;
import data.*;
import exceptions.CommandInterruptedException;
import exceptions.ExecuteScriptFailedException;

import java.util.function.Supplier;

public class Common {
    public final static int PORT = 8000;

    /**
     * Global process of input values to add, update, validate Movies
     */
    public static Movie inputAndUpdateMovie(boolean updMode, Movie movie, boolean printMode, Supplier<String> valueGetter)
            throws ExecuteScriptFailedException, CommandInterruptedException {
        String name = (String) new InputValidator(String.class, false)
                .loadPreviousValue(updMode, updMode ? movie.getName() : null)
                .processInput("movieName", printMode, valueGetter);

        Console.println("Type coordinates:", printMode);
        Float x = (Float) new InputValidator(Float.class, false)
                .loadPreviousValue(updMode, updMode ? movie.getCoordinates().getX() : null)
                .processInput("x (float)", printMode, valueGetter);

        long y = (long) new InputValidator(long.class, false, -863, Double.MAX_VALUE)
                .loadPreviousValue(updMode, updMode ? movie.getCoordinates().getY() : null)
                .processInput("y (long > -863)", printMode, valueGetter);
        Coordinates coordinates = new Coordinates(x,y);

        int oscarsCount = (int) new InputValidator(int.class, false, -1, 12)
                .loadPreviousValue(updMode, updMode ? movie.getOscarsCount() : null)
                .processInput("oscars", printMode, valueGetter);

        MovieGenre movieGenre = (MovieGenre) new InputValidator(MovieGenre.class, false)
                .loadPreviousValue(updMode, updMode ? movie.getMovieGenre() : null)
                .processInput("movieGenre", MovieGenre.values(), printMode, valueGetter);

        Object obj = new InputValidator(MpaaRating.class, true)
                .loadPreviousValue(updMode, updMode ? movie.getMpaaRating() : null)
                .processInput("mpaaRating", MpaaRating.values(), printMode, valueGetter);
        MpaaRating mpaaRating = (obj != null) ? ((MpaaRating) obj) : null;

        Console.println("Type director data:", printMode);
        String directorName = (String) new InputValidator(String.class, false)
                .loadPreviousValue(updMode, updMode ? movie.getDirector().getName() : null)
                .processInput("directorName", printMode, valueGetter);

        double directorWeight = (double) new InputValidator(double.class, false, 0, Double.MAX_VALUE)
                .loadPreviousValue(updMode, updMode ? movie.getDirector().getWeight() : null)
                .processInput("directorWeight (double)", printMode, valueGetter);

        Color hairColor = (Color) new InputValidator(Color.class, false)
                .loadPreviousValue(updMode, updMode ? movie.getDirector().getHairColor() : null)
                .processInput("hairColor", Color.values(), printMode, valueGetter);

        Double locationX = (Double) new InputValidator(Double.class, false)
                .loadPreviousValue(updMode, updMode ? movie.getDirector().getLocation().getX() : null)
                .processInput("locationX (double)", printMode, valueGetter);

        double locationY = (double) new InputValidator(double.class, false)
                .loadPreviousValue(updMode, updMode ? movie.getDirector().getLocation().getY() : null)
                .processInput("locationY (double)", printMode, valueGetter);

        String locationName = (String) new InputValidator(String.class, true)
                .loadPreviousValue(updMode, updMode ? movie.getDirector().getLocation().getName() : null)
                .processInput("locationName", printMode, valueGetter);

        Location location = new Location(locationX, locationY, locationName);
        Person director = new Person(directorName, directorWeight, hairColor, location);

        if (updMode) {
            movie.updateMovie(name, coordinates, oscarsCount, movieGenre, mpaaRating, director);
        } else {
            movie = new Movie(0, "", name, coordinates, oscarsCount, movieGenre, mpaaRating, director);
        }
        return movie;
    }
}

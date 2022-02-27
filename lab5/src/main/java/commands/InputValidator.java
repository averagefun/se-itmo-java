package commands;

import exceptions.ExecuteScriptFailedException;
import org.jetbrains.annotations.Nullable;
import console.Console;
import data.Color;
import data.MovieGenre;
import data.MpaaRating;
import exceptions.EmptyEntryException;
import exceptions.InvalidEnumEntryException;
import exceptions.InvalidRangeException;

import java.util.Arrays;
import java.util.function.Supplier;

public class InputValidator {
    private final Class<?> cl;
    private final Boolean canBeNull;
    private final double minVal;
    private final double maxVal;

    private boolean updateMode;
    private Object prevValue;

    public InputValidator(Class<?> cl, Boolean canBeNull, double minVal, double maxVal) {
        this.cl = cl;
        this.canBeNull = canBeNull;
        this.minVal = minVal;
        this.maxVal = maxVal;
    }

    public InputValidator(Class<?> cl, Boolean canBeNull) {
        this(cl, canBeNull, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public InputValidator loadPreviousValue(boolean updateMode, @Nullable Object prevValue) {
        if (updateMode) {
            this.updateMode = true;
            this.prevValue = prevValue;
        }
        return this;
    }

    public Object interactiveInput(String text, boolean printMode, Supplier<String> valueGetter) throws ExecuteScriptFailedException {
        if (printMode) {
            String strInsert = "";
            if (updateMode) {
                strInsert = " >> " + prevValue;
            }
            while (true) {
                Console.print("Type " + text + strInsert + " >>> ");
                String input = valueGetter.get();
                if (prevValue != null && input.equals("<")) return prevValue;
                Object obj = validate(input, false);
                if (obj != null) return obj;
            }
        } else {
            String input = valueGetter.get();
            if (prevValue != null && input.equals("<")) return prevValue;
            Object obj = validate(input, false);
            if (obj == null) throw new ExecuteScriptFailedException();
            return obj;
        }
    }

    public <T extends Enum<T>> Object interactiveInput(String text, T[] enumValues, boolean printMode, Supplier<String> valueGetter) throws ExecuteScriptFailedException {
        String textNew = text + " " + Arrays.asList(enumValues);
        return interactiveInput(textNew, printMode, valueGetter);
    }

    public Object validate(String input, Boolean arg) {
        try {
            if (!canBeNull && (input==null || input.isEmpty())) {
                throw new EmptyEntryException();
            }

            if (cl == String.class) {
                return input;

            } else if (cl == MovieGenre.class) {
                MovieGenre mg = MovieGenre.checkElement(input);
                if (mg != null) return mg;
                throw new InvalidEnumEntryException();
            } else if (cl == MpaaRating.class) {
                MpaaRating mr = MpaaRating.checkElement(input);
                if (mr != null) return mr;
                throw new InvalidEnumEntryException();
            } else if (cl == Color.class) {
                Color col = Color.checkElement(input);
                if (col != null) return col;
                throw new InvalidEnumEntryException();
            } else {
                if (cl == int.class) {
                    int num = Integer.parseInt(input);
                    if (num <= minVal || num >= maxVal) throw new InvalidRangeException();
                    return num;
                } else if (cl == long.class) {
                    long num = Long.parseLong(input);
                    if (num <= minVal || num >= maxVal) throw new InvalidRangeException();
                    return num;
                } else if (cl == double.class || cl == Double.class) {
                    double num = Double.parseDouble(input);
                    if (num <= minVal || num >= maxVal) throw new InvalidRangeException();
                    return num;
                } else if (cl == float.class || cl == Float.class) {
                    float num = Float.parseFloat(input);
                    if (num <= minVal || num >= maxVal) throw new InvalidRangeException();
                    return num;
                }
            }

        } catch (EmptyEntryException e) {
            Console.println(arg ? "Error: No argument find." : "Field can't be empty.");
        } catch (InvalidEnumEntryException e) {
            Console.println(arg ? "Argument not one of shown constants." : "Input value not one of shown constants.");
        }
        catch (NumberFormatException e) {
            Console.println(arg ? "Argument has wrong number format!" : "Invalid number format.");
        } catch (InvalidRangeException e) {
            Console.println(arg ? "Argument has wrong number range!" : "Invalid number range.");
        }
        return null;
    }
}

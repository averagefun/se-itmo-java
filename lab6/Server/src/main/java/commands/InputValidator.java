package commands;

import exceptions.*;
import org.jetbrains.annotations.Nullable;
import console.Console;
import data.Color;
import data.MovieGenre;
import data.MpaaRating;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

/**
 * Class represent validator, that validate user input by type, format, range
 */
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

    /**
     * Method load previous field value to memory in case of update (no add) mode.
     * Then in update mode user can see previous field value.
     * @param updateMode updateMode (no add)
     * @param prevValue previous field value
     * @return this object
     */
    public InputValidator loadPreviousValue(boolean updateMode, @Nullable Object prevValue) {
        if (updateMode) {
            this.updateMode = true;
            this.prevValue = prevValue;
        }
        return this;
    }

    /**
     * Method produce interactive input of current field.
     * @param text name of field that shows to user
     * @param printMode onn/off hint to users (only need in console mode, no need in execute_file mode)
     * @param valueGetter supplier, that get input field value from different streams (Scanner, File)
     * @return Object, cast to specified type
     * @throws ExecuteScriptFailedException exception show validate error while execute_script
     */
    public Object interactiveInput(String text, boolean printMode, Supplier<String> valueGetter) throws ExecuteScriptFailedException {
        if (printMode) {
            String strInsert = "";
            if (updateMode) {
                strInsert = " >> " + prevValue;
            }
            while (true) {
                Console.print("Type " + text + strInsert + " >>> ");
                String input = "";
                try {
                    input = valueGetter.get();
                } catch (NoSuchElementException e) {
                    Console.println("Bye!");
                    System.exit(0);
                }
                if (prevValue != null && input.equals("<")) return prevValue;

                try {
                    return validate(input, null, false);
                } catch (ValidateException e) {
                    Console.println(e.getMessage());
                }
            }
        } else {
            String input = valueGetter.get();
            try {
                if (prevValue != null && input.equals("<")) return prevValue;
                else if (input.equals("#validate_initial")) {
                    return validate((prevValue != null) ? prevValue.toString() : null,
                            text.substring(0, 1).toUpperCase() + text.substring(1) + ": ",
                            false);
                } else {
                    return validate(input, null, false);
                }
            } catch (ValidateException e) {
                Console.println(e.getMessage());
                throw new ExecuteScriptFailedException();
            }
        }
    }

    /**
     * Method produce interactive input of current enum field.
     * It's also show users all available enum values.
     * @param text name of field that show to user
     * @param enumValues enum values
     * @param printMode onn/off hint to users (only need in console mode, no need in execute_file mode)
     * @param valueGetter supplier, that get input field value from different streams (Scanner, File)
     * @param <T> enum class
     * @return Object, cast to specified type
     * @throws ExecuteScriptFailedException exception show validate error while execute_script
     */
    public <T extends Enum<T>> Object interactiveInput(String text, T[] enumValues, boolean printMode, Supplier<String> valueGetter) throws ExecuteScriptFailedException {
        String textNew = text + " " + Arrays.asList(enumValues);
        return interactiveInput(textNew, printMode, valueGetter);
    }

    /**
     * Validate input value by type and range
     * @return Object, cast to specified type
     */
    public Object validate(String input, String fieldName, Boolean arg) throws ValidateException {
        if (fieldName == null) fieldName = "";
        try {
            if (!canBeNull && (input==null || input.trim().isEmpty())) {
                throw new EmptyEntryException();
            }

            input = input.trim();

            if (cl == String.class) {
                if (input.trim().isEmpty()) return null;
                return input;

            } else if (cl == MovieGenre.class) {
                MovieGenre mg = MovieGenre.checkElement(input);
                if (mg != null) return mg;
                throw new InvalidEnumEntryException();
            } else if (cl == MpaaRating.class) {
                if (input.isEmpty()) return null;
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
            throw new ValidateException(arg ? "Error: No argument find." : fieldName + "Field can't be empty.");
        } catch (InvalidEnumEntryException e) {
            throw new ValidateException(arg ? "Argument not one of shown constants." : fieldName + "Input value not one of shown constants.");
        }
        catch (NumberFormatException e) {
            throw new ValidateException(arg ? "Argument has wrong number format!" : fieldName + "Invalid number format.");
        } catch (InvalidRangeException e) {
            throw new ValidateException(arg ? "Argument has wrong number range!" : fieldName + "Invalid number range.");
        }
        throw new ValidateException("Validation error.");
    }
}

package commands;

import exceptions.ExecuteScriptFailedException;
import exceptions.InvalidArgumentException;

import java.io.IOException;


/**
 * Functional interface that represents Command, available to running and throw exceptions
 */
@FunctionalInterface
public interface Command {
    void run(String arg) throws InvalidArgumentException, ExecuteScriptFailedException, IOException;
}

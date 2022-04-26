package commands;

import exceptions.ExecuteScriptFailedException;
import exceptions.InvalidArgumentException;

import java.io.IOException;


/**
 * Functional interface that represents Command, available to running and throw exceptions
 */
@FunctionalInterface
public interface Command {
    Object run(Object arg) throws InvalidArgumentException, ExecuteScriptFailedException, IOException;
}

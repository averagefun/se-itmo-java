package commands;

import exceptions.CommandInterruptedException;
import exceptions.ExecuteScriptFailedException;
import exceptions.InvalidArgumentException;
import network.CommandResponse;

import java.io.IOException;


/**
 * Functional interface that represents Command, available to running and throw exceptions
 */
@FunctionalInterface
public interface Command {
    CommandResponse run(String name, String arg) throws InvalidArgumentException, ExecuteScriptFailedException, IOException, CommandInterruptedException;
}

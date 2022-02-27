package commands;

import exceptions.ExecuteScriptFailedException;
import exceptions.InvalidArgumentException;

import java.io.IOException;

@FunctionalInterface
public interface Command {
    void run(String arg) throws InvalidArgumentException, ExecuteScriptFailedException, IOException;
}

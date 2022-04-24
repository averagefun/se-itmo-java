package commands;

import exceptions.ExecuteScriptFailedException;
import exceptions.InvalidArgumentException;

import java.io.IOException;
import java.sql.SQLException;


/**
 * Functional interface that represents Command, available to running and throw exceptions
 */
@FunctionalInterface
public interface Command {
    Object run(int userId, int count, Object arg) throws InvalidArgumentException, ExecuteScriptFailedException, IOException, SQLException;
}

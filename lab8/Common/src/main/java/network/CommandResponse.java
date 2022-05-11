package network;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class CommandResponse implements Serializable {
    private static final long serialVersionUID = 5198924033159344900L;
    private int exitCode = 0;
    private String message = "";
    private Object object;

    public CommandResponse() {
    }

    public CommandResponse(int exitCode, String message) {
        this.exitCode = exitCode;
        this.message = message;
    }

    public CommandResponse(String message) {
        this.message = message;
    }

    public CommandResponse(Object object) {
        this.object = object;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
        if (exitCode > 0 && message.isEmpty()) {
            message = "Command did not run successfully, problem detected.";
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setObject(Object object) {
        this.object = object;
    }


    /**
     * 0 - success
     * 1 - fatal server or database error
     * 2 - failed to connect to server
     * 5 - command not found
     * 6 - command interrupted
     * 9 - auth error
     * 10+ - command execution error
     */
    public int getExitCode() {
        return exitCode;
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    public Object getObject() {
        return object;
    }
}

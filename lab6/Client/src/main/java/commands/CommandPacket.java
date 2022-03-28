package commands;

import java.io.Serializable;

public class CommandPacket<T> implements Serializable {
    private final String name;
    private final T arg;

    public CommandPacket(String name, T arg) {
        this.name = name;
        this.arg = arg;
    }

    public CommandPacket(String name) {
        this.name = name;
        this.arg = null;
    }

    public String getName() {
        return name;
    }

    public T getArg() {
        return arg;
    }
}

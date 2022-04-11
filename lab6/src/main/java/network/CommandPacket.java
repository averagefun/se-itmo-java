package network;

import java.io.Serializable;

public class CommandPacket implements Serializable {
    private final String name;
    private final Object arg;

    public CommandPacket(String name) {
        this.name = name;
        this.arg = null;
    }

    public <T extends Serializable> CommandPacket(String name, T arg) {
        this.name = name;
        this.arg = arg;
    }

    public String getName() {
        return name;
    }

    public Object getArg() {
        return arg;
    }
}

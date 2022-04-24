package network;

import java.io.Serializable;

public class CommandPacket implements Serializable {
    private final String username;
    private final String password;
    private final String name;
    private final int count;
    private final Object arg;

    public CommandPacket(String name, int count, String username, String password) {
        this(name, count, username, password, null);
    }

    public <T extends Serializable> CommandPacket(String name, int count, T arg, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.arg = arg;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public Object getArg() {
        return arg;
    }

    public int getCount() {
        return count;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

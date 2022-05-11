package network;

import java.io.Serializable;

public class CommandRequest implements Serializable {
    private static final long serialVersionUID = 3707777629449859022L;
    private final String username;
    private final String password;
    private final String name;
    private final int count;
    private final Object arg;

    public <T extends Serializable> CommandRequest(String name, int count, T arg, String username, String password) {
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

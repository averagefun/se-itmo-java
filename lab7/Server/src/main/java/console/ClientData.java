package console;

import java.net.InetAddress;

public class ClientData {
    private final InetAddress inetAddress;
    private final int port;
    private final Object data;

    public ClientData(InetAddress inetAddress, int port, Object data) {
        this.inetAddress = inetAddress;
        this.port = port;
        this.data = data;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getPort() {
        return port;
    }

    public Object getData() {
        return data;
    }
}

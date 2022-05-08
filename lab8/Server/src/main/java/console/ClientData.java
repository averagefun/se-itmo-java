package console;

import network.CommandRequest;

import java.net.InetAddress;

public class ClientData {
    private final InetAddress inetAddress;
    private final int port;
    private final CommandRequest cReq;

    public ClientData(InetAddress inetAddress, int port, CommandRequest cReq) {
        this.inetAddress = inetAddress;
        this.port = port;
        this.cReq = cReq;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getPort() {
        return port;
    }

    public CommandRequest getCommandRequest() {
        return cReq;
    }
}

package console;

import network.CommandRequest;
import network.CommandResponse;

import java.net.InetAddress;

public class ProcessedClientData extends ClientData {
    private final CommandResponse cRes;

    public ProcessedClientData(InetAddress inetAddress, int port, CommandRequest cReq, CommandResponse cRes) {
        super(inetAddress, port, cReq);
        this.cRes = cRes;
    }

    public CommandResponse getCommandResponse() {
        return cRes;
    }
}

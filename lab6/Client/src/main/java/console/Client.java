package console;

import commands.CommandManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

public class Client {
    private final DatagramChannel datagramChannel;
    private SocketAddress socketAddress;

    public Client(InetAddress inetAddress) throws IOException {
        this.datagramChannel = DatagramChannel.open();
        this.socketAddress = new InetSocketAddress(inetAddress, 8000);
    }

    public <T extends Serializable> String sendThenReceive(T objToSend) {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(objToSend);
            byte[] data = baos.toByteArray();

            ByteBuffer byteBuffer = ByteBuffer.wrap(data);
            datagramChannel.send(byteBuffer, socketAddress);

            byteBuffer.clear();
            socketAddress = datagramChannel.receive(byteBuffer);

            return new String(byteBuffer.array());
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        InetAddress inetAddress = InetAddress.getByName("localhost");
        Client client = new Client(inetAddress);

        Scanner sc = new Scanner(System.in);
        FileManager fm = new FileManager();
        CommandManager cm = new CommandManager(sc, client, fm);

        Console c = new Console(sc, client, cm);
        c.interactiveMode();
    }
}

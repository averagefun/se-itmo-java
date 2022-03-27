import collection.MovieCollection;
import commands.CommandManager;
import console.Console;
import console.FileManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;

public class Server {

    private DatagramSocket datagramSocket;
    private byte[] buffer = new byte[256];

    public Server(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    public void receiveAndAnswer(Console console) {
        while(true) {
        try {
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(datagramPacket);
            InetAddress inetAddress = datagramPacket.getAddress();
            int port = datagramPacket.getPort();
            String messageFromClient = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
            console.interactiveMode(messageFromClient);
            System.out.println("Message from client: " + messageFromClient);
            datagramPacket = new DatagramPacket(buffer, buffer.length, inetAddress, port);
            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
            break;
        }
        }
    }

    public static void main(String[] args) throws SocketException {
        FileManager fm = new FileManager();

        MovieCollection mc;
        if (args.length >= 1) {
            mc = new MovieCollection(fm, args[0]);
        } else {
            mc = new MovieCollection();
        }

        Scanner sc = new Scanner(System.in);

        CommandManager cm = new CommandManager(sc, mc, fm);

        Console console = new Console(cm);

        DatagramSocket datagramSocket = new DatagramSocket(8000);
        Server server = new Server(datagramSocket);

        server.receiveAndAnswer(console);
    }

}

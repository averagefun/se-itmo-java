import collection.MovieCollection;
import commands.CommandManager;
import commands.CommandPacket;
import console.FileManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class Server {
    private final DatagramSocket datagramSocket;
    private final byte[] buffer = new byte[256];

    private final CommandManager cm;

    public Server(DatagramSocket datagramSocket, CommandManager cm) {
        this.datagramSocket = datagramSocket;
        this.cm = cm;
    }

    public void receiveAndAnswer() {
        while(true) {
        try {
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(datagramPacket);
            byte[] data = datagramPacket.getData();
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bais);

            Object response = ois.readObject();
            try {
                if (response.getClass() == CommandPacket.class) {
                    CommandPacket cp = (CommandPacket) ois.readObject();
                }

            }
            if (request instanceof CommandPacket) {

                cm.runCommand((CommandPacket<String>)request);
            }


            InetAddress inetAddress = datagramPacket.getAddress();
            int port = datagramPacket.getPort();

            datagramPacket = new DatagramPacket(buffer, buffer.length, inetAddress, port);
            datagramSocket.send(datagramPacket);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            break;
        }}
    }

    public static void main(String[] args) throws SocketException {
        FileManager fm = new FileManager();

        MovieCollection mc;
        if (args.length >= 1) {
            mc = new MovieCollection(fm, args[0]);
        } else {
            mc = new MovieCollection(fm);
        }

        Scanner sc = new Scanner(System.in);
        CommandManager cm = new CommandManager(sc, mc, fm);

        DatagramSocket datagramSocket = new DatagramSocket(8000);
        Server server = new Server(datagramSocket, cm);

        server.receiveAndAnswer();
    }

}

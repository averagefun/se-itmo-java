import java.io.*;
import java.lang.Math;

import collection.MovieCollection;
import commands.CommandManager;
import network.CommandPacket;
import console.FileManager;
import network.Common;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
    private final DatagramSocket datagramSocket;
    private final CommandManager cm;
    private final Logger log = LoggerFactory.getLogger(Server.class);

    public Server(DatagramSocket datagramSocket, CommandManager cm) {
        this.datagramSocket = datagramSocket;
        this.cm = cm;
    }

    private void receiveAndAnswer() throws IOException, ClassNotFoundException {
        byte[] receiveBuffer = new byte[10000];
        DatagramPacket receiveDatagramPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        datagramSocket.receive(receiveDatagramPacket);

        InetAddress inetAddress = receiveDatagramPacket.getAddress();
        int port = receiveDatagramPacket.getPort();

        log.info("received data from {}:{}", inetAddress, port);

        ByteArrayInputStream bais = new ByteArrayInputStream(receiveBuffer);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object receivedObject = ois.readObject();

        // processing command
        Object toSendObject = cm.runCommand((CommandPacket) receivedObject);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(toSendObject);
        byte[] sendBuffer = baos.toByteArray();

        DatagramPacket sendDatagramPacket = new DatagramPacket(sendBuffer, sendBuffer.length,
                inetAddress, port);
        datagramSocket.send(sendDatagramPacket);

        log.info("send {} bytes of data to {}:{}", sendBuffer.length, inetAddress, port);
    }
    
    public void start() {
        log.info("successfully started at port {}", Common.PORT);
        while (true) {
            try {
                receiveAndAnswer();
            } catch (IOException | ClassNotFoundException e) {
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
            mc = new MovieCollection(fm);
        }

        CommandManager cm = new CommandManager(mc, fm);

        DatagramSocket datagramSocket = new DatagramSocket(Common.PORT);
        Server server = new Server(datagramSocket, cm);
        server.start();
    }

}

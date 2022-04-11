import java.io.*;
import java.lang.Math;

import collection.MovieCollection;
import commands.CommandManager;
import network.CommandPacket;
import console.FileManager;
import network.Common;
import sun.security.util.ArrayUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server {
    private final DatagramSocket datagramSocket;

    private final CommandManager cm;

    public Server(DatagramSocket datagramSocket, CommandManager cm) {
        this.datagramSocket = datagramSocket;
        this.cm = cm;
    }

    public void receiveAndAnswer() {
        while(true) {
        try {
            byte[] sizeArr = new byte[10];
            DatagramPacket datagramPacket = new DatagramPacket(sizeArr, sizeArr.length);
            datagramSocket.receive(datagramPacket);
            int size = 0;
            for (int i = 0; i < sizeArr.length; i++) {
                size += sizeArr[i] * Math.pow(10, sizeArr.length-1-i);
            }

            byte[] buffer = new byte[size];
            datagramPacket = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(datagramPacket);
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = new ObjectInputStream(bais);

            Object receive = ois.readObject();
            Object objToSend = cm.runCommand((CommandPacket) receive);

            InetAddress inetAddress = datagramPacket.getAddress();
            int port = datagramPacket.getPort();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(objToSend);

            buffer = baos.toByteArray();
            size = buffer.length;
            sizeArr = new byte[10];
            for (int i = 0; (i < 10) && (size > 0); i++) {
                sizeArr[i] = (byte) (size % 10);
                size /= 10;
            }
            ArrayUtil.reverse(sizeArr);
            datagramPacket = new DatagramPacket(sizeArr, sizeArr.length, inetAddress, port);
            datagramSocket.send(datagramPacket);
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

        CommandManager cm = new CommandManager(mc, fm);

        DatagramSocket datagramSocket = new DatagramSocket(Common.PORT);
        Server server = new Server(datagramSocket, cm);

        server.receiveAndAnswer();
    }

}

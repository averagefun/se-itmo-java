package console;

import commands.CommandManager;
import network.Common;
import sun.security.util.ArrayUtil;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client {
    private final DatagramChannel datagramChannel;
    private final SocketAddress socketAddress;

    public Client(InetAddress inetAddress) throws IOException {
        this.datagramChannel = DatagramChannel.open();
        this.socketAddress = new InetSocketAddress(inetAddress, Common.PORT);
    }

    public <T extends Serializable> Object sendThenReceive(T objToSend) {
        try {
            // send the size of next package
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(objToSend);
            int size = baos.size();
            byte[] sizeArr = new byte[10];
            for (int i = 0; (i < 10) && (size > 0); i++) {
                sizeArr[i] = (byte) (size % 10);
                size /= 10;
            }
            ArrayUtil.reverse(sizeArr);

            ByteBuffer byteBuffer = ByteBuffer.wrap(sizeArr);
            datagramChannel.send(byteBuffer, socketAddress);

            byteBuffer = ByteBuffer.wrap(baos.toByteArray());
            datagramChannel.send(byteBuffer, socketAddress);
            byteBuffer.clear();

            sizeArr = new byte[10];
            datagramChannel.receive(ByteBuffer.wrap(sizeArr));
            size = 0;
            for (int i = 0; i < sizeArr.length; i++) {
                size += sizeArr[i] * Math.pow(10, sizeArr.length-1-i);
            }

            byte[] buffer = new byte[size];
            datagramChannel.receive(ByteBuffer.wrap(buffer));

            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = new ObjectInputStream(bais);

            return ois.readObject();
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Cycle, that listen user input before exit from program
     */
    public void interactiveMode(Scanner sc, CommandManager cm) {
        //noinspection InfiniteLoopStatement
        while(true) {
            Console.print("$ ");
            String[] input = {};
            try {
                input = sc.nextLine().trim().split(" ");
            } catch (NoSuchElementException e) {
                cm.runCommand("exit");
            }
            String command = null;
            String arg = null;
            if (input.length >= 1) {
                command = input[0];
            }
            if (input.length >= 2) {
                arg = input[1];
            }
            cm.runCommand(command, arg);
        }
    }

    public static void main(String[] args) throws IOException {
        Console.println("Welcome to client app. Enter command or type 'help'.");

        InetAddress inetAddress = InetAddress.getByName("localhost");
        Client client = new Client(inetAddress);

        Scanner sc = new Scanner(System.in);
        FileManager fm = new FileManager();
        CommandManager cm = new CommandManager(sc, client, fm);

        client.interactiveMode(sc, cm);
    }
}

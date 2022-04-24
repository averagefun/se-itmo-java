package console;

import commands.CommandManager;
import network.Common;

import java.io.*;
import java.net.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.NoConnectionPendingException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client {
    private final DatagramChannel datagramChannel;
    private final SocketAddress socketAddress;

    public Client(InetAddress inetAddress) throws IOException {
        this.datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);
        this.socketAddress = new InetSocketAddress(inetAddress, Common.PORT);
    }

    private void send(Object objToSend) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(objToSend);

        datagramChannel.send(ByteBuffer.wrap(baos.toByteArray()), socketAddress);
    }

    public <T extends Serializable> Object sendThenReceive(T objToSend) {

        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1000);

            F: for (int i = 0; i < 80; i++) {
                if (i%5==0) send(objToSend);
                for (int j = 0; j<10; j++) {
                    datagramChannel.receive(byteBuffer);
                    if (byteBuffer.position() != 0) break F;
                }
                if (i == 40) {
                    Console.println("Trying to connect to the server...");
                }
                Thread.sleep(150);
            }

            if (byteBuffer.position() == 0) throw new NoConnectionPendingException();

            ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer.array());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
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
        CommandManager cm = new CommandManager(sc, client);

        client.interactiveMode(sc, cm);
    }
}

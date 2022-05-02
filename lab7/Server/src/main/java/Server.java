import java.io.*;

import collection.MovieCollection;
import commands.CommandManager;
import console.ClientData;
import console.Console;
import database.Database;
import exceptions.MyExceptions;
import network.CommandPacket;
import network.Common;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
    private final DatagramSocket datagramSocket;
    private final CommandManager cm;
    private final static Logger log = LoggerFactory.getLogger(Server.class);
    private final BlockingQueue<ClientData> queueToProcess;
    private final BlockingQueue<ClientData> queueToSend;

    public Server(DatagramSocket datagramSocket, CommandManager cm) {
        this.datagramSocket = datagramSocket;
        this.cm = cm;
        this.queueToProcess = new LinkedBlockingQueue<>();
        this.queueToSend = new LinkedBlockingQueue<>();
    }

    private void readRequest() throws IOException {
            byte[] receiveBuffer = new byte[10000];
            DatagramPacket receiveDatagramPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            datagramSocket.receive(receiveDatagramPacket);

            new Thread(() -> {
                try {
                    log.debug("{} started reading client request", Thread.currentThread().getName());
                    InetAddress inetAddress = receiveDatagramPacket.getAddress();
                    int port = receiveDatagramPacket.getPort();

                    log.info("received data from {}:{}", inetAddress, port);

                    ByteArrayInputStream bais = new ByteArrayInputStream(receiveBuffer);
                    ObjectInputStream ois = new ObjectInputStream(bais);

                    ClientData cd = new ClientData(inetAddress, port, ois.readObject());
                    queueToProcess.add(cd);
                    log.debug("{} finished reading command", Thread.currentThread().getName());
                } catch (IOException | ClassNotFoundException ignored) {}
            }).start();
    }

    public void process(ClientData cd) {
        log.debug("{} started processing command", Thread.currentThread().getName());
        ClientData newCd = new ClientData(cd.getInetAddress(), cd.getPort(),
                cm.runCommand((CommandPacket) cd.getData()));
        queueToSend.add(newCd);
        log.debug("{} finished processing command", Thread.currentThread().getName());
    }

    public void sendToClient(ClientData cd) {
        log.debug("{} started sending result to client", Thread.currentThread().getName());
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(cd.getData());
            byte[] sendBuffer = baos.toByteArray();

            DatagramPacket sendDatagramPacket = new DatagramPacket(sendBuffer, sendBuffer.length,
                    cd.getInetAddress(), cd.getPort());
            datagramSocket.send(sendDatagramPacket);
            log.info("send {} bytes of data to {}:{}", sendBuffer.length, cd.getInetAddress(), cd.getPort());
        } catch (IOException ignored) {
        }
        log.debug("{} finished sending result to client", Thread.currentThread().getName());

    }

    public void startShell(Scanner sc, Thread processor, Thread sender) {
        log.info("successfully started server shell: type 'exit' to safely shut down server");
        while(true) {
            String input = "";
            try {
                input = sc.nextLine().trim();
            } catch (NoSuchElementException e) {
                System.exit(0);
            }
            if (input.equals("exit")) {
                processor.interrupt();
                sender.interrupt();

                log.info("shut downing server...");

                int i;
                for (i = 0; processor.isAlive() || sender.isAlive(); i++) {
                    try {
                        //noinspection BusyWait
                        Thread.sleep(300);
                    } catch (InterruptedException ignored) {}
                    if (i%10==4) Console.print(".");
                }
                if (i >= 4) Console.println();

                try {
                    cm.getDb().closeConnection();
                    log.info("successfully closed database connection");
                } catch (SQLException e) {
                    log.error("closing connection failed:\n{}", MyExceptions.getStringStackTrace(e));
                }
                log.info("finishing process...");
                break;
            }
            else {
                log.warn("unknown command");
            }
        }
    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("helios")) Database.setConfigFile("db_helios.cfg");

        MovieCollection mc = new MovieCollection();
        CommandManager cm = new CommandManager(mc);

        DatagramSocket datagramSocket;
        try {
            datagramSocket = new DatagramSocket(Common.PORT);
        } catch (SocketException e) {
            log.error("problem with DatagramSocket on port {}:\n{}", Common.PORT, MyExceptions.getStringStackTrace(e));
            return;
        }

        Server server = new Server(datagramSocket, cm);
        log.info("successfully started listening requests at port {}", Common.PORT);

        Thread receiver = new Thread(() -> {
            while (true) {
                try {
                    server.readRequest();
                } catch (IOException ignored) {}
            }
        });
        receiver.setDaemon(true);
        receiver.start(); // start receiving and processing requests

        Thread processor = new Thread(() -> {
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            while (true) {
                try {
                    ClientData cd = server.queueToProcess.take();
                    executorService.execute(() -> server.process(cd));
                } catch (InterruptedException e) {
                    executorService.shutdown();
                    break;
                }
            }
        });
        processor.start();

        Thread sender = new Thread(() -> {
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            while (true) {
                try {
                    ClientData cd = server.queueToSend.take();
                    executorService.execute(() -> server.sendToClient(cd));
                } catch (InterruptedException e) {
                    executorService.shutdown();
                    break;
                }
            }
        });
        sender.start();

        Scanner sc = new Scanner(System.in);
        server.startShell(sc, processor, sender); // start reading directly server commands
    }

}

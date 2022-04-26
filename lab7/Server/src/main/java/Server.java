import java.io.*;

import collection.MovieCollection;
import com.google.gson.JsonSyntaxException;
import commands.CommandManager;
import console.ClientData;
import console.FileManager;
import console.JsonParser;
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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
    private final DatagramSocket datagramSocket;
    private final CommandManager cm;
    private final static Logger log = LoggerFactory.getLogger(Server.class);
    private final Queue<ClientData> queueToProcess;
    private final Queue<ClientData> queueToSend;

    public Server(DatagramSocket datagramSocket, CommandManager cm) {
        this.datagramSocket = datagramSocket;
        this.cm = cm;
        this.queueToProcess = new ArrayDeque<>();
        this.queueToSend = new ArrayDeque<>();
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
                } catch (IOException | ClassNotFoundException ignored) {}
            }).start();
    }

    public void process() {
        while (!queueToProcess.isEmpty()) {
            Executor executor = Executors.newFixedThreadPool(10);
            ClientData cd = queueToProcess.poll();
            executor.execute(() -> {
                log.debug("{} started processing command", Thread.currentThread().getName());
                assert cd != null;
                ClientData newCd = new ClientData(cd.getInetAddress(), cd.getPort(),
                        cm.runCommand((CommandPacket) cd.getData()));
                queueToSend.add(newCd);
            });
        }
    }

    public void sendToClient () {
        while (!queueToSend.isEmpty()) {
            Executor executor = Executors.newFixedThreadPool(10);
            ClientData cd = queueToSend.poll();
            executor.execute(() -> {
                log.debug("{} started sending result to client", Thread.currentThread().getName());
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    assert cd != null;
                    oos.writeObject(cd.getData());
                    byte[] sendBuffer = baos.toByteArray();

                    DatagramPacket sendDatagramPacket = new DatagramPacket(sendBuffer, sendBuffer.length,
                            cd.getInetAddress(), cd.getPort());
                    datagramSocket.send(sendDatagramPacket);
                    log.info("send {} bytes of data to {}:{}", sendBuffer.length, cd.getInetAddress(), cd.getPort());
                } catch (IOException ignored) {}
            });
        }
    }

    public void startShell(Scanner sc, Thread processor, Thread sender) {
        log.info("successfully started server shell: type 'exit' to safely shut down server.");
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

                while (processor.isAlive() && sender.isAlive()) {
                    log.info("shut downing server...");
                }
                log.info("successfully shut down");
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) {
        JsonParser jp = new JsonParser();
        HashMap<String, String> config;
        try {
            String configFile = "config.json";
            if (args.length > 0 && args[0].equals("helios")) configFile = "config_helios.json";
            String text = new FileManager().readResourcesFile(configFile);
            config = jp.jsonToMap(text);
            log.info("successfully loaded config file '{}'", configFile);
        } catch (NullPointerException | IOException | JsonSyntaxException e) {
            log.error("failed to load config file:\n{}", MyExceptions.getStringStackTrace(e));
            return;
        }

        Database db;
        try {
            db = new Database(config.get("host"), config.get("db_name"),
                    config.get("user"), config.get("password"), config.get("db_salt"));
            log.info("successfully connected to a database '{}'", config.get("db_name"));
        } catch (SQLException e) {
            log.error("database connection error:\n{}", MyExceptions.getStringStackTrace(e));
            return;
        }

        MovieCollection mc = new MovieCollection(db);
        CommandManager cm = new CommandManager(mc, db);

        DatagramSocket datagramSocket;
        try {
            datagramSocket = new DatagramSocket(Common.PORT);
        } catch (SocketException e) {
            log.error("problem with DatagramSocket on port {}:\n{}", Common.PORT, MyExceptions.getStringStackTrace(e));
            return;
        }

        Server server = new Server(datagramSocket, cm);

        Thread receiver = new Thread(() -> {
            while (true) {
                try {
                    server.readRequest();
                } catch (IOException ignored) {}
            }
        });
        receiver.start(); // start receiving and processing requests

        Thread processor = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                server.process();
            }
        });
        processor.start();

        Thread sender = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                server.sendToClient();
            }
        });
        sender.start();

        Scanner sc = new Scanner(System.in);
        server.startShell(sc, processor, sender); // start reading directly server commands
    }

}

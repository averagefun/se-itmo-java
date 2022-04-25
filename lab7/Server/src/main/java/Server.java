import java.io.*;

import collection.MovieCollection;
import com.google.gson.JsonSyntaxException;
import commands.CommandManager;
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
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
    private final DatagramSocket datagramSocket;
    private final CommandManager cm;
    private final static Logger log = LoggerFactory.getLogger(Server.class);

    public Server(DatagramSocket datagramSocket, CommandManager cm) {
        this.datagramSocket = datagramSocket;
        this.cm = cm;
    }

    private void receiveAndAnswer() throws IOException, ClassNotFoundException, InterruptedException {
            byte[] buffer = new byte[10000];
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(datagramPacket);

            InetAddress inetAddress = datagramPacket.getAddress();
            int port = datagramPacket.getPort();

            log.info("received data from {}:{}", inetAddress, port);

            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = new ObjectInputStream(bais);

            Object receive = ois.readObject();
            Object objToSend = cm.runCommand((CommandPacket) receive);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(objToSend);

            buffer = baos.toByteArray();

            datagramPacket = new DatagramPacket(buffer, buffer.length, inetAddress, port);
            datagramSocket.send(datagramPacket);

            log.info("send {} bytes of data to {}:{}", buffer.length, inetAddress, port);
    }
    
    public void start() {
        log.info("RECEIVER -> STARTED AT PORT {}", Common.PORT);
        while (true) {
            try {
                receiveAndAnswer();
                if (Thread.currentThread().isInterrupted()) {
                    log.info("RECEIVER -> SHUT DOWN");
                    break;
                }
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                log.error("receive-answer method:\n{}", MyExceptions.getStringStackTrace(e));
                break;
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
        } catch (IOException | JsonSyntaxException e) {
            log.error("failed to load config file:\n{}", MyExceptions.getStringStackTrace(e));
            return;
        }

        Thread receiver = new Thread(() -> {
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
            server.start();
        });

        Scanner sc = new Scanner(System.in);
        interactiveMode(sc, receiver);
    }

    public static void interactiveMode(Scanner sc, Thread receiver) {
        log.info("successfully started server shell: available commands: 'start', 'stop'.\nStarting receiver by default...");
        receiver.start();
        while(true) {
            String input = "";
            try {
                input = sc.nextLine().trim();
            } catch (NoSuchElementException e) {
                System.exit(0);
            }

            switch (input) {
                case "start":
                    if (receiver.isAlive()) {
                        log.warn("already started");
                        continue;
                    }
                    receiver.start();
                    break;
                case "stop":
                    if (!receiver.isAlive()) {
                        log.warn("server already shut down");
                        continue;
                    }
                    receiver.interrupt();
                    break;
            }
        }
    }

}

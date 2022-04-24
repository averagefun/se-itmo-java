import java.io.*;
import java.lang.Math;

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
            byte[] sizeArr = new byte[10];
            DatagramPacket datagramPacket = new DatagramPacket(sizeArr, sizeArr.length);
            datagramSocket.receive(datagramPacket);
            int size = 0;
            for (int i = 0; i < sizeArr.length; i++) {
                size += sizeArr[i] * Math.pow(10, i);
            }

            byte[] buffer = new byte[size];
            datagramPacket = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(datagramPacket);

            InetAddress inetAddress = datagramPacket.getAddress();
            int port = datagramPacket.getPort();

            log.info("received {} bytes of data from {}:{}", size, inetAddress, port);

            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = new ObjectInputStream(bais);

            Object receive = ois.readObject();
            Object objToSend = cm.runCommand((CommandPacket) receive);

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

            datagramPacket = new DatagramPacket(sizeArr, sizeArr.length, inetAddress, port);
            datagramSocket.send(datagramPacket);
            datagramPacket = new DatagramPacket(buffer, buffer.length, inetAddress, port);
            datagramSocket.send(datagramPacket);

            log.info("send {} bytes of data to {}:{}", buffer.length, inetAddress, port);
    }
    
    public void start() {
        log.info("successfully started at port {}", Common.PORT);
        while (true) {
            try {
                receiveAndAnswer();
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
        

    public static void main(String[] args) throws SocketException, SQLException {
        JsonParser jp = new JsonParser();
        HashMap<String, String> config = new HashMap<>();
        try {
            String text = FileManager.readFile("config.json");
            config = jp.jsonToMap(text);
        } catch (IOException | JsonSyntaxException e) {
            log.error("failed to load config file:\n{}", MyExceptions.getStringStackTrace(e));
            System.exit(0);
        }
        Database db = new Database(config.get("user"), config.get("password"));

        MovieCollection mc = new MovieCollection(db);
        CommandManager cm = new CommandManager(mc);

        DatagramSocket datagramSocket = new DatagramSocket(Common.PORT);

        Server server = new Server(datagramSocket, cm);
        server.start();
    }

}

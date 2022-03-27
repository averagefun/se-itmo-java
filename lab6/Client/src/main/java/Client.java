import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Client {
    private DatagramSocket datagramSocket;
    private InetAddress inetAddress;
    private byte[] buffer;

    public Client(DatagramSocket datagramSocket, InetAddress inetAddress) {
        this.datagramSocket = datagramSocket;
        this.inetAddress = inetAddress;
    }

    public void sendThenReceive(String messageToSend) {
        try {
            buffer = messageToSend.getBytes();
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, inetAddress, 8000);
            datagramSocket.send(datagramPacket);
            datagramSocket.receive(datagramPacket);
            String messageFromServer = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
            System.out.println("Answer: " + messageFromServer);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}

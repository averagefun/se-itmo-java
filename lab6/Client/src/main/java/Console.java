import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Class represent console, that produce interactive input/output with user
 */
public class Console {
    public final Scanner sc;
    private final Client client;

    public Console(Scanner sc, Client client) {
        this.sc = sc;
        this.client = client;
    }

    /**
     * Cycle, that listen user input before exit from program
     */
    public void interactiveMode() {
        //noinspection InfiniteLoopStatement
        while (true) {
            Console.print("$ ");
            client.sendThenReceive(sc.nextLine());
        }
    }

    public static void print(Object printable){
        System.out.print(printable);
    }

    public static void printWithSpace(Object printable){
        System.out.print(printable + " ");
    }

    public static void println(){
        System.out.println();
    }

    public static void println(Object printable){
        System.out.println(printable);
    }

    public static void println(Object printable, boolean printMode){
        if (printMode) println(printable);
    }


    public static void main(String[] args) throws SocketException, UnknownHostException {
        DatagramSocket datagramSocket = new DatagramSocket();
        InetAddress inetAddress = InetAddress.getByName("localhost");
        Client client = new Client(datagramSocket, inetAddress);
        Scanner sc = new Scanner(System.in);
        Console c = new Console(sc, client);
        c.interactiveMode();
    }


}

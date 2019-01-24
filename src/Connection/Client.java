package Connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Connection.Client class for a simple client-server application
 *
 * @author Theo Ruys
 * @version 2005.02.21
 */
public class Client {
    public static final CommandReader.State state = CommandReader.State.CLIENT;
    static Thread streamInputHandler;
    static Thread terminalInputHandler;

    /**
     * Starts a Connection.Client application.
     */
    public Client(String ip) {
        connect(ip);
    }

    public static void connect(String ip) {
        String name = "name";
        InetAddress addr = null;
        int port = 4000;
        Socket sock = null;

        // check args[1] - the IP-adress
        try {
            addr = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {

            System.out.println("ERROR: host " + ip + " unknown");
            System.exit(0);
        }

        // try to open a Socket to the server
        try {
            sock = new Socket(addr, port);
        } catch (IOException e) {
            System.out.println("ERROR: could not create a socket on " + addr
                + " and port " + port);
        }

        // create Connection.Peer object and start the two-way communication
        Peer client = new Peer(sock, state);
    }


} // end of class Connection.Client

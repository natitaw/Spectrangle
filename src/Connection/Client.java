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
public class Client implements ClientOrServer{
    public static final CommandReader.State state = CommandReader.State.CLIENT;
    static Thread streamInputHandler;
    static Thread terminalInputHandler;
    private Peer clientPeer;

    /**
     * Starts a Connection.Client application.
     */
    public Client(String ip) {
        connect(ip);
    }

    public void connect(String ip) {
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
        clientPeer = new Peer(sock, state);
        System.out.println("Connected to server");
    }

    // TODO fix null pointer passing
    public Peer getPeer(){
        return clientPeer;
    }

    public void

} // end of class Connection.Client

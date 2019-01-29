package connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * connection.Client class for a simple client-server application
 *
 * @author Theo Ruys
 * @version 2005.02.21
 */
public class Client implements ClientOrServer {
    public static final Type type = ClientOrServer.Type.CLIENT;
    static Thread streamInputHandler;
    private Thread terminalInputHandlerThread;
    private Peer clientPeer;
    private volatile boolean running;

    /**
     * Starts a connection.Client application.
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

        this.running = true;

        // create connection.Peer object and start the two-way communication
        clientPeer = new Peer(sock, type, this);
        terminalInputHandlerThread = new Thread(new TerminalInputHandler(this));
        terminalInputHandlerThread.start();
        System.out.println("Connected to server");
    }

    // TODO fix null pointer passing
    public Peer getPeer() {
        return clientPeer;
    }

    public void sendMessageToAll(String s) {
        clientPeer.sendMessage(s);
    }

    @Override
    public synchronized boolean getRunning() {
        return this.running;
    }

    public Type getType(){
        return this.type;
    }

    // TODO Implement
    @Override
    public void shutDown() {
        this.running = false;
        clientPeer.setRunning(false);

        System.out.println("Trying to shut down");
        try {
            terminalInputHandlerThread.interrupt(); // TODO this is not a very neat way of doing it
            System.out.println("Closed terminal input handling thread");
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println("Error in closing terminal input thread");
        }
        clientPeer.close();



    }
} // end of class connection.Client

package Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Connection.Server.
 * @author  Theo Ruys
 * @version 2005.02.21
 */
public class Server {
    static Thread streamInputHandler;
    private static final String USAGE
        = "usage: " + Server.class.getName() + " <name> <port>";

    /** Starts a Connection.Server-application. */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(0);
        }

        String name = args[0];
        int port = 0;
       Socket sock = null;
       ServerSocket serverSock = null;

        // parse args[1] - the port
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println(USAGE);
            System.out.println("ERROR: port " + args[2]
                    + " is not an integer");
            System.exit(0);
        }

        // try to open a Socket to the server
        // TODO Connection.Server socket
        try {
            serverSock = new ServerSocket(port);
            sock = serverSock.accept();
            System.out.println("new client connected");

        } catch (IOException e) {
            System.out.println("ERROR: could not create a socket on " + port);
        }

        // create Connection.Peer object and start the two-way communication
        try {
            Peer server = new Peer(name, sock);
            Thread streamInputHandler = new Thread(server);
            streamInputHandler.start();

            while (server.running) {
                server.handleTerminalInput();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                streamInputHandler.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

} // end of class Connection.Server

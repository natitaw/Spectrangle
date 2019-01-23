package Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//TODO accept multiple peers

public class Server {
    static Thread streamInputHandler;

    public static final CommandReader.State state = CommandReader.State.SERVER;

    public Server(){
        startHosting();
    }

    /** Starts a Connection.Server-application. */
    public void startHosting() {

        int port = 4000;
        String name = "name";
       Socket sock = null;
       ServerSocket serverSock = null;

        // try to open a Socket to the server
        try {
            serverSock = new ServerSocket(port);
            sock = serverSock.accept();
            System.out.println("new client connected");

        } catch (IOException e) {
            System.out.println("ERROR: could not create a socket on " + port);
        }

        // create Connection.Peer object and start the two-way communication
        try {
            Peer server = new Peer(sock, state);
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

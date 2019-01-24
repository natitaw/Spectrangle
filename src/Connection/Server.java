package Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
    public static final CommandReader.State state = CommandReader.State.SERVER;
    static Thread streamInputHandler;
    static Thread terminalInputHandler;
    int port;
    String name;
    ServerSocket serverSock;
    List<Peer> peerList;
    private boolean running;

    public boolean getRunning(){
        return this.running;
    }

    public Server() {
        port = 4000;
        name = "name";
        serverSock = null;
        peerList = new ArrayList<>();
        running = true;
        try {
            serverSock = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        terminalInputHandler = new Thread(new TerminalInputHandler(this));
        terminalInputHandler.start();
    }

    /**
     * Starts a Connection.Server-application.
     */
    public void run() {

        // try to open a Socket to the server
        while (running) {
            Socket sock;
            System.out.println("waiting");
            try {

                sock = serverSock.accept();
                System.out.println("new client connected");
                peerList.add(new Peer(sock, state));
                System.out.println("done establishing connection");

            } catch (IOException e) {
                System.out.println("ERROR: could not create a socket on " + port);
            }


        }
    }

    public void sendMessages(String s){
        for (Peer p : peerList) {
            p.sendMessage(s);
        }
    }

    public void shutDown() {
        // TODO actually call this at some point
        running = false;
        try {
            terminalInputHandler.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        for (Peer p : peerList) {
            try {
                p.streamInputHandler.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

} // end of class Connection.Server

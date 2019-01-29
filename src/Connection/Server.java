package Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable,ClientOrServer {
    public static final Type state = ClientOrServer.Type.SERVER;

    // TODO encapsulate
    Thread streamInputHandler;
    Thread terminalInputHandlerThread;
    int port;
    String name;
    ServerSocket serverSock;
    List<Peer> peerList;
    private volatile boolean running;
    Thread newConnectionThread;

    public synchronized boolean getRunning(){
        return this.running;
    }

    public Server() {
        port = 4000;
        name = "name";
        serverSock = null;
        peerList = new ArrayList<>();
        this.running = true;
        try {
            serverSock = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        newConnectionThread = new Thread(this);
        newConnectionThread.start();
        terminalInputHandlerThread = new Thread(new TerminalInputHandler(this));
        terminalInputHandlerThread.start();
    }

    /**
     * Starts a Connection.Server-application.
     */
    public void run() {

        // try to open a Socket to the server
        while (running) {
            Socket sock;
            try {

                sock = serverSock.accept();
                System.out.println("New unknown client connected");
                peerList.add(new Peer(sock, state, this));

            } catch (IOException e) {
                System.out.println("Thread was unable to create socket.");
            }


        }
    }

    // TODO sort
    public void sendMessages(String s){
        for (Peer p : peerList) {
            p.sendMessage(s);
        }
    }

    public void shutDown() {
        this.running = false;
        for (Peer p : peerList) {
            p.setRunning(false);
            p.close();
        }

        newConnectionThread.interrupt();

        try {
            serverSock.close();
            System.out.println("Server socket closed");
        } catch (IOException e) {
            System.out.println("Error in closing server socket. Printing error");
            e.printStackTrace();
        }

        try {
            terminalInputHandlerThread.interrupt();
            System.out.println("Terminal input handling thread closed");
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println("Error in closing terminal input thread");
        }

    }

} // end of class Connection.Server

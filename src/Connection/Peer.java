package Connection;

import java.io.*;
import java.net.Socket;

//TODO fix exception on shutdown

public class Peer implements Runnable {
    public static final String EXIT = "EXIT";

    private Socket sock;
    private BufferedReader in;
    private PrintWriter out;
    private volatile boolean running;
    private CommandReader reader;
    private Thread streamInputHandler;
    private String name;
    private boolean chatEnabled;
    private int currentRoom;
    private ClientOrServer parent;

    public Thread getStreamInputHandler() {
        return streamInputHandler;
    }

    /**
     * Constructor. creates a peer object based in the given parameters.
     * @param   sockArg Socket of the Connection.Peer-proces
     */
    public Peer(Socket sockArg, ClientOrServer.Type type, ClientOrServer parentInput)
    {
        try {
            parent=parentInput;
            sock = sockArg;
            currentRoom=0; // set current "room" to lobby = all communication gets sent
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new PrintWriter(sock.getOutputStream(), true);
            this.running = true;
            reader = new CommandReader(type);



            streamInputHandler = new Thread(this);
            streamInputHandler.start();

            if (type== ClientOrServer.Type.CLIENT) {
                name="Server";
                chatEnabled=true; // Client leaves all communication with its "Peer", the server, enabled by default
            } else {
                name="Unknown client";
                chatEnabled=false;
            }

        } catch (IOException e) {
            System.out.println("Error in starting peer");
            e.printStackTrace();
        }
    }

    public boolean getChatEnabled(){
        return this.chatEnabled;
    }

    public void setChatEnabled(boolean b){
        this.chatEnabled=b;
    }

    public int getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(int currentRoom) {
        this.currentRoom = currentRoom;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * Reads strings of the stream of the socket-connection and
     * writes the characters to the default output.
     */



    public void run() {
        while (running && parent.getRunning()) {
        try {
            String s1 = in.readLine();
            if (s1 == null || s1 == "") {
                close();
            } else {
                reader.read(s1, this);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
    }
    } }

    public String getName(){
        return this.name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    /**
     * Reads a string from the console and sends this string over
     * the socket-connection to the Connection.Peer process.
     * On Connection.Peer.EXIT the method ends
     */


    public void sendMessage(String s){
        out.println(s);
    }

    /**
     * Closes the connection, the sockets will be terminated
     */
    public void close() {
        this.running=false;

        streamInputHandler.interrupt(); // todo ERROR HAPPENS HERE
        System.out.println("Connection reading thread for " + name + " closed");
        try {
            sock.close();
            System.out.println("Socket for connection " + name + " closed");
        } catch (IOException e) {
            System.out.println("Error in closing socket for " + name);

        }


    }

    /** read a line from the default input */


    }


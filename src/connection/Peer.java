package connection;

import connection.server.Room;

import java.io.*;
import java.net.Socket;

//TODO fix exception on shutdown

public class Peer implements Runnable {
    public static final String EXIT = "EXIT";

    private Socket sock;
    private BufferedReader in;
    private PrintWriter out;
    private volatile boolean running;
    private CommandInterpreter reader;
    private Thread streamInputHandler;
    private String name;
    private boolean chatEnabled;
    private ClientOrServer parent;
    private int preferredNrOfPlayers;
    private Room currentRoom;

    public Thread getStreamInputHandler() {
        return streamInputHandler;
    }

    /**
     * Constructor. creates a peer object based in the given parameters.
     * @param   sockArg Socket of the connection.Peer-proces
     */
    public Peer(Socket sockArg, ClientOrServer.Type type, ClientOrServer parentInput)
    {
        try {
            parent=parentInput;
            sock = sockArg;
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new PrintWriter(sock.getOutputStream(), true);
            this.running = true;
            reader = new CommandInterpreter(parent);



            streamInputHandler = new Thread(this);
            streamInputHandler.start();

            if (type== ClientOrServer.Type.CLIENT) {
                name="Server";
                chatEnabled=true; // Client leaves all communication with its "Peer", the server, enabled by default
            } else {
                name="Unknown client";
                chatEnabled=false;
                preferredNrOfPlayers =0;
            }

        } catch (IOException e) {
            System.out.println("Error in starting peer");
            e.printStackTrace();
        }
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public boolean getChatEnabled(){
        return this.chatEnabled;
    }

    public void setChatEnabled(boolean b){
        this.chatEnabled=b;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getPreferredNrOfPlayers() {
        return preferredNrOfPlayers;
    }

    public void setPreferredNrOfPlayers(int preferredNrOfPlayers) {
        this.preferredNrOfPlayers = preferredNrOfPlayers;
    }

    /**
     * Reads strings of the stream of the socket-connection and
     * writes the characters to the default output.
     */



    public void run() {
        while (running && parent.getRunning()) {
        try {
            String s1 = in.readLine();
            if (s1 == null || s1.equals("")) {
                close();
            } else {
                reader.read(s1, this);
            }

        } catch (IOException e) {
            if (running && parent.getRunning()){
                System.out.println("Error in reading data from " + name);
                e.printStackTrace();
            }

    }
    } }

    public String getName(){
        return this.name;
    }

    public void setName(String newName) {
        this.name = newName;
    }


    public void sendMessage(String s){
        out.println(s);
    }

    public void moveToRoom(Room room) {
        if (this.currentRoom != null) {
            currentRoom.removePeer(this);
        }
        room.addPeer(this);
        currentRoom=room;
    }


    /**
     * Closes the connection, the sockets will be terminated
     */
    public void close() {
        this.running=false;

        streamInputHandler.interrupt();
        System.out.println("Connection reading thread for " + name + " closed");
        try {
            sock.close();
            System.out.println("Socket for connection " + name + " closed");
        } catch (IOException e) {
            System.out.println("Error in closing socket for " + name);

        }
        // TODO if server, remove peer from lists, rooms and such. Close any game rooms it is in

    }



    }


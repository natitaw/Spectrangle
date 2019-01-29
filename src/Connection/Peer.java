package Connection;

import java.io.*;
import java.net.Socket;

//TODO fix exception on shutdown

public class Peer implements Runnable {
    public static final String EXIT = "exit";

    protected Socket sock;
    protected BufferedReader in;
    protected PrintWriter out;
    public boolean running;
    private CommandReader reader;
    static Thread streamInputHandler;
    static Thread terminalInputHandler;
    private String name;
    private boolean chatEnabled;


    /**
     * Constructor. creates a peer object based in the given parameters.
     * @param   sockArg Socket of the Connection.Peer-proces
     */
    public Peer(Socket sockArg, CommandReader.Type state)
    {
        try {
        sock = sockArg;

        in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        out = new PrintWriter(sock.getOutputStream(), true);
        running = true;
        reader = new CommandReader(state);


            PeerReader peerReader = new PeerReader(this);
            streamInputHandler = new Thread(this);
            streamInputHandler.start();

            if (state== CommandReader.Type.CLIENT) {
                //terminalInputHandler = new Thread(peerReader);
                //terminalInputHandler.start();
                name="Server";
                chatEnabled=true; // Client leaves all communication with its "Peer", the server, enabled by default
            } else {
                name="Unknown client";
                chatEnabled=false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean getChatEnabled(){
        return this.chatEnabled;
    }

    public void setChatEnabled(boolean b){
        this.chatEnabled=b;
    }

    /**
     * Reads strings of the stream of the socket-connection and
     * writes the characters to the default output.
     */
    public void run() {
        while (running) {
        try {
            String s1 = in.readLine();
            if (s1 == null || s1 == "") {
                shutDown();
            } else {
                reader.read(s1, this);
            }

        } catch (IOException e) {
            e.printStackTrace();
            running=false;
            shutDown();
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
    public void handleTerminalInput() {
            String s = readString("");
            if (s.equals("EXIT")){
                shutDown();
            } else {
                sendMessage(s);
            }

    }

    public void sendMessage(String s){
        out.println(s);
    }

    /**
     * Closes the connection, the sockets will be terminated
     */
    public void shutDown() {
        try {
            System.out.println("Shutting down");
            running=false;
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
            running=false;

        }
    }

    /** read a line from the default input */
    static public String readString(String tekst) {
        System.out.print(tekst);
        String antw = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    System.in));
            antw = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (antw == null) ? "" : antw;
    }

    }


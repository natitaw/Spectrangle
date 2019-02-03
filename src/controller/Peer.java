package controller;

import controller.server.Room;
import model.TileBag;
import view.CommandInterpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Peer class. Is runnable so that a thread can run on it for networked communication in parallel
 * to the rest of the program.
 * Represents a connection to another computer.
 *
 * @author Bit 4 - Group 4
 */
public class Peer implements Runnable, Comparable<Peer> {
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
    private TileBag tileBag;
    private int score;

    /**
     * Constructor. Creates a Peer object based in the given parameters
     * And starts a thread on itself for reading lines from the socket
     *
     * @param sockArg     Socket to be used for communication with this peer
     * @param type        Whether the parent is a client or server
     * @param parentInput The parent to be used for this peer
     */
    public Peer(Socket sockArg, ClientOrServer.Type type, ClientOrServer parentInput) {
        try {
            parent = parentInput;

            sock = sockArg;
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new PrintWriter(sock.getOutputStream(), true);
            this.running = true;
            reader = new CommandInterpreter(parent);


            streamInputHandler = new Thread(this);
            streamInputHandler.start();

            if (type == ClientOrServer.Type.CLIENT) {
                name = "Server";
                chatEnabled = true; // Client leaves all communication with  "Peer" (Server) enabled
            } else {
                name = "Unknown client";
                chatEnabled = false;
                preferredNrOfPlayers = 0;
            }

        } catch (IOException e) {
            parent.getPrinter().println("Error in starting peer");
            e.printStackTrace();
        }
    }

    /**
     * Gets running
     *
     * @return value of running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Sets running to running
     *
     * @param running new value of running
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * Gets name
     *
     * @return value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name to name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets chatEnabled
     *
     * @return value of chatEnabled
     */
    public boolean isChatEnabled() {
        return chatEnabled;
    }

    /**
     * Sets chatEnabled to chatEnabled
     *
     * @param chatEnabled new value of chatEnabled
     */
    public void setChatEnabled(boolean chatEnabled) {
        this.chatEnabled = chatEnabled;
    }

    /**
     * Gets preferredNrOfPlayers
     *
     * @return value of preferredNrOfPlayers
     */
    public int getPreferredNrOfPlayers() {
        return preferredNrOfPlayers;
    }

    /**
     * Sets preferredNrOfPlayers to preferredNrOfPlayers
     *
     * @param preferredNrOfPlayers new value of preferredNrOfPlayers
     */
    public void setPreferredNrOfPlayers(int preferredNrOfPlayers) {
        this.preferredNrOfPlayers = preferredNrOfPlayers;
    }

    /**
     * Gets currentRoom
     *
     * @return value of currentRoom
     */
    public Room getCurrentRoom() {
        return currentRoom;
    }

    /**
     * Gets tileBag
     *
     * @return value of tileBag
     */
    public TileBag getTileBag() {
        return tileBag;
    }

    /**
     * Sets tileBag to tileBag
     *
     * @param tileBag new value of tileBag
     */
    public void setTileBag(TileBag tileBag) {
        this.tileBag = tileBag;
    }

    /**
     * Gets score
     *
     * @return value of score
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets score to score
     *
     * @param score new value of score
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Increment score by i
     *
     * @param i Value to be added to score
     */
    public void incScore(int i) {
        this.score += i;
    }

    /**
     * If the peer and its parent aren't shutting down, tries to read the next line
     * from the socket's BufferedReader. If it reads empty data, it shuts down the peer.
     * Else it sends it to the peer's CommandInterpreter instance.
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
                if (running && parent.getRunning()) {
                    parent.getPrinter().println("Error in reading data from " + name);
                    e.printStackTrace();
                }

            }
        }
    }


    /**
     * Sends a message over network to the peer.
     *
     * @param s String to be sent
     */
    public void sendMessage(String s) {
        out.println(s);
    }

    /**
     * If the peer is in a room, removes it from this room. Then adds it to the new room, and
     * sets the peer's currentRoom field to this new room.
     *
     * @param room The room the peer should be moved to.
     */
    public void moveToRoom(Room room) {
        if (this.currentRoom != null) {
            currentRoom.removePeer(this);
        }
        room.addPeer(this);
        currentRoom = room;
    }

    /**
     * Override of Comparable.compareTo for sorting reasons. GameRoom sorts the peers by name.
     *
     * @param p Peer to be compared to
     * @return returns result of compareTo of name Strings.
     */
    @Override
    public int compareTo(Peer p) {
        return this.name.compareTo(p.getName());
    }

    /**
     * Ends the thread running on the peer, and closes the socket.
     */
    public void close() {
        this.running = false;
        try {
            sock.close();
            parent.getPrinter().println("Socket for controller " + name + " closed");
        } catch (IOException e) {
            parent.getPrinter().println("Error in closing socket for " + name);

        }
        parent.getPrinter().println("Connection reading thread for " + name + " closed");

        streamInputHandler.interrupt();

    }

}





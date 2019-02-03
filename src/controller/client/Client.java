package controller.client;

import controller.ClientOrServer;
import controller.Peer;
import model.Board;
import view.TerminalInputHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Client class. Used for connecting to a server
 *
 * @author Bit 4 - Group 4
 */
public class Client implements ClientOrServer {
    private  final Type type = ClientOrServer.Type.CLIENT;
    private List<String> clientTiles;
    private List<List<String>> otherTileList; // intend to use this for complicated AI
    private final boolean isSilent;
    private final TerminalInputHandler terminalInputHandler;
    private final boolean isAI;
    private Thread terminalInputHandlerThread;
    private Peer clientPeer;
    private volatile boolean running;
    private String name;
    private Board board;
    private int prefNrPlayers;
    private double difficulty;
    private PrintStream printer;

    /**
     * Constructor for client class.
     * If argument 0 is singleplayer, starts TerminalInputHandler in SINGLEPLAYER state.
     * Elseif argument is AI starts in AI state. Checks other args for difficulty and whether
     * print statements should be silenced
     * Else (normal) starts it normally
     * Then executes connect method
     *
     * @param ip ip address to connect to in string format
     * @param arg Arguments for construction in string format
     */
    public Client(String ip, String arg) {
        String[] argArray = arg.split(Pattern.quote(" "));
        if (argArray[0].equals("singleplayer")) {
            this.prefNrPlayers = Integer.parseInt(argArray[2]);
            this.difficulty = Double.parseDouble(argArray[3]);
            terminalInputHandler = new TerminalInputHandler(this, TerminalInputHandler.InputState.SINGLEPLAYER);
            name = argArray[1];
            isAI = false;
            isSilent = false;
        } else if (arg.equals("")) {
            terminalInputHandler = new TerminalInputHandler(this);
            name = "default";
            isAI = false;
            isSilent = false;
        } else {
            terminalInputHandler = new TerminalInputHandler(this, TerminalInputHandler.InputState.AI_NAME);
            this.name = argArray[1];
            this.prefNrPlayers = Integer.parseInt(argArray[2]);
            isAI = true;
            this.difficulty = Double.parseDouble(argArray[3]);
            this.isSilent = Boolean.parseBoolean(argArray[4]);

        }

        connect(ip);

    }

    /**
     * Gets type
     *
     * @return value of type
     */
    public Type getType() {
        return type;
    }

    /**
     * Gets clientTiles
     *
     * @return value of clientTiles
     */
    public List<String> getClientTiles() {
        return clientTiles;
    }

    /**
     * Sets clientTiles to clientTiles
     *
     * @param clientTiles new value of clientTiles
     */
    public void setClientTiles(List<String> clientTiles) {
        this.clientTiles = clientTiles;
    }

    /**
     * Gets otherTileList
     *
     * @return value of otherTileList
     */
    public List<List<String>> getOtherTileList() {
        return otherTileList;
    }

    /**
     * Sets otherTileList to otherTileList
     *
     * @param otherTileList new value of otherTileList
     */
    public void setOtherTileList(List<List<String>> otherTileList) {
        this.otherTileList = otherTileList;
    }

    /**
     * Gets terminalInputHandler
     *
     * @return value of terminalInputHandler
     */
    public TerminalInputHandler getTerminalInputHandler() {
        return terminalInputHandler;
    }

    /**
     * Gets isAI
     *
     * @return value of isAI
     */
    public boolean isAI() {
        return isAI;
    }

    /**
     * Gets name
     *
     * @return value of name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets name to name
     *
     * @param name new value of name
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets board
     *
     * @return value of board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Sets board to board
     *
     * @param board new value of board
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Gets prefNrPlayers
     *
     * @return value of prefNrPlayers
     */
    public int getPrefNrPlayers() {
        return prefNrPlayers;
    }

    /**
     * Gets difficulty
     *
     * @return value of difficulty
     */
    public double getDifficulty() {
        return difficulty;
    }

    /**
     * Gets printer
     *
     * @return value of printer
     */
    @Override
    public PrintStream getPrinter() {
        return printer;
    }

    /**
     * Connect method.
     * Creates a new PrintStream that is either silent or just a regular System.out depending
     * on constructor args.
     * Tries to connect to the IP and open a socket.
     * Starts a new thread on the terminalInputHandler.
     * @param ip ip address to connect to in sring format
     */
    private void connect(String ip) {
        InetAddress addr = null;
        int port = 4000;
        Socket sock = null;


        if (isSilent) {
            printer = new PrintStream(new OutputStream() {
                public void write(int b) {
                    // does nothing
                }
            });

        } else {
            printer = System.out;


        }

        // check the IP-adress
        try {
            addr = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {

            printer.println("ERROR: host " + ip + " unknown");
            System.exit(0);
        }


        // try to open a Socket to the server
        try {
            sock = new Socket(addr, port);
        } catch (IOException e) {
            printer.println("ERROR: could not create a socket on " + addr
                + " and port " + port);
        }

        this.running = true;

        // create controller.Peer object and start the two-way communication
        clientPeer = new Peer(sock, type, this);
        terminalInputHandlerThread = new Thread(terminalInputHandler);
        terminalInputHandlerThread.start();
        printer.println("Connected to server");
    }


    /**
     * Sends message to all peers (in this case, the only peer: server)
     * @param s message to send
     */
    public void sendMessageToAll(String s) {
        clientPeer.sendMessage(s);
    }

    @Override
    public boolean getRunning() {
        return running;
    }

    /**
     * Shuts down server by setting running fields to false.
     * Closes terminalInputHandler thread. Then closes peer object.
     */
    @Override
    public void shutDown() {
        this.running = false;
        clientPeer.setRunning(false);

        printer.println("Trying to shut down");
        try {
            terminalInputHandler.setInterrupted(true);
            terminalInputHandlerThread.join();
            printer.println("Closed terminal input handling thread");
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            printer.println("Error in closing terminal input thread");
        }
        clientPeer.close();


    }
} // end of class controller.client.Client

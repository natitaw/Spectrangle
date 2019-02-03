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
    private static final Type type = ClientOrServer.Type.CLIENT;
    private static List<String> clientTiles;
    private static List<List<String>> otherTileList; // intend to use this for complicated AI
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
     * @param ip
     * @param arg
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
     * @return
     */
    public int getPrefNrPlayers() {
        return prefNrPlayers;
    }

    /**
     * @return
     */
    public boolean isAI() {
        return isAI;
    }

    /**
     * @return
     */
    public double getDifficulty() {
        return difficulty;
    }

    /**
     * @return
     */
    public TerminalInputHandler getTerminalInputHandler() {
        return terminalInputHandler;
    }

    /**
     * @return
     */
    public List<String> getClientTiles() {
        return clientTiles;
    }

    /**
     * @param clientTiles
     */
    public void setClientTiles(List<String> clientTiles) {
        this.clientTiles = clientTiles;
    }

    /**
     * @return
     */
    public List<List<String>> getOtherTileList() {
        return otherTileList;
    }

    /**
     * @param otherTileList
     */
    public void setOtherTileList(List<List<String>> otherTileList) {
        this.otherTileList = otherTileList;
    }

    /**
     *
     * @return
     */
    public Board getBoard() {
        return board;
    }

    /**
     *
     * @param board
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     *
     * @return
     */
    @Override
    public PrintStream getPrinter() {
        return printer;
    }

    /**
     *
     * @param s
     */
    public void sendMessageToAll(String s) {
        clientPeer.sendMessage(s);
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    @Override
    public synchronized boolean getRunning() {
        return this.running;
    }

    /**
     *
     * @return
     */
    public Type getType() {
        return type;
    }

    /**
     *
     * @param ip
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


    @Override
    public void shutDown() {
        this.running = false;
        clientPeer.setRunning(false);

        printer.println("Trying to shut down");
        try {
            terminalInputHandlerThread.interrupt();
            printer.println("Closed terminal input handling thread");
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            printer.println("Error in closing terminal input thread");
        }
        clientPeer.close();


    }
} // end of class controller.client.Client

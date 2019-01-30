package connection.client;

import connection.ClientOrServer;
import connection.Peer;
import connection.TerminalInputHandler;
import game.Board;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * connection.client.Client class for a simple client-server application
 *
 * @author Theo Ruys
 * @version 2005.02.21
 */
public class Client implements ClientOrServer {
    private static final Type type = ClientOrServer.Type.CLIENT;


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
     * Starts a connection.client.Client application.
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
        ClientCommands.setClientObject(this);

    }

    public int getPrefNrPlayers() {
        return prefNrPlayers;
    }

    public boolean isAI() {
        return isAI;
    }

    public double getDifficulty() {
        return difficulty;
    }

    public TerminalInputHandler getTerminalInputHandler() {
        return terminalInputHandler;
    }

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

        // create connection.Peer object and start the two-way communication
        clientPeer = new Peer(sock, type, this);
        terminalInputHandlerThread = new Thread(terminalInputHandler);
        terminalInputHandlerThread.start();
        printer.println("Connected to server");
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    @Override
    public PrintStream getPrinter() {
        return printer;
    }

    public void sendMessageToAll(String s) {
        clientPeer.sendMessage(s);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public synchronized boolean getRunning() {
        return this.running;
    }

    public Type getType() {
        return type;
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
} // end of class connection.client.Client

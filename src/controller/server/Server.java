package controller.server;

import controller.ClientOrServer;
import controller.Peer;
import view.TerminalInputHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Server class. Is runnable, so that a thread can constantly execute server functions next to other
 * functionality. This handles all serverside handling of incoming clients
 *
 * @author Bit 4 - Group 4
 */
public class Server implements Runnable, ClientOrServer {
    private static final Type type = ClientOrServer.Type.SERVER;
    private final int port;
    private final List<Peer> peerList;
    private final Thread newConnectionThread;
    private final Room lobby;
    private final List<Room> roomList;
    private final PrintStream printer;
    private TerminalInputHandler terminalInputHandler;
    private Thread terminalInputHandlerThread;
    private String name;
    private ServerSocket serverSock;
    private volatile boolean running;

    /**
     * Server constructor. Starts a ServerSocket on port 4000. Starts a thread on itself (See run
     * method). Starts a thread on TerminalInputHandler. Starts a new Room object with index 0,
     * which is the "lobby". Checks the arguments. If the server was started as part of a
     * singleplayer game, its print statements are silenced.
     *
     * @author Bit 4 - Group 4
     * @see TerminalInputHandler
     */
    public Server(String arg) {
        port = 4000;
        name = "server";
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
        if (!arg.equals("singleplayer")) {
            terminalInputHandler = new TerminalInputHandler(this);
            terminalInputHandlerThread = new Thread(terminalInputHandler);
            terminalInputHandlerThread.start();
        }

        roomList = new ArrayList<>();

        lobby = newRoom();

        if (arg.equals("singleplayer")) {
            printer = new PrintStream(new OutputStream() {
                public void write(int b) {
                    // does nothing
                }
            });

        } else {
            printer = System.out;

        }

        printer.println("Server successfully started on port " + port);
    }

    /**
     * Getter for the running field.
     *
     * @return boolean which specifies if this Server should be running. Used in shutdown methods.
     * @author Bit 4 - Group 4
     */
    public synchronized boolean getRunning() {
        return this.running;
    }

    /**
     * Getter for the peerList field.
     *
     * @return a list of peers connected to the server.
     * @author Bit 4 - Group 4
     */
    public List<Peer> getPeerList() {
        return peerList;
    }

    /**
     * Getter for the printer field.
     *
     * @return the printer for this server. Is either System.out or a silent printer.
     * @author Bit 4 - Group 4
     */
    @Override
    public PrintStream getPrinter() {
        return printer;
    }

    /**
     * Getter for the roomList field.
     *
     * @return a list of all rooms started by this server.
     * @author Bit 4 - Group 4
     */
    public List<Room> getRoomList() {
        return roomList;
    }

    /**
     * Getter for the type field.
     *
     * @return an enum that specifies whether this is a Client or Server.
     * @author Bit 4 - Group 4
     * @see ClientOrServer
     */
    public Type getType() {
        return type;
    }

    /**
     * Getter for the name field. Not really needed as it will always be "server".
     * But used for consistency with the ClientOrServer interface.
     *
     * @return the server's name.
     * @author Bit 4 - Group 4
     * @see ClientOrServer
     */
    @Override
    public String getName() {
        return this.name;
    }


    /**
     * Setter for the name field. Not really needed as it will always be "server".
     * But used for consistency with the ClientOrServer interface.
     *
     * @param s The server's new name.
     * @author Bit 4 - Group 4
     * @see ClientOrServer
     */
    @Override
    public void setName(String s) {
        this.name = s;
    }

    /**
     * Method used for creating a new instance of Room, to put peers in.
     * Its index will be equal to the current size of roomList (thus, one higher than the highest)
     * Then adds it to the list of rooms.
     * @return the room that was created.
     * @author Bit 4 - Group 4
     */
    private Room newRoom() {
        Room tempRoom = new Room(roomList.size());
        roomList.add(tempRoom);
        return tempRoom;
    }

    /**
     * Method used for creating a new instance of GameRoom, to put peers in and play a game.
     * Its index will be equal to the current size of roomList (thus, one higher than the highest)
     * Then adds it to the list of rooms.
     * @return the GameRoom that was created.
     * @author Bit 4 - Group 4
     */
    public GameRoom newGameRoom() {
        GameRoom tempGameRoom = new GameRoom(roomList.size(), this);
        roomList.add(tempGameRoom);
        return tempGameRoom;
    }

    /**
     * Runs a while loop until the Server is to be shut down (by checking the "running" field).
     * Loop does the following:
     * Tries to create a new socket by accepting a connection in the server socket.
     * Creates a new peer instance for this connection. Moves the peer to lobby.
     */
    public void run() {

        // try to open a Socket to the server
        while (running) {
            Socket sock;
            Peer newPeer;
            try {

                sock = serverSock.accept();

                newPeer = new Peer(sock, type, this);
                peerList.add(newPeer);

                newPeer.setName("Unknown client " + peerList.size());
                newPeer.moveToRoom(lobby);
                printer.println(newPeer.getName() + " connected");


            } catch (IOException e) {
                if (running) {
                    printer.println("Thread was unable to create socket on port " + port);
                }
            }


        }
    }

    /**
     * Sends a message to all peers in peerList
     * @param s The message to send
     */
    public void sendMessageToAll(String s) {
        for (Peer p : peerList) {
            p.sendMessage(s);
        }
    }

    /**
     * Sends a message to all peers in a room
     *
     * @param command The command to be sent
     * @param room The room of which the players must receive the commands
     * @param commandType Type of command (because not all players want to receive chat)
     */
    void sendMessageToRoom(String command, Room room, String commandType) {
        for (Peer p : room.getPeerList()) {

            if (commandType.equals("chat")) {
                if (p.getChatEnabled()) {
                    p.sendMessage(command);
                }
            } else {
                p.sendMessage(command);
            }


        }
    }

    /**
     * Shuts down server by settings the running field of this server and all peers to false.
     * Cloes connections to all peers.
     * Closes the server socket.
     * Closes the terminal input handler.
     *
     */
    public void shutDown() {
        this.running = false;

        for (Peer p : peerList) {
            p.setRunning(false);
            p.close();
        }

        newConnectionThread.interrupt();

        try {
            serverSock.close();
            printer.println("Server socket closed");
        } catch (IOException e) {
            printer.println("Error in closing server socket. Printing error");
            e.printStackTrace();
        }

        try {
            terminalInputHandler.setInterrupted(true);
            terminalInputHandlerThread.join();
            printer.println("Terminal input handling thread closed");
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            printer.println("Error in closing terminal input thread");
        }

    }

} // end of class controller.server.Server

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

public class Server implements Runnable, ClientOrServer {
    private static final Type type = ClientOrServer.Type.SERVER;
    private final int port;
    private final List<Peer> peerList;
    private final Thread newConnectionThread;
    private final Room lobby;
    private final List<Room> roomList;
    private final PrintStream printer;
    private Thread terminalInputHandlerThread;
    private String name;
    private ServerSocket serverSock;
    private volatile boolean running;


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
            terminalInputHandlerThread = new Thread(new TerminalInputHandler(this));
            terminalInputHandlerThread.start();
        }
        // zero index of roomlist should always have lobby
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

    public synchronized boolean getRunning() {
        return this.running;
    }

    public List<Peer> getPeerList() {
        return peerList;
    }

    @Override
    public PrintStream getPrinter() {
        return printer;
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    private Room newRoom() {
        Room tempRoom = new Room(roomList.size());
        roomList.add(tempRoom);
        return tempRoom;
    }

    public GameRoom newGameRoom() {
        GameRoom tempGameRoom = new GameRoom(roomList.size(), this);
        roomList.add(tempGameRoom);
        return tempGameRoom;
    }

    /**
     * Starts a controller.server.Server-application.
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


    public void sendMessageToAll(String s) {
        for (Peer p : peerList) {
            p.sendMessage(s);
        }
    }

    public void sendMessageToRoom(String command, Room room, String commandType) {
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

    public Type getType() {
        return type;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String s) {
        this.name = s;
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
            printer.println("Server socket closed");
        } catch (IOException e) {
            printer.println("Error in closing server socket. Printing error");
            e.printStackTrace();
        }

        try {
            terminalInputHandlerThread.interrupt();
            printer.println("Terminal input handling thread closed");
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            printer.println("Error in closing terminal input thread");
        }

    }

} // end of class controller.server.Server

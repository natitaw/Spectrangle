package connection.server;

import connection.ClientOrServer;
import connection.Peer;
import connection.TerminalInputHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable, ClientOrServer {
    public static final Type type = ClientOrServer.Type.SERVER;

    // TODO encapsulate
    Thread streamInputHandler;
    Thread terminalInputHandlerThread;
    int port;
    String name;
    ServerSocket serverSock;
    private List<Peer> peerList;
    private volatile boolean running;
    Thread newConnectionThread;
    public Room lobby;
    private List<Room> roomList;
    private PrintStream printer;


    public synchronized boolean getRunning(){
        return this.running;
    }


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
            printer = new PrintStream(new OutputStream(){
                public void write(int b) {
                    // does nothing
                }
            });

        } else {
            printer = System.out;


        }


        printer.println("Server successfully started on port " + port);
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

    public Room newRoom(){
        Room tempRoom = new Room(roomList.size());
        roomList.add(tempRoom);
        return tempRoom;
    }

    public GameRoom newGameRoom(){
        GameRoom tempGameRoom = new GameRoom(roomList.size(), this);
        roomList.add(tempGameRoom);
        return tempGameRoom;
    }

    /**
     * Starts a connection.server.Server-application.
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


    public void sendMessageToAll(String s){
        for (Peer p : peerList) {
            p.sendMessage(s);
        }
    }

    public void sendMessageToRoom(String command, Room room, String commandType){
        for (Peer p : room.getPeerList()) {

                if (commandType.equals("chat")){
                    if (p.getChatEnabled()){
                        p.sendMessage(command);
                    }
                } else {
                    p.sendMessage(command);
                }


        }
    }

    public Type getType(){
        return this.type;
    }

    @Override
    public void setName(String s) {
        this.name = s;
    }

    @Override
    public String getName() {
        return this.name;
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

} // end of class connection.server.Server

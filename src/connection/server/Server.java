package connection.server;

import connection.ClientOrServer;
import connection.Peer;
import connection.TerminalInputHandler;

import java.io.IOException;
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

    public synchronized boolean getRunning(){
        return this.running;
    }


    public Server() {
        port = 4000;
        name = "name";
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
        terminalInputHandlerThread = new Thread(new TerminalInputHandler(this));
        terminalInputHandlerThread.start();

        // zero index of roomlist should always have lobby
        roomList = new ArrayList<>();

        lobby = newRoom();

        System.out.println("Server successfully started on port " + port);
    }

    public List<Peer> getPeerList() {
        return peerList;
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
                System.out.println(newPeer.getName() + " connected");


            } catch (IOException e) {
                if (running) {
                System.out.println("Thread was unable to create socket on port " + port);
                }
            }


        }
    }

    // TODO sort
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

    public void shutDown() {
        this.running = false;
        for (Peer p : peerList) {
            p.setRunning(false);
            p.close();
        }

        newConnectionThread.interrupt();

        try {
            serverSock.close();
            System.out.println("Server socket closed");
        } catch (IOException e) {
            System.out.println("Error in closing server socket. Printing error");
            e.printStackTrace();
        }

        try {
            terminalInputHandlerThread.interrupt();
            System.out.println("Terminal input handling thread closed");
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println("Error in closing terminal input thread");
        }

    }

} // end of class connection.server.Server

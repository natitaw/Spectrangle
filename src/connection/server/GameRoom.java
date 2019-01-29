package connection.server;

import connection.Peer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class GameRoom extends Room{

    public boolean hasFinished = false;
    private Server serverObject;



    public GameRoom (int nr, Server inputServer) {
        this(nr, new ArrayList<>(), inputServer);
    }

    public GameRoom(int nr, List<Peer> peers, Server inputServer) {
        super(nr, peers);
        this.serverObject = inputServer;

    }

    public void startGame() {
        determineOrder();

        // TODO implement game logic
        // TODO change terminalinput state
    }


    public void determineOrder(){
        List<String> nameList = peerList.stream().map(Peer::getName).collect(Collectors.toList());
        Collections.sort(nameList);

        // Determine order
        // send chat message
    }

    // TODO make sure this gets called basically everywhere
    public void checkIfAllPeersAreRunning(){
        boolean allRunning=true;
        String disconnectedPeerName = null;
        for (Peer p : peerList){ // decided not to use iterator and do lazy checking, because two might disconnect at once
            if (!p.isRunning()){
                allRunning=false;
                disconnectedPeerName = p.getName();
                peerList.remove(p);
            }
        }
        if (!allRunning){
            peerDisconnected(disconnectedPeerName);
        }
    }


    public void peerDisconnected(String name){
        for (Peer p : peerList) {
            // did not use sendmessagetoroom because we needed a for loop anyways
            p.sendMessage("player " + name + " left");
            p.moveToRoom(serverObject.getRoomList().get(0));
        }
        hasFinished=true;
    }

}

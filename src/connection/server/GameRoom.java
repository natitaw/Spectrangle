package connection.server;

import connection.Peer;
import game.Piece;
import game.TileBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GameRoom extends Room{

    public boolean hasFinished = false;
    private Server serverObject;
    private TileBag roomBag;



    public GameRoom (int nr, Server inputServer) {
        this(nr, new ArrayList<>(), inputServer);
    }

    public GameRoom(int nr, List<Peer> peers, Server inputServer) {
        super(nr, peers);
        this.serverObject = inputServer;

    }

    public void startGame() {
        determineOrder();
        checkIfAllPeersAreRunning();

        roomBag = new TileBag(36);
        roomBag.populateBag();

        int highestValue = 0;
        Peer startingPlayer = null;
        for (Peer p : peerList) {
            p.setTileBag(new TileBag(4));
            Piece drawnPiece = roomBag.takeRandomPiece();
            p.getTileBag().addPiece(drawnPiece);

            if (drawnPiece.getValue() > highestValue) {
                highestValue = drawnPiece.getValue();
                startingPlayer = p;
            }
        }

        for (Peer p : peerList) {
            for (int i=1; i<=3; i++) {
                p.getTileBag().addPiece(roomBag.takeRandomPiece());
            }
        }

        newTurn(startingPlayer);

    }

    private void newTurn(Peer startingPlayer) {
        // TODO if terminalinputhandler is waiting on something else, interrupt with YourTurnException and switch state to YOUR_TURN


        // TODO change terminalinputstate to NOT_YOUR_TURN

        // check if game ended? then run game ended method
    }


    public void determineOrder(){
        // TODO Check if this is the right way of determining order
        Collections.sort(peerList);
        List<String> nameList = peerList.stream().map(Peer::getName).collect(Collectors.toList());
        String arg = String.join(" ", nameList);
        for (Peer p : peerList) {
            p.sendMessage("order " + arg);
        }
        // send chat message
    }

    // TODO make sure this gets called at the start of each turn
    // TODO make sure peerDisconnected is run at exception
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

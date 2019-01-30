package connection.server;

import connection.Peer;
import game.Board;
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
    private Board board;
    private boolean waitingforMove;


    public GameRoom (int nr, Server inputServer) {
        this(nr, new ArrayList<>(), inputServer);
    }

    public GameRoom(int nr, List<Peer> peers, Server inputServer) {
        super(nr, peers);
        this.serverObject = inputServer;
        this.waitingforMove=false;

    }

    public void startGame() {
        determineOrder();
        checkIfAllPeersAreRunning();


        roomBag = new TileBag(36);
        roomBag.populateBag();
        board = new Board(roomBag);

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

    public boolean isWaitingforMove() {
        return waitingforMove;
    }

    public void setWaitingforMove(boolean waitingforMove) {
        this.waitingforMove = waitingforMove;
    }

    private void newTurn(Peer startingPlayer) {
        this.waitingforMove=true;

        // TODO actually test if player has no valid moves and set this bool properly
        boolean mustSkip = false;

        sendTilesCommand(startingPlayer, mustSkip);

        waitforMove(startingPlayer);

        // check if game ended? then run game ended method (SORT BY SCORE)
        // else start new turn with next player

    }

    private void waitforMove(Peer startingPlayer) {
        while (waitingforMove) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        // let player announce move (skip, place or exchange)

        // announce move
    }

    private void sendTilesCommand(Peer startingPlayer, boolean mustSkip) {
        String command = "tiles";
        List<String> middleArgs = new ArrayList<>();
        String lastString = null;

        for (Peer p : peerList) {
            List<String> peerArgs = new ArrayList<>();
            peerArgs.add(p.getName());
            for (int peerPieceIndex = 0; peerPieceIndex < p.getTileBag().getNumberOfPieces(); peerPieceIndex++) {
                String pieceString = p.getTileBag().viewPiece(peerPieceIndex).toString();
                peerArgs.add(pieceString);
            }
            // concatenate all peerargs to one peerString
            middleArgs.addAll(peerArgs);
        }

        String middleString = String.join(" ", middleArgs);


        if (mustSkip) {
            lastString = "skip " + startingPlayer.getName();
        } else {
            lastString = "turn " + startingPlayer.getName();
        }

        String fullCommand = command + " " + middleString + " " + lastString;
        for (Peer p : peerList){
            p.sendMessage(fullCommand);
        }
    }


    public void determineOrder(){
        // TODO Check if this is the right way of determining order
        Collections.sort(peerList);
        List<String> nameList = peerList.stream().map(Peer::getName).collect(Collectors.toList());
        String arg = String.join(" ", nameList);
        for (Peer p : peerList) {
            p.sendMessage("order " + arg);
        }
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


    // TODO implement
    public void checkPlace(Peer peer, String arg, int parseInt) {
        // check it, if valid:
        // do move
        // announce it
        // update waitingformove
    }
    // TODO implement
    public void checkSkip(Peer peer) {
        // check it, if valid:
        // do move
        // announce it
        // update waitingformove
    }
    // TODO implement
    public void checkExchange(Peer peer, String arg) {
        // check it, if valid:
        // do move
        // announce it
        // update waitingformove
    }
}

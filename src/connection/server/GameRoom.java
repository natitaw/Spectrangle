package connection.server;

import connection.Peer;
import game.Board;
import game.EmptyBagException;
import game.Piece;
import game.TileBag;

import java.util.*;
import java.util.stream.Collectors;

public class GameRoom extends Room implements Runnable {

    public boolean hasFinished = false;
    private Server serverObject;
    private TileBag roomBag;
    private Board board;
    private boolean waitingforMove;
    private Peer currentPlayer;
    private int currentPlayernr;
    boolean mustSkip;


    public GameRoom(int nr, Server inputServer) {
        this(nr, new ArrayList<>(), inputServer);
    }

    public GameRoom(int nr, List<Peer> peers, Server inputServer) {
        super(nr, peers);
        this.serverObject = inputServer;
        this.waitingforMove = false;
        this.currentPlayer = null;
        this.mustSkip = false;

    }

    @Override
    public void run() {
        determineOrder();
        checkIfAllPeersAreRunning();


        roomBag = new TileBag(36);
        roomBag.populateBag();
        board = new Board(roomBag);

        int highestValue = 0;
        Peer startingPlayer = null;
        for (Peer p : peerList) {
            p.setScore(0);
            p.setTileBag(new TileBag(4));
            Piece drawnPiece = null;
            try {
                drawnPiece = roomBag.takeRandomPiece();
            } catch (EmptyBagException e) {
                System.out.println(e.getMessage());
            }
            p.getTileBag().addPiece(drawnPiece);

            if (drawnPiece.getValue() > highestValue) {
                highestValue = drawnPiece.getValue();
                startingPlayer = p;
            }
        }

        for (Peer p : peerList) {
            for (int i = 1; i <= 3; i++) {
                try {
                    p.getTileBag().addPiece(roomBag.takeRandomPiece());
                } catch (EmptyBagException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        newTurn(startingPlayer);

    }

    public int getCurrentPlayernr() {
        return currentPlayernr;
    }

    public void setCurrentPlayernr(int currentPlayernr) {
        this.currentPlayernr = currentPlayernr;
    }

    public boolean isWaitingforMove() {
        return waitingforMove;
    }

    public void setWaitingforMove(boolean waitingforMove) {
        this.waitingforMove = waitingforMove;
    }

    // TODO Check if this actually works
    private boolean hasValidMoves(Peer player) {
        boolean result = false;
        Iterator itr = player.getTileBag().getBag().iterator();

        while (!result && itr.hasNext()){
            Piece piece = (Piece) itr.next();
            int i=0;
            while (!result && i<36) {
                result = board.isValidMove(i, piece);
                i++;
            }
        }

        return result;
    }
    // TODO check if this works (will not work if hasValidMoves does not work)
    private boolean checkIfGameHasEnded() {
        boolean result=false;
        Iterator itr = peerList.iterator();
        while ((result) && itr.hasNext()){
            Peer p = (Peer) itr.next();
            result = (!hasValidMoves(p));
            // if p has any moves, set result=false
        }

        return result;
    }


    private void newTurn(Peer startingPlayer) {
        this.currentPlayer = startingPlayer;
        this.waitingforMove = true;


        mustSkip = (!hasValidMoves(currentPlayer));

        sendTilesCommand(startingPlayer, mustSkip);

        waitforMove(startingPlayer);

        hasFinished=checkIfGameHasEnded();
        
        if (hasFinished) {
            endGame();
        } else {
            int newIndex = (currentPlayernr + 1) % (peerList.size());
            Peer newPeer = peerList.get(newIndex);
            newTurn(newPeer);
        }


    }



    private void endGame() {
        Collections.sort(peerList, new Comparator<Peer>() {
            @Override
            public int compare(Peer p1, Peer p2) {
                return Integer.compare(p1.getScore(), p2.getScore());
            }
        });

        List<String> args = new ArrayList<>();
        for (Peer p1 : peerList){
            args.add(p1.getName());
            args.add(String.valueOf(p1.getScore()));
        }


        String argString = String.join(" ", args);

        serverObject.printer.println("Room "+ getRoomNumber());
        serverObject.printer.println("game finished leaderboard " + argString);

        while (!peerList.isEmpty()){
            peerList.get(0).sendMessage("game finished leaderboard " + argString);
            peerList.get(0).moveToRoom(serverObject.getRoomList().get(0));
        }

    }

    private void waitforMove(Peer startingPlayer) {
        while (waitingforMove) {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            checkIfAllPeersAreRunning();
        }
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
        serverObject.printer.println("Room "+ getRoomNumber());
        serverObject.printer.println(fullCommand);
        for (Peer p : peerList) {

            p.sendMessage(fullCommand);
        }
    }


    public void determineOrder() {
        // TODO Check if this is the right way of determining order
        Collections.sort(peerList);
        List<String> nameList = peerList.stream().map(Peer::getName).collect(Collectors.toList());
        String arg = String.join(" ", nameList);
        serverObject.printer.println("Room "+ getRoomNumber());
        serverObject.printer.println("order " + arg);
        for (Peer p : peerList) {

            p.sendMessage("order " + arg);
        }
    }

    public void checkIfAllPeersAreRunning() {
        boolean allRunning = true;
        String disconnectedPeerName = null;
        for (Peer p : peerList) { // decided not to use iterator and do lazy checking, because two might disconnect at once
            if (!p.isRunning()) {
                allRunning = false;
                disconnectedPeerName = p.getName();
                peerList.remove(p);
            }
        }
        if (!allRunning) {
            peerDisconnected(disconnectedPeerName);
        }
    }


    public void peerDisconnected(String name) {
        serverObject.printer.println("Room "+ getRoomNumber());
        serverObject.printer.println("player " + name + " left");

        for (Peer p : peerList) {
            // did not use sendmessagetoroom because we needed a for loop anyways

            p.sendMessage("player " + name + " left");
            p.moveToRoom(serverObject.getRoomList().get(0));
        }
        waitingforMove=false;
        hasFinished = true;
    }


    public void checkPlace(Peer peer, String tileString, int index) {
        Piece newPiece = new Piece(tileString);
        if (peer.equals(this.currentPlayer)) {
            if (!mustSkip) {

                if (peer.getTileBag().findPiece(newPiece) >= 0) {

                    if (board.isValidMove(index, newPiece)) {
                        int pointsScored = board.movePiece(index, newPiece);
                        peer.incScore(pointsScored);
                        serverObject.printer.println("Room " + getRoomNumber());
                        serverObject.printer.println("move " + peer.getName() + " " + tileString + " " + index + " " + pointsScored);
                        for (Peer p : peerList) {
                            p.sendMessage("move " + peer.getName() + " " + tileString + " " + index + " " + pointsScored);
                        }


                        int pieceIndex = peer.getTileBag().findPiece(newPiece);

                        try {
                            peer.getTileBag().takePiece(pieceIndex);
                        } catch (EmptyBagException e) {
                            serverObject.printer.println("Player bag is empty");
                        }



                            try {
                                peer.getTileBag().addPiece(roomBag.takeRandomPiece());
                            } catch (EmptyBagException e) {
                                serverObject.printer.println("Room bag is empty");
                            }

                        waitingforMove = false;
                    }
                }
            }
        }

    }

    public void checkSkip(Peer peer) {
        if (peer.equals(this.currentPlayer)) {
            if (mustSkip) {
                serverObject.printer.println("Room "+ getRoomNumber());
                serverObject.printer.println("player skipped " + peer.getName());
                for (Peer p : peerList) {
                    p.sendMessage("player skipped " + peer.getName());
                }
                waitingforMove = false;
            }
        }
    }


    public void checkExchange(Peer peer, String tileArg) {
        if (peer.equals(this.currentPlayer)) {
            if (mustSkip) {
                boolean cont = false;
                Piece removedTile = new Piece(tileArg);
                if (peer.getTileBag().getBag().remove(removedTile)){
                    cont=true;
                } else if (peer.getTileBag().getBag().remove(removedTile.getRotated())){
                    cont=true;
                    removedTile.rotate();
                } else if (peer.getTileBag().getBag().remove(removedTile.getRotated())) {
                    cont=true;
                    removedTile.rotate2x();
                }



                if (cont) {
                    Piece newTile = null;
                    try {
                        newTile = roomBag.takeRandomPiece();
                    } catch (EmptyBagException e) {
                        serverObject.printer.println(e.getMessage());
                    }

                    peer.getTileBag().addPiece(newTile);
                    roomBag.addPiece(removedTile);;
                    serverObject.printer.println("Room "+ getRoomNumber());
                    serverObject.printer.println("replace " + peer.getName() + " " + removedTile.toString() + " with " + newTile.toString());
                    for (Peer p : peerList) {
                        p.sendMessage("replace " + peer.getName() + " " + removedTile.toString() + " with " + newTile.toString());
                    }
                    waitingforMove = false;
                }

            }
        }
    }


}

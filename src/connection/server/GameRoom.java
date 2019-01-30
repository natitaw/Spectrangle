package connection.server;

import connection.Peer;
import game.Board;
import game.EmptyBagException;
import game.Piece;
import game.TileBag;

import java.util.*;
import java.util.stream.Collectors;

public class GameRoom extends Room {

    public boolean hasFinished = false;
    private Server serverObject;
    private TileBag roomBag;
    private Board board;
    private boolean waitingforMove;
    private Peer currentPlayer;
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

    public void startGame() {
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

    public boolean isWaitingforMove() {
        return waitingforMove;
    }

    public void setWaitingforMove(boolean waitingforMove) {
        this.waitingforMove = waitingforMove;
    }

    private void newTurn(Peer startingPlayer) {
        this.currentPlayer = startingPlayer;
        this.waitingforMove = true;

        // TODO actually test if player has no valid moves and set this bool properly
        mustSkip = false;

        sendTilesCommand(startingPlayer, mustSkip);

        waitforMove(startingPlayer);

        // TODO actually check if game ended
        hasFinished=false;
        
        if (hasFinished) {
            endGame();
        } else {
            Iterator itr = peerList.listIterator( ((ArrayList<Peer>) peerList).indexOf(startingPlayer));
            newTurn((Peer) itr.next());
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

        for (Peer p : peerList){
            p.sendMessage("game finished leaderboard " + argString);
            p.moveToRoom(serverObject.getRoomList().get(0));
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
        for (Peer p : peerList) {
            p.sendMessage(fullCommand);
        }
    }


    public void determineOrder() {
        // TODO Check if this is the right way of determining order
        Collections.sort(peerList);
        List<String> nameList = peerList.stream().map(Peer::getName).collect(Collectors.toList());
        String arg = String.join(" ", nameList);
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
                if (board.isValidMove(index, newPiece)) {
                    int pointsScored = board.movePiece(index, newPiece);
                    peer.incScore(pointsScored);
                    for (Peer p : peerList) {
                        p.sendMessage("move " + peer.getName() + " " + tileString + " " + index + " " + pointsScored);
                    }
                    waitingforMove = false;
                }
            }
        }

    }

    public void checkSkip(Peer peer) {
        if (peer.equals(this.currentPlayer)) {
            if (mustSkip) {
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
                Piece removedTile = new Piece(tileArg);
                peer.getTileBag().getBag().remove(removedTile); // TODO check if this actually works, I reimplemented equals method
                Piece newTile = null;
                try {
                    newTile = roomBag.takeRandomPiece();
                } catch (EmptyBagException e) {
                    System.out.println(e.getMessage());
                }

                peer.getTileBag().addPiece(newTile);
                roomBag.addPiece(removedTile);;

                for (Peer p : peerList) {
                    p.sendMessage("replace " + peer.getName() + " " + removedTile.toString() + " with " + newTile.toString());
                }
                waitingforMove = false;
            }
        }
    }
}

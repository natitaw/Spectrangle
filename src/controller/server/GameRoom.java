package controller.server;

import controller.Peer;
import model.Board;
import model.EmptyBagException;
import model.Piece;
import model.TileBag;

import java.io.IOException;
import java.net.SocketException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GameRoom class. Extends Room class with functionality for playing games.
 *
 * @author Bit 4 - Group 4
 * @see controller.server.Room
 */
public class GameRoom extends Room implements Runnable {

    private final Server serverObject;
    private boolean hasFinished = false;
    private TileBag roomBag;
    private Board board;
    private boolean waitingforMove;
    private Peer currentPlayer;
    private int currentPlayernr;
    private boolean mustSkip;

    /**
     * GameRoom constructor. Passes arguments to other version of constructor, along with an empty peer list.
     *
     * @param nr          The number of the room, must be unique and one higher than highest existing room nr.
     * @param inputServer Server object that is to be the "parent" of this room.
     * @author Bit 4 - Group 4
     */
    public GameRoom(int nr, Server inputServer) {
        this(nr, new ArrayList<>(), inputServer);
    }

    /**
     * GameRoom constructor. Same as other constructor, but takes a list of peers as param as well.
     *
     * @param nr          The number of the room, must be unique and one higher than highest existing room nr.
     * @param inputServer Server object that is to be the "parent" of this room.
     * @param peers       List of peers that the room contains.
     * @author Bit 4 - Group 4
     */
    private GameRoom(int nr, List<Peer> peers, Server inputServer) {
        super(nr, peers);
        this.serverObject = inputServer;
        this.waitingforMove = false;
        this.currentPlayer = null;
        this.mustSkip = false;

    }

    /**
     * run method for the thread that will run on this object. The thread is for game logic.
     * It creates an instance of Board and TileBag, which are crucial to play the game.
     * The order of turns is determined using determineOrder method.
     * Then, the players take pieces from the TileBag into their own bag, deciding which player
     * starts first according to the game rules as specified on Canvas.
     * Then, while the game has not ended, the Thread will keep starting new turns, looping through
     * the list of players for the turns.
     *
     * @author Bit 4 - Group 4
     */
    @Override
    public void run() {
        determineOrder();
        checkIfAllPeersAreRunning();


        roomBag = new TileBag(36);
        roomBag.populateBag();
        board = new Board();

        int highestValue = 0;
        Peer startingPlayer = null;
        for (Peer p : peerList) {
            p.setScore(0);
            p.setTileBag(new TileBag(4));
            Piece drawnPiece = null;
            try {
                drawnPiece = roomBag.takeRandomPiece();
            } catch (EmptyBagException e) {
                serverObject.getPrinter().println(e.getMessage());
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
                    serverObject.getPrinter().println(e.getMessage());
                }
            }
        }

        while (!hasFinished) {
            newTurn(startingPlayer);

            hasFinished = checkIfGameHasEnded();

            if (hasFinished) {
                endGame();
            } else {
                currentPlayernr = ((ArrayList) peerList).indexOf(startingPlayer);
                int newIndex = (currentPlayernr + 1) % (peerList.size());
                startingPlayer = peerList.get(newIndex);
            }

        }


    }

    /**
     * Checks if a player has any valid moves left according to the tiles in their bag,
     * and the current board state.
     *
     * @param player The Peer instance to check the valid moves for
     * @author Bit 4 - Group 4
     * @return the boolean that specifies if the player has valid moves
     */
    private boolean hasValidMoves(Peer player) {
        boolean result = false;
        Iterator itr = player.getTileBag().getBag().iterator();

        while (!result && itr.hasNext()) {
            Piece piece = (Piece) itr.next();
            List<Piece> pieceRotations = new ArrayList<>();
            pieceRotations.add(piece);
            pieceRotations.add(piece.getRotated());
            pieceRotations.add(piece.getRotated2x());
            for (Piece rotatedPiece : pieceRotations) {
                int i = 0;
                while (!result && i < 36) {
                    result = board.isValidMove(i, rotatedPiece);
                    i++;
                }
            }

        }

        return result;
    }

    /**
     * Checks if players have valid moves.
     * According to the game rules, the game ends if the bag is empty and no players can place tiles.
     * Because players must always take a new tile if they can, we only have to check if any player
     * can place tiles.
     *
     * @author Bit 4 - Group 4
     * @return The boolean that specified if the game has ended
     */
    private boolean checkIfGameHasEnded() {
        boolean result = false;
        Iterator itr = peerList.iterator();
        while ((result) && itr.hasNext()) {
            Peer p = (Peer) itr.next();
            result = (!hasValidMoves(p));
            // if p has any moves, set result=false
        }

        return result;
    }

    /**
     * This method checks if the current player must skip or not, and then sends the appropriate
     * "tiles" command to everyone using sendTilesCommand. Then, it waits until another thread
     * that reads network traffic runs checkMove, checkExchange or checkSkip and successfully
     * executes a valid move. When this happens, the waitingForMove boolean is set to false, and
     * another turn begins.
     *
     * @param startingPlayer The player who makes a move this turn.
     * @author Bit 4 - Group 4
     */
    private void newTurn(Peer startingPlayer) {
        this.currentPlayer = startingPlayer;
        this.waitingforMove = true;

        mustSkip = (!hasValidMoves(currentPlayer));

        sendTilesCommand(startingPlayer, mustSkip);

        waitforMove();
    }

    /**
     * Ends game.
     * Starts by sorting the peerList by score (decsending).
     * Sends the game finished command to everyone in room, and moves them back to lobby,
     *
     * @author Bit 4 - Group 4
     */
    private void endGame() {
        peerList.sort(new Comparator<Peer>() {
            @Override
            public int compare(Peer p1, Peer p2) {
                // Compare p2 to p1 to get descending instead of ascending order
                return Integer.compare(p2.getScore(), p1.getScore());
            }
        });

        List<String> args = new ArrayList<>();
        for (Peer p1 : peerList) {
            args.add(p1.getName());
            args.add(String.valueOf(p1.getScore()));
        }


        String argString = String.join(" ", args);

        serverObject.getPrinter().println("Room " + getRoomNumber());
        serverObject.getPrinter().println("game finished leaderboard " + argString);

        while (!peerList.isEmpty()) {
            peerList.get(0).sendMessage("game finished leaderboard " + argString);
            peerList.get(0).moveToRoom(serverObject.getRoomList().get(0));
        }
    }

    /**
     * Waits for current player to finish move. Checks if all peers are still connected every 0.4s
     * Keeps checking the waitingforMove field to see if another thread has ended the turn.
     *
     * @author Bit 4 - Group 4
     */
    private void waitforMove() {
        while (waitingforMove) {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            checkIfAllPeersAreRunning();
        }
    }

    /**
     * Sends the "tiles" command as defined in the protocol.
     * Concatenates a series of strings that contain informations about player tiles, and adds
     * "skip playerName" or "turn playerName" depending on if the player, whose name is playerName,
     * should skip their turn or not (specified in mustSkip param).
     *
     * @param mustSkip Whether the player must skip their turn
     * @param startingPlayer Peer instance that is to start this turn
     */
    private void sendTilesCommand(Peer startingPlayer, boolean mustSkip) {
        String command = "tiles";
        List<String> middleArgs = new ArrayList<>();
        String lastString;

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
        serverObject.getPrinter().println("Room " + getRoomNumber());
        serverObject.getPrinter().println(fullCommand);
        for (Peer p : peerList) {

            p.sendMessage(fullCommand);
        }
    }

    /**
     * Determines the order of players for the game using alphabetical ordering system (ascending).
     * Then sends the order of players.
     */
    private void determineOrder() {
        Collections.sort(peerList);
        List<String> nameList = peerList.stream().map(Peer::getName).collect(Collectors.toList());
        String arg = String.join(" ", nameList);
        serverObject.getPrinter().println("Room " + getRoomNumber());
        serverObject.getPrinter().println("order " + arg);
        for (Peer p : peerList) {

            p.sendMessage("order " + arg);
        }
    }

    /**
     * Checks if all peers are still connected. If a peer has disconnected (has its running field
     * set to false by another thread that handles connections) then it is removed from the peerList
     * and afterwards the game is ended using the peerDisconnected method.
     */
    private void checkIfAllPeersAreRunning() {
        boolean allRunning = true;
        String disconnectedPeerName = null;
        for (Peer p : peerList) { // decided not to use iterator and do lazy checking, because two might disconnect at once
            if (!p.isRunning()) {
                allRunning = false;
                disconnectedPeerName = p.getName();
                peerList.remove(p);
                serverObject.getPeerList().remove(p);
                p.close();
            }
        }
        if (!allRunning) {
            peerDisconnected(disconnectedPeerName);
        }
    }

    /**
     * Sends to all the players that player "name" left. Then moves them to lobby.
     * @param name Name of the player who left, who is no longer in the peerList.
     */
    private void peerDisconnected(String name) {
        serverObject.getPrinter().println("Room " + getRoomNumber());
        serverObject.getPrinter().println("player " + name + " left");

        for (Peer p : peerList) {
            // did not use sendmessagetoroom because we needed a for loop anyways

            p.sendMessage("player " + name + " left");
            p.moveToRoom(serverObject.getRoomList().get(0));
        }
        waitingforMove = false;
        hasFinished = true;
    }

    /**
     * Checks if a "place" move is valid.
     * First, check if it is the player's move.
     * Then, checks if the player does not have to skip.
     * Then, it checks if the player indeed has the piece he is trying to place.
     * Then, checks if the move is valid by executing board.isValidMove on the tile and index.
     * Then executes the move. Removes the tile from the player's bag, and tries to move
     * a piece from the room bag to the player's bag.
     *
     * @param peer Player who tried to execute this move
     * @param tileString String representation (e.g. RRR6) of tile to be placed
     * @param index Index representation (0-35) of where the tile is to be placed on the board.
     */
    public void checkPlace(Peer peer, String tileString, int index) {
        Piece newPiece = new Piece(tileString);
        if (peer.equals(this.currentPlayer)) {
            if (!mustSkip) {

                if (peer.getTileBag().findPiece(newPiece) >= 0) {

                    if (board.isValidMove(index, newPiece)) {
                        int pointsScored = board.movePiece(index, newPiece);
                        peer.incScore(pointsScored);
                        serverObject.getPrinter().println("Room " + getRoomNumber());
                        serverObject.getPrinter().println("move " + peer.getName() + " " + tileString + " " + index + " " + pointsScored);
                        for (Peer p : peerList) {
                            p.sendMessage("move " + peer.getName() + " " + tileString + " " + index + " " + pointsScored);
                        }


                        int pieceIndex = peer.getTileBag().findPiece(newPiece);

                        try {
                            peer.getTileBag().takePiece(pieceIndex);
                        } catch (EmptyBagException e) {
                            serverObject.getPrinter().println("Player bag is empty");
                        }


                        try {
                            peer.getTileBag().addPiece(roomBag.takeRandomPiece());
                        } catch (EmptyBagException e) {
                            serverObject.getPrinter().println("Room bag is empty");
                        }

                        waitingforMove = false;
                    }
                }
            }
        }

    }

    /**
     * Checks if a "skip" move is valid.
     * First, check if it is the player's move.
     * Then, checks if the player has to skip.
     * If so, we go to the next turn and send out the appropriate command.
     *
     */
    public void checkSkip(Peer peer) {
        if (peer.equals(this.currentPlayer)) {
            if (mustSkip) {
                serverObject.getPrinter().println("Room " + getRoomNumber());
                serverObject.getPrinter().println("player skipped " + peer.getName());
                for (Peer p : peerList) {
                    p.sendMessage("player skipped " + peer.getName());
                }
                waitingforMove = false;
            }
        }
    }

    /**
     * Checks if an "exchange" move is valid.
     * First, check if it is the player's move.
     * Then, checks if the player has to skip.
     * Then checks if the player really has the tile.
     * Then exchange the tile for one in the bag if the bag is not empty
     * If so, we go to the next turn and send out the appropriate command.
     *
     */
    public void checkExchange(Peer peer, String tileArg) {
        if (peer.equals(this.currentPlayer)) {
            if (mustSkip) {
                boolean cont = false;
                Piece removedTile = new Piece(tileArg);
                if (peer.getTileBag().getBag().remove(removedTile)) {
                    cont = true;
                } else if (peer.getTileBag().getBag().remove(removedTile.getRotated())) {
                    cont = true;
                    removedTile.rotate();
                } else if (peer.getTileBag().getBag().remove(removedTile.getRotated2x())) {
                    cont = true;
                    removedTile.rotate2x();
                }


                if (cont) {
                    Piece newTile = null;
                    try {
                        newTile = roomBag.takeRandomPiece();
                        peer.getTileBag().addPiece(newTile);
                        roomBag.addPiece(removedTile);
                        serverObject.getPrinter().println("Room " + getRoomNumber());
                        serverObject.getPrinter().println("replace " + peer.getName() + " " + removedTile.toString() + " with " + newTile.toString());
                        for (Peer p : peerList) {
                            p.sendMessage("replace " + peer.getName() + " " + removedTile.toString() + " with " + newTile.toString());
                        }
                        waitingforMove = false;
                    } catch (EmptyBagException e) {
                        serverObject.getPrinter().println(e.getMessage());
                        peer.sendMessage("invalid move");
                    }



                }

            }
        }
    }


}

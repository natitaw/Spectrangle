package controller.client;

import model.Board;
import model.Piece;
import model.TileBag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 * ClientCommands class. Contains some static commands that can be executed on the client.
 * Are usually called from the CommandInterpreter.
 * Most methods here require the parent to be passed because one instance of the game might have
 * multiple client instances running (AIs vs player in singleplayer mode)
 *
 * @author Bit 4 - Group 4
 */
public class ClientCommands {

    /**
     * Method some time after client receives the "tiles" command from the server
     * Saves the parent client's current tiles in the Client "clientTiles" field.
     *
     * @author Bit 4 - Group 4
     * @param args Arguments sent with the "tiles" command
     * @param clientObject Parent client object.
     */
    public static void setTiles(String[] args, Client clientObject) {
        String[] middleArgs = Arrays.copyOfRange(args, 0, args.length - 2);
        for (int peerNr = 0; peerNr < middleArgs.length / 5; peerNr++) {
            String name = middleArgs[peerNr * 5];
            if (name.equals(clientObject.getName())) {

                String[] tileArgs = Arrays.copyOfRange(middleArgs, (peerNr * 5) + 1, (peerNr * 5) + 5);
                clientObject.setClientTiles(Arrays.asList(tileArgs));


            }
        }
    }



    /**
     * Sets the client's board to a new, empty board
     * @param clientObject client to do this for
     */
    public static void makeBoard(Client clientObject) {
        clientObject.setBoard(new Board());
    }



    /**
     * Method some time after client receives the "tiles" command from the server
     * Sets opponents' current tiles in the Client "otherTiles" field.
     * Prints them in the shortened format specified in protocol, e.g. WWW1 or RRR6.
     *
     * @author Bit 4 - Group 4
     * @param args Arguments sent with the "tiles" command
     * @param clientObject Parent client object.
     */
    public static void otherTiles(String[] args, Client clientObject) {
        clientObject.getPrinter().println("Other players have tiles:");
        clientObject.setOtherTileList(new ArrayList<>());
        String[] middleArgs = Arrays.copyOfRange(args, 0, args.length - 3);
        for (int peerNr = 0; peerNr < middleArgs.length / 5; peerNr++) {
            String name = middleArgs[peerNr * 5];
            if (!name.equals(clientObject.getName())) {
                String[] printArgs = Arrays.copyOfRange(args, peerNr * 5 + 1, peerNr * 5 + 5);
                clientObject.getOtherTileList().add(Arrays.asList(printArgs));
                String printString = String.join(" ", printArgs);
                printString = name + ": " + printString;


                clientObject.getPrinter().println(printString);
            }
        }


    }

    // AI RELATED METHODS DOWN HERE //

    /**
     * Calculates best moves for a client
     * Checks client's board. Checks for every piece:
     * For every rotated variant of this piece:
     * For every location on the board:
     * the score moving it there would generate (if invalid: 0)
     * Then returns the move (as string "place XX on YY") with highest score.
     *
     * @param clientObject Client to generate this move for
     * @param tileBag Client's tiles to generate this move for
     * @return the best move possible in string representation i.e. "place RRR6 on 20"
     */
    public static String bestMove(TileBag tileBag, Client clientObject) {
        int result = 0;
        Piece bestPiece = null;
        int bestPos = 0;
        Iterator itr = tileBag.getBag().iterator();

        while (itr.hasNext()) {
            Piece piece = (Piece) itr.next();
            if (piece.getValue()>0) {
                List<Piece> pieceRotations = new ArrayList<>();
                pieceRotations.add(piece);
                pieceRotations.add(piece.getRotated());
                pieceRotations.add(piece.getRotated2x());
                for (Piece rotatedPiece : pieceRotations) {
                    int i = 0;
                    while (i < 36) {
                        int thisScore = clientObject.getBoard().getPotentialMoveScore(i, rotatedPiece);
                        if (thisScore > result) {
                            result = thisScore;
                            bestPiece = rotatedPiece;
                            bestPos = i;
                        }
                        i++;
                    }
                }
            }
        }

        return "place " + bestPiece.toString() + " on " + bestPos;
    }

    /**
     * Calculates a "random" move for a client (just the first move that it finds to be possible).
     * Checks client's board. Checks for every piece:
     * For every rotated variant of this piece:
     * For every location on the board:
     * if placing it there is a valid move
     * Then returns that move
     *
     * @param clientObject Client to generate this move for
     * @param tileBag Client's tiles to generate this move for
     * @return the first move found in string representation i.e. "place RRR6 on 20"
     */
    private static String randomMove(TileBag tileBag, Client clientObject) {
        Iterator itr = tileBag.getBag().iterator();

        while (itr.hasNext()) {
            Piece piece = (Piece) itr.next();
            List<Piece> pieceRotations = new ArrayList<>();
            pieceRotations.add(piece);
            pieceRotations.add(piece.getRotated());
            pieceRotations.add(piece.getRotated2x());
            for (Piece rotatedPiece : pieceRotations) {
                int i = 0;
                while (i < 36) {
                    if (clientObject.getBoard().isValidMove(i, rotatedPiece)) {
                        return "place " + rotatedPiece.toString() + " on " + i;

                    }
                    i++;

                }
            }

        }
        return "";
    }

    /**
     * Method executes when an AI client is given the turn
     * Usually called from TerminalInputHandler, in the AI_TURN state.
     * Depending on the AI's difficulty, generates a random or best move.
     *
     * @param clientObject Client to generate this move for
     */
    public static void aiTurn(Client clientObject) {
        if (clientObject.getDifficulty() < 1) {
            clientObject.sendMessageToAll(randomMove(TileBag.generateBag(clientObject.getClientTiles()), clientObject));
        } else if (clientObject.getDifficulty() < 2) {
            clientObject.sendMessageToAll(bestMove(TileBag.generateBag(clientObject.getClientTiles()), clientObject));
        }
    }

    /**
     * Method executes when an AI client is given the turn but must skip
     * Usually called from TerminalInputHandler, in the AI_SKIP state.
     * Generates some random numbers using which it determines to skip or exchange
     * Also checks nr of tiles in player's hands and in the room bag
     * Always skips if player or room bag has no tiles
     * If going to exchange:
     * generate nother random number to determine which tile to exchange
     *
     * @param clientObject Client to generate this move for
     */
    public static void aiSkip(Client clientObject) {
        double random = Math.random();
        TileBag tiles = TileBag.generateBag(clientObject.getClientTiles());

        // check if server tilebag is empty, start by taking nr of tiles 36
        int totalTilesOnBoardAndPlayers = 36;

        // for every empty location on board, subtract a tile from it
        for (int i =0; i<36; i++){
            if (clientObject.getBoard().isEmptyLocation(i)){
                totalTilesOnBoardAndPlayers-=1;
            }
        }

        // for every tile in all players' bags, add 1
        totalTilesOnBoardAndPlayers += clientObject.getOtherTileList().size();
        totalTilesOnBoardAndPlayers += clientObject.getClientTiles().size();

        // or if random value <= 0.5, or room tilebag must be empty,
        // or player tilebag empty, skip turn
        if (random <= 0.5 || totalTilesOnBoardAndPlayers >=36 ||
            tiles.equals(TileBag.generateBag(Arrays.asList("null", "null", "null", "null")))) {
            clientObject.sendMessageToAll("skip");
        } else {

            Piece tile=null;
            boolean tileFound=false;
            while (!tileFound) {
                double secondRandom = Math.random();
                if (secondRandom < 0.25) {
                    if (tiles.viewPiece(0)!=null && tiles.viewPiece(0).getValue()>0) {
                        tile = tiles.viewPiece(0);
                        tileFound=true;
                    }
                } else if (secondRandom < 0.5) {
                    if (tiles.viewPiece(1)!=null && tiles.viewPiece(1).getValue()>0) {
                        tile = tiles.viewPiece(1);
                        tileFound=true;
                    }
                } else if (secondRandom < 0.75) {
                    if (tiles.viewPiece(2)!=null && tiles.viewPiece(2).getValue()>0) {
                        tile = tiles.viewPiece(2);
                        tileFound=true;
                    }
                } else {
                    if (tiles.viewPiece(3)!=null && tiles.viewPiece(3).getValue()>0) {
                        tile = tiles.viewPiece(3);
                        tileFound=true;
                    }
                }
            }
            clientObject.sendMessageToAll("exchange " + tile.toString());
        }

    }
}

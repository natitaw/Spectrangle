package connection.client;

import game.Board;
import game.Piece;
import game.TileBag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class ClientCommands {

    private static Client clientObject;
    private static List<String> clientTiles;
    private static List<List<String>> otherTileList;

    public static void setClientObject(Client inputClientObject) {
        clientObject = inputClientObject;
    }

    public static void setTiles(String[] args){
        String[] middleArgs = Arrays.copyOfRange(args, 0, args.length-2);
        for (int peerNr=0; peerNr < middleArgs.length/5; peerNr++) {
            String name = middleArgs[peerNr * 5];
            if (name.equals(clientObject.getName())) {

                String[] tileArgs = Arrays.copyOfRange(middleArgs, (peerNr * 5) + 1, (peerNr * 5) + 5);
                clientTiles = Arrays.asList(tileArgs);


            }
        }
}

    public static void printTiles() {
        clientObject.getPrinter().println("You have tiles:");

        List<String[]> pieceLineList = new ArrayList<>();
        String[] resultArray;
        for (String t : clientTiles) {
            Piece tempPiece = new Piece(t);
            String pieceString = tempPiece.toPrinterString();
            String[] pieceLines = pieceString.split(Pattern.quote("\n"));
            pieceLineList.add(pieceLines);
        }
        resultArray = new String[]{"","","" ,"" ,"", "" };

        for (int lineNr = 0; lineNr < resultArray.length; lineNr++) {
            for (String[] tempPieceLines : pieceLineList) {
                resultArray[lineNr] = resultArray[lineNr].concat(" ").concat(tempPieceLines[lineNr]);
            }
        }
        String tilesPrinted = String.join("\n", resultArray);
        clientObject.getPrinter().println(tilesPrinted);
        clientObject.getPrinter().println("");
        clientObject.getPrinter().println("    [1]    " + " " + "    [2]    " + " " + "    [3]    " + " " + "    [4]    ");
        clientObject.getPrinter().println("");
    }

    public static void makeBoard(){
        clientObject.setBoard(new Board());
    }

    public static void printBoard() {
        clientObject.getPrinter().println(clientObject.getBoard().toPrinterString());
    }

    // prints other people's tiles in a shortened way
    public static void otherTiles(String[] args) {
        clientObject.getPrinter().println("Other players have tiles:");
        otherTileList = new ArrayList<>();
        String[] middleArgs = Arrays.copyOfRange(args, 0, args.length - 3);
        for (int peerNr = 0; peerNr < middleArgs.length / 5; peerNr++) {
            String name = middleArgs[peerNr * 5];
            if (!name.equals(clientObject.getName())) {
                String[] printArgs = Arrays.copyOfRange(args, peerNr * 5 + 1, peerNr * 5 + 5);
                otherTileList.add(Arrays.asList(printArgs));
                String printString = String.join(" ", printArgs);
                printString = name + ": " + printString;


                clientObject.getPrinter().println(printString);
            }
        }


    }

    public static List<String> getClientTiles() {
        return clientTiles;
    }

    public static void setClientTiles(List<String> clientTiles) {
        ClientCommands.clientTiles = clientTiles;
    }


    private boolean hasValidMoves(TileBag tilebag) {
        boolean result = false;
        Iterator itr = tilebag.getBag().iterator();

        while (!result && itr.hasNext()){
            Piece piece = (Piece) itr.next();
            List<Piece> pieceRotations = new ArrayList<>();
            pieceRotations.add(piece);
            pieceRotations.add(piece.getRotated());
            pieceRotations.add(piece.getRotated2x());
            for   (Piece rotatedPiece : pieceRotations){
                int i=0;
                while (!result && i<36) {
                    result = clientObject.getBoard().isValidMove(i, rotatedPiece);
                    i++;
                }
            }

        }

        return result;
    }

    public static TileBag generateBag(List<String> stringList){
        TileBag result = new TileBag(4);
        for (String s : stringList){
            result.addPiece(new Piece(s));
        }
        return result;
    }

    public static List<TileBag> generateBags(List<List<String>> stringListList){
        List<TileBag> result = new ArrayList<>();
        for (List<String> stringList : stringListList){
            result.add(generateBag(stringList));
        }

        return result;
    }

    public static String bestMove(TileBag tileBag){
        int result = 0;
        Piece bestPiece = null;
        int bestPos = 0;
        Iterator itr = tileBag.getBag().iterator();

        while (itr.hasNext()){
            Piece piece = (Piece) itr.next();
            List<Piece> pieceRotations = new ArrayList<>();
            pieceRotations.add(piece);
            pieceRotations.add(piece.getRotated());
            pieceRotations.add(piece.getRotated2x());
            for   (Piece rotatedPiece : pieceRotations){
                int i=0;
                while (i<36) {
                    int thisScore = clientObject.getBoard().getPotentialMoveScore(i, rotatedPiece);
                    if (thisScore > result) {
                        result = thisScore;
                        bestPiece=rotatedPiece;
                        bestPos=i;
                    }
                    i++;
                }}

        }

        return "place " + bestPiece.toString() + " on " + bestPos;
    }

    public static String randomMove(TileBag tileBag) {
        Iterator itr = tileBag.getBag().iterator();

        while (itr.hasNext()) {
            Piece piece = (Piece) itr.next();
            List<Piece> pieceRotations = new ArrayList<>();
            pieceRotations.add(piece);
            pieceRotations.add(piece.getRotated());
            pieceRotations.add(piece.getRotated2x());
            for   (Piece rotatedPiece : pieceRotations){
                int i = 0;
                while (i < 36) {
                    if (clientObject.getBoard().isValidMove(i, rotatedPiece)) {
                        return "place " + rotatedPiece.toString() + " on " + i;
                    }


                }
            }

        }
        return "";
    }



    public static void aiTurn() {
        if (clientObject.difficulty < 1){
            clientObject.sendMessageToAll(randomMove(generateBag(clientTiles)));
        } else if (clientObject.difficulty < 2) {
            clientObject.sendMessageToAll(bestMove(generateBag(clientTiles)));
        }

        // TODO add calculation based on enemy tiles and future board states
        // TODO cap off this calculation after difficulty in seconds has passed
    }

    public static void aiSkip() {
        double random = Math.random();


        if (random <= 0.5){
            clientObject.sendMessageToAll("skip");
        } else {
            TileBag tiles = generateBag(clientTiles);
            Piece tile;
            double secondRandom = Math.random();
            if (secondRandom < 0.25){
                tile = tiles.viewPiece(0);
            } else if (secondRandom < 0.5){
                tile = tiles.viewPiece(1);
            } else if (secondRandom < 0.75){
                tile = tiles.viewPiece(2);
            } else {
                tile = tiles.viewPiece(3);
            }
            clientObject.sendMessageToAll("exchange " + tile.toString());
        }

    }
}

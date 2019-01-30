package connection.client;

import game.Board;
import game.Piece;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ClientCommands {

    private static Client clientObject;
    private static List<String> clientTiles;

    public static void setClientObject(Client inputClientObject) {
        clientObject = inputClientObject;
    }

    public static void printTiles(String[] args){
        System.out.println("You have tiles:");
        String[] middleArgs = Arrays.copyOfRange(args, 0, args.length-2);
        for (int peerNr=0; peerNr < middleArgs.length/5; peerNr++) {
            String name = middleArgs[peerNr * 5];
            if (name.equals(clientObject.getName())) {
                List<String[]> pieceLineList = new ArrayList<>();
                String[] resultArray;
                String[] tileArgs = Arrays.copyOfRange(middleArgs, (peerNr * 5) + 1, (peerNr * 5) + 4);
                clientTiles = Arrays.asList(tileArgs);
                for (String t : tileArgs) {
                    Piece tempPiece = new Piece(t);
                    String pieceString = tempPiece.toPrinterString();
                    String[] pieceLines = pieceString.split(Pattern.quote("\n"));
                    pieceLineList.add(pieceLines);
                }
                resultArray = pieceLineList.get(0);
                for (int lineNr = 0; lineNr < resultArray.length; lineNr++) {
                    for (String[] tempPieceLines : pieceLineList) {
                        resultArray[lineNr] = resultArray[lineNr].concat(" ").concat(tempPieceLines[lineNr]);
                    }
                }
                String tilesPrinted = String.join("\n", resultArray);
                System.out.println(tilesPrinted);
                System.out.println("");
                System.out.println("    [1]    " + " " + "    [2]    " + " " + "    [3]    " + " " + "    [4]    ");
                System.out.println("");

            }
        }
}

    public static void makeBoard(){
        clientObject.setBoard(new Board());
    }

    public static void printBoard() {
        System.out.println(clientObject.getBoard().toPrinterString());
    }

    // prints other people's tiles in a shortened way
    public static void otherTiles(String[] args) {
        System.out.println("Other players have tiles:");
        String[] middleArgs = Arrays.copyOfRange(args, 0, args.length - 2);
        for (int peerNr = 0; peerNr < middleArgs.length / 5; peerNr++) {
            String name = middleArgs[peerNr * 5];
            if (!name.equals(clientObject.getName())) {
                String[] printArgs = Arrays.copyOfRange(args, peerNr * 5 + 1, peerNr * 5 + 4);
                String printString = String.join(" ", printArgs);
                System.out.println(name + ": " + printString);
            }
        }


    }

    public static List<String> getClientTiles() {
        return clientTiles;
    }
}

package connection.client;

import game.Piece;

import java.util.Arrays;
import java.util.regex.Pattern;

public class ClientCommands {

    private static Client clientObject;

    public static void setClientObject(Client inputClientObject) {
        clientObject = inputClientObject;
    }


    public static void askTurn(String[] args) {
        String[] middleArgs = Arrays.copyOfRange(args, 0, args.length-2);
        for (int peerNr=0; peerNr < middleArgs.length/5; peerNr++){
            String name = middleArgs[peerNr*5];
            if (name.equals(clientObject.getName())) {
                String[] tileArgs = Arrays.copyOfRange(middleArgs, (peerNr * 5) + 1, (peerNr * 5) + 4);
                for (String t : tileArgs) {
                    Piece tempPiece = new Piece(t);
                    String pieceString = tempPiece.toPrinterString();
                    String[] pieceLines = pieceString.split(Pattern.quote("\n"));
                }
            }
        }
    }

    public static void askSkip(String[] args) {
        String[] middleArgs = Arrays.copyOfRange(args, 0, args.length-2);
    }
}

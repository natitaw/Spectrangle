package connection.client;

import java.util.Arrays;

public class ClientCommands {

    private static Client clientObject;

    public static void setClientObject(Client inputClientObject) {
        clientObject = inputClientObject;
    }


    public static void askTurn(String[] args) {
        String[] middleArgs = Arrays.copyOfRange(args, 0, args.length-2);
        for (int peerNr=0; peerNr < middleArgs.length/5; peerNr++){
            String[] peerArgs = Arrays.copyOfRange(middleArgs, peerNr*5, (peerNr*5)+4;
            String name = peerArgs[0];
            String[] tileStrings =
        }
    }

    public static void askSkip(String[] args) {
        String[] middleArgs = Arrays.copyOfRange(args, 0, args.length-2);
    }
}

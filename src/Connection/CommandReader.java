package Connection;

import java.util.Arrays;
import java.util.List;

public class CommandReader {

    public enum State {
        CLIENT, SERVER
    }

    public State readerState;

    public CommandReader(State stateInput){
        this.readerState = stateInput;
    }

    public void read(String inputString, Peer peer) {
        // seperate by spaces
        // get first command
        String[] seperateWords = inputString.split("\\s+");
        String command = seperateWords[0];
        String [] args = Arrays.copyOfRange(seperateWords, 1, seperateWords.length);

        if (this.readerState==State.CLIENT){
            switch (command) {
                //TODO Make case for 4 or 8 whitespaces
                case "waiting":
                    //
                    break;
                case "start":
                    //
                    // THIS IS GOING TO HAVE "with"
                    //
                    break;
                case "order":
                    //
                    break;

                case "replace":
                    //
                    break;
                case "move":
                    //
                    break;
                case "chat":
                    //S
                    break;
                default:
                    if (Settings.debug) {
                        System.out.println("Server sent a command that was ignored:");
                        System.out.println(inputString);
                    }
                    break;
            }
        } else if (this.readerState==State.SERVER) {
            switch (command) {
                //TODO Make case for 4 or 8 whitespaces
                case "connect":
                    ServerCommands.clientConnects(args,peer);
                    break;
                case "request":
                    //
                    break;
                case "place":
                    //
                    break;
                case "skip":
                    //
                    break;
                case "exchange":
                    //
                    break;
                case "move":
                    //
                    break;
                case "chat":
                    ServerCommands.sendChat(args, peer);
                    break;
                default:
                    if (Settings.debug) {
                        System.out.println(peer.getName() + " sent a command that was ignored:");
                        System.out.println(inputString);
                    }
                    break;
            }
        }
    }
}



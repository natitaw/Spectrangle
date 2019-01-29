package connection;

import connection.server.GameRoom;
import connection.server.Server;
import connection.server.ServerCommands;

import java.util.Arrays;

public class CommandInterpreter {

    public ClientOrServer parent;
    public ClientOrServer.Type parentType;

    public CommandInterpreter(ClientOrServer parentInput){
        this.parent = parentInput;
        this.parentType=parent.getType();
        if (parentType== ClientOrServer.Type.SERVER){
            ServerCommands.setServerObject((Server) parent);
        }
    }

    public void read(String inputString, Peer peer) {
        // seperate by spaces
        // get first command
        String[] seperateWords = inputString.split("\\s+");
        String command = seperateWords[0];
        String[] args = Arrays.copyOfRange(seperateWords, 1, seperateWords.length);

        if (this.parentType== ClientOrServer.Type.CLIENT){
            switch (command) {
                //TODO Make case for 4 or 8 whitespaces
                case "welcome":
                    System.out.print("Name change acknowledged");
                    if (args.length > 1 && args[1].equals("chat")){
                        System.out.print(", and chat has been enabled.\n");
                    } else {
                        System.out.print(".\n");
                    }
                    break;
                case "waiting":
                    System.out.println("Waiting for game with requested amount of players.");
                    System.out.println("Players in queue: " + String.join(", ", args));
                    break;
                case "start":
                    if (args[0].equals("with")){
                        String[] newargs = Arrays.copyOfRange(args, 1, args.length);

                        TerminalInputHandler.clearScreen();
                        System.out.println("Starting new game with: " + String.join(", ", newargs));
                    }
                    break;
                case "order":
                    System.out.println("Order of turns: " + String.join(", ", args));
                    break;
                case "tiles":
                    // TODO change terminalinput state to NOT_YOUR_TURN or to YOUR_TURN
                    // TODO if terminalinputhandler is waiting on something else, interrupt with YourTurnException and switch state to YOUR_TURN
                    // TODO print tiles
                    break;
                case "replace":
                    // sent if a player exchanged one of their tiles for one in the bag
                    break;
                case "skip":
                    // sent if player is allowed to skip? maybe just include in tiles
                    break;
                case "player":
                    // player left
                    // player skipped
                    break;
                case "move":
                    // sent if player did a move
                    break;
                case "game":
                    // game finished
                    break;
                case "chat":
                    String sender = args[0];
                    String[] messageArray = Arrays.copyOfRange(args, 1, args.length);
                    String message = String.join(" ", messageArray);
                    System.out.println(sender + ": " + message);
                    break;
                case "invalid":
                    //
                    break;
                default:
                    if (Settings.debug) {
                        System.out.println(peer.getName() + " sent an unknown command that was ignored: " + inputString);
                    }
                    break;
            }
        } else if (this.parentType== ClientOrServer.Type.SERVER) {
            switch (command) {
                //TODO Make case for 4 or 8 whitespaces
                case "connect":
                    ServerCommands.clientConnects(args,peer);
                    break;
                case "request":
                    ServerCommands.clientRequests(args,peer);
                    break;
                case "place":
                        if (args[1].equals("on")) {
                            if (peer.getCurrentRoom() instanceof GameRoom) {
                                ((GameRoom) peer.getCurrentRoom()).checkPlace(peer,args[1],Integer.parseInt(args[3]));
                            }
                        }

                    break;
                case "skip":
                            if (peer.getCurrentRoom() instanceof GameRoom) {
                                ((GameRoom) peer.getCurrentRoom()).checkSkip(peer);
                            }
                    break;
                case "exchange":
                    if (args.length > 0) {
                        if (peer.getCurrentRoom() instanceof GameRoom) {
                            ((GameRoom) peer.getCurrentRoom()).checkExchange(peer,args[0]);
                        }
                    }
                    break;
                case "chat":
                    ServerCommands.sendChat(args, peer);
                    break;
                default:
                    if (Settings.debug) {
                        System.out.println(peer.getName() + " sent an unknown command that was ignored: " + inputString);
                    }
                    break;
            }
        }
    }
}



package connection;

import connection.client.Client;
import connection.client.ClientCommands;
import connection.server.GameRoom;
import connection.server.Server;
import connection.server.ServerCommands;
import game.Piece;

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
                        ClientCommands.makeBoard();
                    }
                    break;
                case "order":
                    System.out.println("Order of turns: " + String.join(", ", args));
                    break;
                case "tiles":

                    if (args[args.length-1].equals(parent.getName()) ) {

                        if (args[args.length-2].equals("skip") ) {
                            ((Client) parent).getTerminalInputHandler().setState(TerminalInputHandler.InputState.SKIP);


                        } else {
                            ((Client) parent).getTerminalInputHandler().setState(TerminalInputHandler.InputState.TURN);
                        }
                        ClientCommands.printBoard();
                        ClientCommands.otherTiles(args);
                        ClientCommands.printTiles(args);
                        ((Client) parent).getTerminalInputHandlerThread().interrupt();


                    }
                    break;

                case "player":
                    if (args[0].equals("skipped")){
                        System.out.print("Player " + args[1] + " skipped turn.");
                    } else if (args[1].equals("left")){
                        TerminalInputHandler.clearScreen();
                        System.out.println("Player " + args[0] + " left mid-game. Returned to lobby");
                    }
                    break;
                case "replace":
                    System.out.println(args[0] + " replaced tile " + args[1] +" with new tile:" + args[3]);
                    break;
                case "move":
                    System.out.println(args[0] + " placed tile " + args[1] + " on position" + args[2] + ", earning " + args[3] + " points.");
                    ((Client) parent).getBoard().movePiece(Integer.parseInt(args[2]),new Piece(args[1]));
                    break;
                case "game":
                    if (args[0].equals("finished")){
                        TerminalInputHandler.clearScreen();
                        System.out.println("Game finished");
                        System.out.println("Scoreboard:");
                        for (int i = 1; i < ((args.length-1)/2); i++){
                            System.out.println(args[i] + ": " + args[i+1]);
                        }
                    }

                    break;
                case "chat":
                    String sender = args[0];
                    String[] messageArray = Arrays.copyOfRange(args, 1, args.length);
                    String message = String.join(" ", messageArray);
                    System.out.println(sender + ": " + message);
                    break;
                case "invalid":
                    System.out.println(inputString);
                    break;
                default:
                    if (Settings.debug) {
                        System.out.println(peer.getName() + " sent an unknown command that was ignored: " + inputString);
                    }
                    break;
            }
        } else if (this.parentType== ClientOrServer.Type.SERVER) {
            switch (command) {
                case "connect":
                    if (args.length > 0) {
                        ServerCommands.clientConnects(args, peer);
                    }
                    break;
                case "request":
                    if (args.length > 0){
                    ServerCommands.clientRequests(args, peer);
                }
                    break;
                case "place":
                    if (args.length > 1) {
                        if (args[1].equals("on")) {
                            if (peer.getCurrentRoom() instanceof GameRoom) {
                                ((GameRoom) peer.getCurrentRoom()).checkPlace(peer, args[0], Integer.parseInt(args[2]));
                            }
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
                        ((Server) parent).printer.println(peer.getName() + " sent an unknown command that was ignored: " + inputString);
                    }
                    break;
            }
        }
    }
}



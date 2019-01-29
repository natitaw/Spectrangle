package connection;

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
                //TODO Make case for wrong command
                case "waiting":
                    System.out.println("Waiting for game with requested amount of players.");
                    System.out.println("Players in queue: " + String.join(", ", args));
                    break;
                case "start":
                    if (args[0].equals("with")){
                        String[] newargs = Arrays.copyOfRange(args, 1, args.length);

                        System.out.println("Starting new game with: " + String.join(", ", newargs));
                    }
                    break;
                case "order":
                    //
                    break;
                case "welcome":
                    System.out.print("Name change acknowledged");
                    if (args.length > 1 && args[1].equals("chat")){
                        System.out.print(", and chat has been enabled.\n");
                    } else {
                        System.out.print(".\n");
                    }
                    break;
                case "replace":
                    //
                    break;
                case "move":
                    //
                    break;
                case "chat":
                    //
                    //
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
                        System.out.println(peer.getName() + " sent an unknown command that was ignored: " + inputString);
                    }
                    break;
            }
        }
    }
}



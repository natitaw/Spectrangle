package Connection;

import java.util.Arrays;

public class ServerCommands {


    public static void clientConnects(String[] args, Peer peer){
        if (args.length > 2) {
            peer.sendMessage("invalid name"); // TODO check if this is correct usage
        } else {
        String name = args[0];



        // TODO Check if name is duplicate of existing name

        // Set name

        System.out.println(peer.getName() + " changed name to " + name);
        peer.setName(name);

        //Check the second argument, if it exists
        if (args.length == 2) {
            if (args[1].equals("chat") ){
                peer.setChatEnabled(true);
                System.out.println(name + " enabled chat");
            } else {
                peer.sendMessage("invalid name");
            }
        }
        }

        // Put client in lobby




    }

    public static void sendChat(String[] args, Peer sendingPeer, Server serverObject) {
        if (sendingPeer.getChatEnabled()) {
            int room=sendingPeer.getCurrentRoom();
            String content = String.join(" ", args);
            String command = "chat "+ sendingPeer.getName() + " " + content;
            serverObject.sendMessageToRoom(command, room, "chat");
        } else {
            sendingPeer.sendMessage("You do not have chat enabled");
        }
    }
}

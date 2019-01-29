package Connection;

public class ServerCommands {


    public static void clientConnects(String[] args, Peer peer){
        String name = args[0];



        // TODO Check if name is duplicate of existing name

        // Set name

        System.out.println(peer.getName() + "changed name to " + name);
        peer.setName(name);

        //Check the second argument, if it exists
        if (args.length >= 2) {
            if (args[1].equals("chat") ){
                peer.setChatEnabled(true);
                System.out.println(name + " enabled chat");
            }
        }

        // Put client in lobby




    }

    public static void sendChat(String[] args, Peer sendingPeer) {
        if (sendingPeer.getChatEnabled()) {
            int room=sendingPeer.getCurrentRoom();

            // TODO check what players have chat enabled in player's room
            // TODO send peer.name and args to them
        } else {
            sendingPeer.sendMessage("You do not have chat enabled");
        }
    }
}

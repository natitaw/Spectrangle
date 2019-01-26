package Connection;

public class ServerCommands {


    public static void clientConnects(String[] args, Peer peer){
        String name = args[0];



        // TODO Check if name is duplicate of existing name

        // Set name
        peer.setName(name);

        //Check the second argument, if it exists
        if (args.length >= 2) {
            if (args[1].equals("chat") ){
                peer.setChatEnabled(true);
            }
        }

        // Put client in lobby




    }

    public static void sendChat(String[] args, Peer peer) {
        if (peer.getChatEnabled()) {
            // TODO check what players have chat enabled
            // TODO send peer.name and args to them
        } else {
            peer.sendMessage("You do not have chat enabled");
        }
    }
}

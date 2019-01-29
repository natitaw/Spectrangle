package Connection;

import java.util.ArrayList;
import java.util.List;

public class ServerCommands {

    private static Server serverObject;

    public static void setServerObject(Server inputServerObject) {
        serverObject = inputServerObject;
    }

    public static void clientConnects(String[] args, Peer peer) {
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
                if (args[1].equals("chat")) {
                    peer.setChatEnabled(true);
                    peer.sendMessage("welcome chat");
                    System.out.println(name + " enabled chat");
                } else {
                    peer.sendMessage("invalid name");
                }
            } else {
                peer.sendMessage("welcome");
            }
        }

        // Put client in lobby


    }

    public static void sendChat(String[] args, Peer sendingPeer) {
        if (sendingPeer.getChatEnabled()) {
            Room room = sendingPeer.getCurrentRoom();
            String content = String.join(" ", args);
            String command = "chat " + sendingPeer.getName() + " " + content;
            serverObject.sendMessageToRoom(command, room, "chat");
        } else {
            sendingPeer.sendMessage("You do not have chat enabled");
        }
    }

    public static void clientRequests(String[] args, Peer peer) {
        int preferredNrOfPlayers = Integer.parseInt(args[0]);
        peer.setPreferredNrOfPlayers(preferredNrOfPlayers);
        if (preferredNrOfPlayers >= 1 && preferredNrOfPlayers <= 4) {
            List<Peer> peerList = serverObject.getPeerList();


            boolean endOfList = false;
            int nrOfMatchingPlayers = 0;
            List<Peer> matchingPeerList = new ArrayList<>();
            while (!endOfList) {
                for (Peer p : peerList) {
                    if (p == peerList.get(peerList.size() - 1)) {
                        endOfList = true;
                    }
                    if (p.getCurrentRoom().getRoomNumber() == 0 && p.getPreferredNrOfPlayers() == preferredNrOfPlayers) {
                        nrOfMatchingPlayers++;
                        matchingPeerList.add(p);
                        if (nrOfMatchingPlayers == preferredNrOfPlayers) {
                            startGame(matchingPeerList);
                            break;
                        }
                    }
                }

            }
        } else {
            peer.sendMessage("invalid command");
        }

        // check how many other players are also waiting for this amount in lobby (room 0)
        // if amount==amount, execute "start with"
    }

    public static void startGame(List<Peer> peerList) {
        for (Peer p : peerList) {
            p.setPreferredNrOfPlayers(0);
            // TODO create server RoomList somewhere else?
            // TODO create new room with a number one higher than already used

        }
    }


}

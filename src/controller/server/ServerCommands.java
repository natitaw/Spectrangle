package controller.server;

import controller.Peer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ServerCommands {

    private static Server serverObject;

    public static void setServerObject(Server inputServerObject) {
        serverObject = inputServerObject;
    }

    public static void clientConnects(String[] args, Peer peer) {
        if (args.length > 2) {
            peer.sendMessage("invalid name");
        } else {
            String name = args[0];


            // Check if name is duplicate of existing name
            boolean nameIsStillUnique = true;
            Iterator itr = serverObject.getPeerList().iterator();
            Peer p;
            while (itr.hasNext() && nameIsStillUnique) {
                p = (Peer) itr.next();
                String otherPeerName = p.getName();
                if (otherPeerName.equals(name)) {
                    nameIsStillUnique = false;
                }
            }

            // Set name
            if (nameIsStillUnique) {
                serverObject.getPrinter().println(peer.getName() + " changed name to " + name);
                peer.setName(name);

                //Check the second argument, if it exists
                if (args.length == 2) {
                    if (args[1].equals("chat")) {
                        peer.setChatEnabled(true);
                        peer.sendMessage("welcome chat");
                        serverObject.getPrinter().println(name + " enabled chat");
                    } else {
                        peer.sendMessage("invalid name");
                    }
                } else {
                    peer.sendMessage("welcome");
                }
            } else {
                peer.sendMessage("invalid name");
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

        if (preferredNrOfPlayers >= 1 && preferredNrOfPlayers <= 4) {
            peer.setPreferredNrOfPlayers(preferredNrOfPlayers);

            sendWaiting(peer);


            List<Peer> peerList = serverObject.getPeerList();

            //Find other matching players and maybe start some games
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
                            createGame(matchingPeerList);
                            break;
                        }
                    }
                }

            }
        } else {
            peer.sendMessage("invalid command");
        }

    }

    private static void sendWaiting(Peer peer) {
        Room lobby = serverObject.getRoomList().get(0);
        List<Peer> lobbyPeers = lobby.getPeerList();
        List<String> waitingList = new ArrayList<>();
        for (Peer otherPeer : lobbyPeers) {
            if (otherPeer.getPreferredNrOfPlayers() == peer.getPreferredNrOfPlayers()) {
                waitingList.add(otherPeer.getName());
            }
        }
        String arg = String.join(" ", waitingList);
        peer.sendMessage("waiting " + arg);
    }

    private static void createGame(List<Peer> peerList) {

        GameRoom gameRoom = serverObject.newGameRoom();
        for (Peer p : peerList) {
            p.setPreferredNrOfPlayers(0);
            p.moveToRoom(gameRoom);
        }

        // send start with message
        List<String> nameList = peerList.stream().map(Peer::getName).collect(Collectors.toList());  // used some java 8 lambda / list comprehension magic here
        String arg = String.join(" ", nameList);
        String command = "start with " + arg;
        serverObject.sendMessageToRoom(command, gameRoom, "start with");
        Thread gameroomThread = new Thread(gameRoom);
        gameroomThread.start();
    }

}

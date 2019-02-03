package controller.server;

import controller.Peer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ServerCommands class. Contains some static commands that can be executed on the server.
 * Are usually called from the CommandInterpreter.
 *
 * @author Bit 4 - Group 4
 */
public class ServerCommands {

    private static Server serverObject;

    /**
     * ServerCommands constructor.
     *
     * @author Bit 4 - Group 4
     * @param inputServerObject Parent server object.
     */
    public static void setServerObject(Server inputServerObject) {
        serverObject = inputServerObject;
    }

    /**
     * Method to be exected if a client executes the "connect" command.
     * Changes client's name and (maybe) enables chat.
     *
     * @author Bit 4 - Group 4
     * @param args Arguments sent after the "connect" command by the client
     * @param peer The peer that sent the command
     */
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

            // Set name if it's unique
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
                        // If second argument is not chat, something is wrong.
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

    /**
     * method used when a peer tries to send chat. Sends chat to all peers in the room
     * that want to receive chat.
     *
     * @author Bit 4 - Group 4
     * @param args Arguments sent after the "connect" command by the client
     * @param sendingPeer The peer that sent the command
     */
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

    /**
     * method used when a peer tries sends the "request" command.
     * Sends the appropriate "waiting" command with sendWaiting.
     * Checks if other players are waiting for games with the same amount of players.
     * If so, runs createGame. Repeats this until there are no new games that can be created.
     *
     * @author Bit 4 - Group 4
     * @param args Arguments sent after the "connect" command by the client
     * @param peer The peer that sent the command
     */
    public static void clientRequests(String[] args, Peer peer) {
        int preferredNrOfPlayers = Integer.parseInt(args[0]);

        if (preferredNrOfPlayers >= 2 && preferredNrOfPlayers <= 4) {
            peer.setPreferredNrOfPlayers(preferredNrOfPlayers);

            sendWaiting(peer);


            List<Peer> peerList = serverObject.getPeerList();

            //Find other matching players and maybe start some games
            boolean endOfList = false;
            int nrOfMatchingPlayers = 0;
            List<Peer> matchingPeerList = new ArrayList<>();

            // Start loop
            while (!endOfList) {

                // Check the full peerList from start to end
                for (Peer p : peerList) {
                    // If we reached the end of the list without starting a game, end the loop
                    if (p == peerList.get(peerList.size() - 1)) {
                        endOfList = true;
                    }
                    // If another player is waiting for a game of the same size
                    if (p.getCurrentRoom().getRoomNumber() == 0 && p.getPreferredNrOfPlayers() == preferredNrOfPlayers) {
                        // Increase the number of matching players
                        nrOfMatchingPlayers++;
                        // Add this peer to list of matching peers
                        matchingPeerList.add(p);
                        // If the amount of waiting peers equals the game size
                        if (nrOfMatchingPlayers == preferredNrOfPlayers) {
                            // Start game
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

    /**
     * Method used when a peer has sent the "request" command and the amount they sent is valid
     * Sends to them which other peers are waiting for a game of the same size
     *
     * @author Bit 4 - Group 4

     * @param peer The peer that sent the command
     */
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

    /**
     * method used when X players are waiting for a game of size X
     * Creates a new GameRoom. Fills it with these players.
     * Sends them the "start with" command with the list of all playernames in this room.
     * Start a new thread on this room to handle all game logic.
     *
     * @author Bit 4 - Group 4
     * @param peerList The list of peers to be included in this game
     */
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

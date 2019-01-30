package connection;

import connection.client.Client;
import connection.client.ClientCommands;
import game.Piece;
import jdk.internal.util.xml.impl.Input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static connection.TerminalInputHandler.InputState.*;

public class TerminalInputHandler implements Runnable{
    ClientOrServer parent;
    private boolean running = true;
    private InputState state = InputState.COMMAND;
    private String name;
    private boolean wantsChat;
    Piece tempPiece = null;
    public boolean interrupted = false;



    public TerminalInputHandler(ClientOrServer inputParent) {
        this.parent=inputParent;
        name = null;
        wantsChat = false;
        if (parent.getType()== ClientOrServer.Type.CLIENT) {
            state=NAME;
        }
    }

    public TerminalInputHandler(ClientOrServer inputParent, InputState firstState) {
        this(inputParent);
        state=firstState;
    }

    public enum InputState {
        COMMAND, NAME, CHAT_PREFERENCE, NUMBER_OF_PLAYERS, SINGLEPLAYER, TURN, TURN2, SKIP, AI, AI_NAME, AI_NUMBER_OF_PLAYERS, AI_TURN, AI_SKIP;
    }



    /**
     * Uses readers to read string from console
     */
    public String readString() {

        String antw = null;
        while (antw==null && !interrupted) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                    System.in));
                if (System.in.available() > 0) {
                    antw = in.readLine();
                }
            } catch (IOException e) {
                if (running && parent.getRunning()) {
                    System.out.println("Error in reading message from terminal");
                    e.printStackTrace();
                }
            }
        }


        return (antw == null) ? "" : antw;
    }

    public void setState(InputState state) {
        this.state = state;
    }

    /**
     * Reads a string from the console and sends this string over
     * the socket-connection to the peer.
     * On connection.Peer.EXIT the process ends
     */
    @Override
    public void run() {
        while (parent.getRunning() && running) {
            String s = "";
            switch (state) {
                case AI:
                    while (!interrupted){
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    interrupted=false;
                    break;
                case AI_NAME:
                    parent.sendMessageToAll("connect " + parent.getName());
                    state=AI_NUMBER_OF_PLAYERS;
                    break;
                case AI_NUMBER_OF_PLAYERS:
                    parent.sendMessageToAll("request " + ((Client) parent).prefNrPlayers);
                    break;
                case AI_TURN:
                    ClientCommands.aiTurn();

                    break;
                case AI_SKIP:
                    ClientCommands.aiSkip();
                    break;
                case SINGLEPLAYER:
                    this.name = "Player";
                    ((Client) parent).setName(this.name);
                    wantsChat=false;
                    parent.sendMessageToAll("connect " + this.name);
                    state=NUMBER_OF_PLAYERS;
                    break;

                case NAME:
                    System.out.println("Please enter your desired name");
                    s = readString();
                    this.name = s;
                    ((Client) parent).setName(s);
                    state=CHAT_PREFERENCE;
                    break;
                case CHAT_PREFERENCE:
                    boolean hasChosen = false;

                    while (!hasChosen) {
                        System.out.println("Would you like to enable chat? Y/N");
                        s = readString();

                        if (s.equals("Y") || s.equals("y") || s.equals("yes") || s.equals("Yes")) {
                            wantsChat = true;
                            hasChosen=true;
                        } else if (s.equals("N") || s.equals("n") || s.equals("no") || s.equals("No")) {
                            wantsChat = false;
                            hasChosen=true;
                        } else {
                            System.out.println("Please type a correct response");
                        }

                    }

                    if (!s.equals("EXIT")) {
                        if (wantsChat) {
                            parent.sendMessageToAll("connect " + name + " chat");
                        } else {
                            parent.sendMessageToAll("connect " + name);
                        }
                    }
                    state=NUMBER_OF_PLAYERS;

                    break;
                case NUMBER_OF_PLAYERS:
                    System.out.println("If you'd like to play a game, just type \"request <number>\"");
                    System.out.println("Where <number> is the amount of players to play with, from 1-4");
                    if (wantsChat) {
                        System.out.println("Type \"chat <your message> \" to chat with others");
                    }

                    s = readString();
                    if (!s.equals("EXIT")) {
                        parent.sendMessageToAll(s);
                    }
                    state=COMMAND;

                    break;
                case TURN:
                    boolean inputFinished = false;
                    while (!inputFinished) {
                        try {
                            System.out.println("Type the number of the tile you would like to place");
                            s = readString();
                            tempPiece = new Piece(ClientCommands.getClientTiles().get(Integer.parseInt(s) - 1));
                            inputFinished=true;
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace(); // TODO Fix exception here
                        }
                    }
                    state=TURN2;
                    break;
                case TURN2:
                        try {
                            System.out.println("Type the index of the board where you would like to place the tile");
                            s = readString();
                            if (((Client) parent).getBoard().isValidMove(Integer.parseInt(s), tempPiece)) {
                                parent.sendMessageToAll("place " + tempPiece.toString() + " on " + s);
                            } else {
                                state=TURN;
                            }
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }

                    state=COMMAND;
                    break;
                case SKIP:
                    boolean moveFinished2 = false;
                    while (!moveFinished2) {
                        try {
                            System.out.println("You have no valid moves.");
                            System.out.println("Type S to skip turn, or type a number above to exchange one of your tiles");
                            s = readString();
                            if (s.equals("S") || s.equals("s") || s.equals("Skip") || s.equals("skip")) {
                                parent.sendMessageToAll("skip");
                                moveFinished2 = true;
                            } else if (s.equals("EXIT")) {

                            } else {
                                Piece tileToReplace = new Piece(ClientCommands.getClientTiles().get(Integer.parseInt(s) - 1));
                                parent.sendMessageToAll("exchange " + tileToReplace.toString());
                                moveFinished2 = true;
                            }
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace(); // TODO Fix exception here
                        }
                    }
                    state=COMMAND;
                    break;
                default:
                        s = readString();
                        if (!s.equals("EXIT")) {
                            if (!interrupted)
                            parent.sendMessageToAll(s);
                        }
                    interrupted=false;
                    break;

            }
            if (s.equals("EXIT")) {
                parent.shutDown();
                running = false;
            }
        }
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

}

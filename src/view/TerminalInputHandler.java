package view;

import controller.ClientOrServer;
import controller.client.Client;
import controller.client.ClientCommands;
import model.Piece;
import model.TileBag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static view.TerminalInputHandler.InputState.*;

/**
 * Class used for handling terminal input. Implements runnable so a Thread can be started on it
 * This is used so that the rest of the program can run in parallel to terminal input
 */
public class TerminalInputHandler implements Runnable {
    private final ClientOrServer parent;
    private boolean running = true;
    private InputState state = InputState.COMMAND;
    private String name;
    private boolean wantsChat;
    private Piece tempPiece = null;
    private boolean interrupted = false;
    private int tempPieceIndex = 0;

    /**
     * Constructor. Sets fields as specified in parameters.
     * If parent is a server, starts in the COMMAND state.
     * If it's a client, start in NAME state.
     * @param inputParent The parent of this object.
     */
    public TerminalInputHandler(ClientOrServer inputParent) {
        this.parent = inputParent;
        name = null;
        wantsChat = false;
        if (parent.getType() == ClientOrServer.Type.CLIENT) {
            state = NAME;
        }
    }

    /**
     * Constructor that sets the fields as specified in parameters
     * Also sets the state as specified in parameters.
     * @param inputParent parent of this object
     * @param firstState state this handler should start in.
     */
    public TerminalInputHandler(ClientOrServer inputParent, InputState firstState) {
        this(inputParent);
        state = firstState;
    }

    /**
     * Clears the terminal (does not work in IntelliJ run/debug windows)
     * @param parent Parent to clear the screen of
     */
    public static void clearScreen(ClientOrServer parent) {
        parent.getPrinter().print("\033[H\033[2J");
        parent.getPrinter().flush();
    }

    /**
     * If there is unread data in terminal:
     * tries to read a string from the terminal, and return it
     * Otherwise it just waits around
     */
    private String readString() {

        String antw = null;
        while (antw == null && !interrupted) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                    System.in));
                if (System.in.available() > 0) {
                    antw = in.readLine();
                }
            } catch (IOException e) {
                if (running && parent.getRunning()) {
                    parent.getPrinter().println("Error in reading message from terminal");
                    e.printStackTrace();
                }
            }
        }


        return (antw == null) ? "" : antw;
    }

    /**
     * Set the state of this handler
     * @param state the new state
     */
    public void setState(InputState state) {
        this.state = state;
    }

    /**
     * Unless this thread and its parents are shutting down:
     * Checks the current state of the handler
     * Depending on this state, prints different things on the terminal
     * And runs different methods
     */
    @Override
    public void run() {
        while (parent.getRunning() && running) {
            String s = "";
            switch (state) {
                case AI:
                    // Default AI state: just waiting until its turn
                    while (!interrupted) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    this.interrupted = false;
                    break;
                case AI_NAME:
                    // State AI starts in. Makes it send the connect command.
                    parent.sendMessageToAll("connect " + parent.getName());
                    state = AI_NUMBER_OF_PLAYERS;
                    break;
                case AI_NUMBER_OF_PLAYERS:
                    // Sends the request command
                    parent.sendMessageToAll("request " + ((Client) parent).getPrefNrPlayers());
                    state = AI;
                    break;
                case AI_TURN:
                    // AI has a turn: runs the following method
                    ClientCommands.aiTurn(((Client) parent));
                    state = AI;
                    break;
                case AI_SKIP:
                    // AI must skip: runs the following method
                    ClientCommands.aiSkip(((Client) parent));
                    state = AI;
                    break;
                case SINGLEPLAYER:
                    // Default starting state for singleplayer. Already sends the connect and
                    // request commands. Then goes to COMMAND state
                    wantsChat = false;
                    parent.sendMessageToAll("connect " + parent.getName());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    parent.sendMessageToAll("request " + String.valueOf(((Client) parent).getPrefNrPlayers()));
                    state = COMMAND;
                    break;
                case NAME:
                    // Starting state for multiplayer. Asks player for name.
                    parent.getPrinter().println("Please enter your desired name");
                    s = readString();
                    this.name = s;
                    parent.setName(s);
                    state = CHAT_PREFERENCE;
                    break;
                case CHAT_PREFERENCE:
                    // Asks player for chat preference and sends connect command.
                    boolean hasChosen = false;
                    while (!hasChosen) {
                        parent.getPrinter().println("Would you like to enable chat? Y/N");
                        s = readString();

                        if (s.equals("Y") || s.equals("y") || s.equals("yes") || s.equals("Yes")) {
                            wantsChat = true;
                            hasChosen = true;
                        } else if (s.equals("N") || s.equals("n") || s.equals("no") || s.equals("No")) {
                            wantsChat = false;
                            hasChosen = true;
                        } else {
                            parent.getPrinter().println("Please type a correct response");
                        }

                    }

                    if (!s.equals("EXIT")) {
                        if (wantsChat) {
                            parent.sendMessageToAll("connect " + name + " chat");
                        } else {
                            parent.sendMessageToAll("connect " + name);
                        }
                    }
                    state = NUMBER_OF_PLAYERS;

                    break;
                case NUMBER_OF_PLAYERS:
                    // Asks player for preferred nr of players and sends the request command.
                    parent.getPrinter().println("If you'd like to play a game, just type \"request <number>\"");
                    parent.getPrinter().println("Where <number> is the amount of players to play with, from 1-4");
                    if (wantsChat) {
                        parent.getPrinter().println("Type \"chat <your message> \" to chat with others");
                    }

                    s = readString();
                    if (!s.equals("EXIT")) {
                        parent.sendMessageToAll(s);
                    }
                    state = COMMAND;

                    break;
                case TURN:
                    // If player has a turn, lets them input their preferred tile to do something with
                    PiecePrinter.printTiles((Client) parent);
                    boolean inputFinished = false;
                    while (!inputFinished) {
                        try {
                            parent.getPrinter().println("Type the number of the tile you would like to place (or rotate)");
                            parent.getPrinter().println("Or type \"hint\" for a hint");
                            s = readString();
                            if (s.equals("hint") || s.equals("Hint") || s.equals("h") || s.equals("H")) {
                                parent.getPrinter().println(ClientCommands.bestMove(
                                    TileBag.generateBag(((Client) parent).getClientTiles()),
                                    (Client) parent));
                                state = TURN;
                            } else {
                                tempPieceIndex = Integer.parseInt(s) - 1;
                                tempPiece = new Piece(((Client) parent).getClientTiles().get(tempPieceIndex));
                                inputFinished = true;
                                state = TURN2;
                            }

                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                            state = TURN;
                        }
                    }

                    break;
                case TURN2:
                    // Lets player rotate their tile or place it somewhere
                    try {
                        parent.getPrinter().println("Type the index of the board where you would like to place the tile");
                        parent.getPrinter().println("Or type R to rotate clockwise one. Type RR to rotate clockwise twice");
                        s = readString();
                        if (s.equals("R") || s.equals("r")) {
                            tempPiece.rotate();
                            ((Client) parent).getClientTiles().set(tempPieceIndex, tempPiece.toString());

                            state = TURN;
                        } else if (s.equals("RR") || s.equals("rr")) {
                            tempPiece.rotate2x();
                            ((Client) parent).getClientTiles().set(tempPieceIndex, tempPiece.toString());
                            state = TURN;
                        } else if (((Client) parent).getBoard().isValidMove(Integer.parseInt(s), tempPiece)) {
                            parent.sendMessageToAll("place " + tempPiece.toString() + " on " + s);
                            state = COMMAND;
                        } else {
                            parent.getPrinter().println(" --- INVALID MOVE --- ");
                            state = TURN;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                        state = TURN;
                    }


                    break;
                case SKIP:
                    // Lets player exchange their tile or skip
                    boolean moveFinished2 = false;
                    while (!moveFinished2) {
                        try {
                            parent.getPrinter().println("You have no valid moves.");
                            parent.getPrinter().println("Type S to skip turn, or type a number above to exchange one of your tiles");
                            s = readString();
                            if (s.equals("S") || s.equals("s") || s.equals("Skip") || s.equals("skip")) {
                                parent.sendMessageToAll("skip");
                                moveFinished2 = true;
                            } else if (s.equals("EXIT")) {

                            } else {
                                Piece tileToReplace = new Piece(((Client) parent).getClientTiles().get(Integer.parseInt(s) - 1));
                                parent.sendMessageToAll("exchange " + tileToReplace.toString());
                                moveFinished2 = true;
                            }
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                    state = COMMAND;
                    break;
                default:
                    // Also known as COMMAND State
                    // Just sends commands to the server straight from terminal
                    s = readString();
                    if (!s.equals("EXIT")) {
                        if (!interrupted) {
                            parent.sendMessageToAll(s);
                        }
                    }
                    interrupted = false;
                    break;

            }
            if (s.equals("EXIT")) {
                parent.shutDown();
                running = false;
            }
        }
    }

    /**
     * Setter for interrupted field
     * @param interrupted new value of interrupted field
     */
    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    /**
     * The different states of this handler as specified in other methods
     */
    public enum InputState {
        COMMAND, NAME, CHAT_PREFERENCE, NUMBER_OF_PLAYERS, SINGLEPLAYER, TURN, TURN2, SKIP, AI, AI_NAME, AI_NUMBER_OF_PLAYERS, AI_TURN, AI_SKIP
    }

}

package connection;

import jdk.internal.util.xml.impl.Input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static connection.TerminalInputHandler.InputState.NAME;

public class TerminalInputHandler implements Runnable{
    ClientOrServer parent;
    private boolean running = true;
    public InputState state = InputState.COMMAND;
    private String name;
    private boolean wantsChat;



    public TerminalInputHandler(ClientOrServer inputParent) {
        this.parent=inputParent;
        name = null;
        wantsChat = false;
    }

    public enum InputState {
        COMMAND, NAME, CHAT_PREFERENCE, NUMBER_OF_PLAYERS
    }



    /**
     * Uses readers to read string from console
     */
    public String readString() {

        String antw = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                System.in));
            antw = in.readLine();
        } catch (IOException e) {
            if (running && parent.getRunning()){
                System.out.println("Error in reading message from terminal");
                e.printStackTrace();
            }
        }


        return (antw == null) ? "" : antw;
    }

    /**
     * Reads a string from the console and sends this string over
     * the socket-connection to the peer.
     * On connection.Peer.EXIT the process ends
     */
    @Override
    public void run() {

        // TODO change states somewhere else
        while (parent.getRunning() && running) {
            String s = "";
            switch (state) {

                case NAME:
                    System.out.println("Please enter your desired name");
                    s = readString();
                    this.name = s;
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
                    break;
                default:
                       s = readString();
                    if (!s.equals("EXIT")) {
                        parent.sendMessageToAll(s);
                    }
                    break;

            }
            if (s.equals("EXIT")) {
                parent.shutDown();
                running = false;
            }
        }
    }
}

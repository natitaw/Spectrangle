package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TerminalInputHandler implements Runnable{
    ClientOrServer parent;
    private boolean running = true;
    public TerminalInputHandler(ClientOrServer inputParent) {
        this.parent=inputParent;
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
        while (parent.getRunning() && running) {
            String s = readString();
            if (s.equals("EXIT")) {
                parent.shutDown();
                running=false;
            } else {
                parent.sendMessageToAll(s);
            }
        }
    }
}

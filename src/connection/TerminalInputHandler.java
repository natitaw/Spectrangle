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



    public String readString(String tekst) {
        System.out.print(tekst);
        String antw = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                System.in));
            antw = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return (antw == null) ? "" : antw;
    }

    @Override
    public void run() {
        while (parent.getRunning() && running) {
            String s = readString("");
            if (s.equals("EXIT")) {
                parent.shutDown();
                running=false;
            } else {
                parent.sendMessageToAll(s);
            }
        }
    }
}

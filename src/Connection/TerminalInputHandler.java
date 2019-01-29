package Connection;

public class TerminalInputHandler implements Runnable{
    ClientOrServer parent;
    public TerminalInputHandler(ClientOrServer inputParent) {
        this.parent=inputParent;
    }

    @Override
    public void run() {
        while (parent.getRunning()) {
            String s = Peer.readString("");
            if (s.equals("EXIT")) {
                parent.shutDown();
            } else {
                parent.sendMessages(s);
            }
        }
    }
}

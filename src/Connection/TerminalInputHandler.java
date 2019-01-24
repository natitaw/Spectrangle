package Connection;

public class TerminalInputHandler implements Runnable{
    Server server;
    public TerminalInputHandler(Server inputServer) {
        this.server=inputServer;
    }

    @Override
    public void run() {
        while (server.getRunning()) {
            String s = Peer.readString("");
            if (s.equals("EXIT")) {
                server.shutDown();
            } else {
                server.sendMessages(s);
            }
        }
    }
}

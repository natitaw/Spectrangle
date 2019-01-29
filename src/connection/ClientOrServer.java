package connection;

public interface ClientOrServer {
    void shutDown();

    void sendMessageToAll(String s);

    boolean getRunning();

    enum Type {
        CLIENT, SERVER
    }

    Type getType();
}

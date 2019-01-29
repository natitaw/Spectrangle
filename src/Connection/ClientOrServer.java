package Connection;

public interface ClientOrServer {
    void shutDown();

    void sendMessages(String s);

    boolean getRunning();

    enum Type {
        CLIENT, SERVER
    }
}

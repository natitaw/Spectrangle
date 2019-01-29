package Connection;

public interface ClientOrServer {
    public void shutDown();

    public void sendMessages(String s);

    public boolean getRunning();
}

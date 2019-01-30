package controller;

import java.io.PrintStream;

public interface ClientOrServer {
    void shutDown();

    void sendMessageToAll(String s);

    boolean getRunning();

    enum Type {
        CLIENT, SERVER
    }

    PrintStream getPrinter();

    Type getType();

    void setName(String s);

    String getName();
}

package controller;

import java.io.PrintStream;

/**
 * ClientOrServer interface
 * Will be implemented in either Client or Server class. Has some shared methods for
 * ease of use, so that it can be referred to in other classes using "ClientOrServer parent"
 * and then, for example, running parent.sendMessageToAll(s), from a class that has methods
 * to send messages to other peers.
 */
public interface ClientOrServer {
    void shutDown();

    void sendMessageToAll(String s);

    boolean getRunning();

    PrintStream getPrinter();

    Type getType();

    String getName();

    void setName(String s);

    enum Type {
        CLIENT, SERVER
    }
}

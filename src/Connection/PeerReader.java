package Connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PeerReader implements Runnable {

    Peer p;
    public PeerReader(Peer inputPeer) {
        this.p=inputPeer;
    }

    public void run() {
        while (p.running) {
            p.handleTerminalInput();
        }
    }
}
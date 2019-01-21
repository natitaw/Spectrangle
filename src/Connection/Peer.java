package Connection;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

/**
 * Connection.Peer for a simple client-server application
 * @author  Theo Ruys
 * @version 2005.02.21
 */
public class Peer implements Runnable {
    public static final String EXIT = "exit";

    protected String name;
    protected Socket sock;
    protected BufferedReader in;
    protected PrintWriter out;
    public boolean running;

    /*@
       requires (nameArg != null) && (sockArg != null);
     */
    /**
     * Constructor. creates a peer object based in the given parameters.
     * @param   nameArg name of the Connection.Peer-proces
     * @param   sockArg Socket of the Connection.Peer-proces
     */
    public Peer(String nameArg, Socket sockArg) throws IOException
    {
        sock = sockArg;
        name = nameArg;
        in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        out = new PrintWriter(sock.getOutputStream(), true);
        running = true;


    }

    /**
     * Reads strings of the stream of the socket-connection and
     * writes the characters to the default output.
     */
    public void run() {
        while (running) {
        try {
            String s1 = in.readLine();
            if (s1 == null || s1 == "") {
                shutDown();
            } else {
                System.out.println(s1);
            }

        } catch (IOException e) {
            e.printStackTrace();
            running=false;
            shutDown();
    }
    } }


    /**
     * Reads a string from the console and sends this string over
     * the socket-connection to the Connection.Peer process.
     * On Connection.Peer.EXIT the method ends
     */
    public void handleTerminalInput() {
            String s = readString("");
            if (s.equals("EXIT")){
                shutDown();
            } else {
                out.println(s);
            }

    }

    /**
     * Closes the connection, the sockets will be terminated
     */
    public void shutDown() {
        try {
            System.out.println("Shutting down");
            running=false;
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
            running=false;

        }
    }

    /**  returns name of the peer object*/
    public String getName() {
        return name;
    }

    /** read a line from the default input */
    static public String readString(String tekst) {
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

    }


import Connection.Client;

import java.io.IOException;
import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args){
        System.out.println("Welcome to our Spectrangle client. Type the IP of a server to continue");
        Scanner scanner = new Scanner(System. in);
        String ip = scanner. nextLine();
        Client clientObject = new Client(ip);

        // Todo add shutdown message?
        // TODO make disconnect not shut down game
    }
}

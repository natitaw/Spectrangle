import connection.client.Client;
import connection.server.Server;

import java.util.Scanner;

public class ClientMain {

    public static final String[] mainArgs = {""};

    public static void main(String[] args) {
        System.out.println("");
        System.out.println("Welcome to our Spectrangle client. ");
        System.out.println("");
        System.out.println("What would you like to do? Type one of the numbers below to continue.");
        System.out.println("[1] Play Multiplayer    [2] Play Singleplayer   [3] Connect AI to server     [4] Quit");
        Scanner scanner = new Scanner(System.in);
        boolean choiceMade = false;
        while (!choiceMade) {
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    multiplayer();
                    choiceMade=true;
                    break;
                case 2:
                    singleplayer();
                    choiceMade=true;
                    break;
                case 3:
                    playAsAI();
                    choiceMade=true;
                    break;
                case 4:
                    choiceMade=true;
                    break;
                default:
                    System.out.println("Input should be 1-4");

            }
        }
    }

    private static void playAsAI() {
        System.out.println("Type the IP of a server to continue");
        Scanner scanner = new Scanner(System.in);
        //TODO Fix error when entering faulty ip


        String ip = scanner.nextLine();

        System.out.println("type AI name");
        String name = scanner.nextLine();

        System.out.println("type preferred nr of players");
        int preferredNrofPlayers= scanner.nextInt();

        System.out.println("Type preferred difficulty. 0: random, 1: best move, 2: best future move etc");
        double difficulty = scanner.nextInt();

        Client clientObject = new Client(ip, "ai " + name + " " + preferredNrofPlayers + " " + difficulty);





        while (clientObject.getRunning()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        // go back to main menu on disconnect
        clearScreen();

        System.out.println("The server has disconnected. Returned to main menu.");

        main(mainArgs);

    }


    public static void singleplayer() {
        Scanner scanner = new Scanner(System.in);
        final Server[] serverObject = new Server[1]; // needed weird final array because of inner class
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                serverObject[0] = new Server("singleplayer");
            }
        });
        serverThread.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Client clientObject = new Client("127.0.0.1", "singleplayer");




        while (clientObject.getRunning()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        serverObject[0].shutDown();
        try {
            serverThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // go back to main menu on disconnect
        clearScreen();

        System.out.println("User has exited singleplayer. Returned to main menu.");

        main(mainArgs);

    }

    public static void multiplayer() {
        System.out.println("Type the IP of a server to continue");
        Scanner scanner = new Scanner(System.in);
        //TODO Fix error when entering faulty ip

        String ip = scanner.nextLine();
        Client clientObject = new Client(ip, "");

        while (clientObject.getRunning()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        // go back to main menu on disconnect
        clearScreen();

        System.out.println("The server has disconnected. Returned to main menu.");

        main(mainArgs);
    }
    
    

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}

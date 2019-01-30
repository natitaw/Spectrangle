import controller.client.Client;
import controller.server.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClientMain {

    private static final String[] mainArgs = {""};

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
                    choiceMade = true;
                    break;
                case 2:
                    singleplayer();
                    choiceMade = true;
                    break;
                case 3:
                    playAsAI();
                    choiceMade = true;
                    break;
                case 4:
                    choiceMade = true;
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
        int preferredNrofPlayers = scanner.nextInt();

        System.out.println("Type preferred difficulty. 0: random, 1: best move, 2: best future move etc");
        double difficulty = scanner.nextInt();

        Client clientObject = new Client(ip, "ai " + name + " " + preferredNrofPlayers + " " + difficulty + " " + "false");


        while (clientObject.getRunning()) {
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


    private static void singleplayer() {
        Scanner scanner = new Scanner(System.in);
        final Server[] serverObject = new Server[1]; // needed weird final array because of inner class
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                serverObject[0] = new Server("singleplayer");
            }
        });
        serverThread.start();

        // wait for a moment so server can start
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("type preferred nr of players");
        int preferredNrofPlayers = scanner.nextInt();

        System.out.println("Type preferred difficulty. 0: random, 1: best move, 2: best future move etc");
        double difficulty = scanner.nextInt();


        List<Thread> aiThreads = new ArrayList<>();
        List<Client> aiObjects = new ArrayList<>();
        for (int i = 1; i < preferredNrofPlayers; i++) {


            final Client[] aiObject = new Client[1]; // needed weird final array because of inner class

            int finalI = i;
            Thread aiThread = new Thread(new Runnable() {
                final int j = finalI;

                @Override
                public void run() {
                    aiObject[0] = new Client("127.0.0.1", "ai Computer" + j + " " + preferredNrofPlayers + " " + difficulty + " " + "true");
                    aiObjects.add(aiObject[0]);
                }
            });
            aiThreads.add(aiThread);
            aiThread.start();
        }

        Client clientObject = new Client("127.0.0.1", "singleplayer Player " + preferredNrofPlayers + " " + difficulty);


        while (clientObject.getRunning()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 1; i <= preferredNrofPlayers; i++) {
            aiObjects.get(i).shutDown();
            try {
                aiThreads.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        // go back to main menu on disconnect
        clearScreen();

        System.out.println("User has exited singleplayer. Returned to main menu.");

        main(mainArgs);

    }

    private static void multiplayer() {
        System.out.println("Type the IP of a server to continue");
        Scanner scanner = new Scanner(System.in);
        //TODO Fix error when entering faulty ip

        String ip = scanner.nextLine();
        Client clientObject = new Client(ip, "");

        while (clientObject.getRunning()) {
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


    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}

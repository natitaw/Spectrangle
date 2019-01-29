import connection.client.Client;

import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("");
        System.out.println("Welcome to our Spectrangle client. ");
        System.out.println("");
        System.out.println("What would you like to do? Type one of the numbers below to continue.");
        System.out.println("[1] Play Multiplayer    [2] Play Singleplayer   [3] Change Settings     [4] Quit");
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
                    settings();
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

    private static void settings() {
        // TODO Implement settings
        // preferred name
        // enable chat
        // preferred game size
    }


    public static void singleplayer() {
        // TODO implement singleplayer
        System.out.println("Singleplayer is not ready yet. Going back to menu");
        String[] mainArgs = {""};
        main(mainArgs);

    }

    public static void multiplayer() {
        System.out.println("Type the IP of a server to continue");
        Scanner scanner = new Scanner(System.in);
        //TODO Fix error when entering faulty ip

        String ip = scanner.nextLine();
        Client clientObject = new Client(ip);
        while (clientObject.getRunning()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // go back to main menu on disconnect
        clearScreen();

        String[] mainArgs = {""};
        main(mainArgs);
    }
    
    

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}

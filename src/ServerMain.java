import controller.server.Server;

/**
 * Class with a main method that starts the game server.
 * To be used for dedicated server instances.
 */
class ServerMain {
    /**
     * Start game as a server. Does not allow user to play the game.
     * Functions the same as most multiplayer games: a dedicated server command line tool.
     *
     * @param args Arguments passed from command line. Not used.
     */
    public static void main(String[] args) {
        Server serverObject = new Server("");

    }
}

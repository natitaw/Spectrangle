import Connection.Server;

public class ServerMain {
    public static void main(String[] args){
       Server serverObject = new Server();
       Thread serverThread = new Thread(serverObject);
       serverThread.start();
    }
}

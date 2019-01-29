package connection.server;

import connection.Peer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GameRoom extends Room{

    public boolean hasFinished = false;
    private Server serverObject;



    public GameRoom (int nr, Server inputServer) {
        this(nr, new ArrayList<>(), inputServer);
    }

    public GameRoom(int nr, List<Peer> peers, Server inputServer) {
        super(nr, peers);
        this.serverObject = inputServer;

    }

    public void startGame() {
        determineOrder();

        // TODO implement game logic
        // TODO change terminalinput state
    }


    public void determineOrder(){
        List<String> nameList = peerList.stream().map(Peer::getName).collect(Collectors.toList());
        Collections.sort(nameList);

        // Determine order
        // send chat message
    }

    // TODO make sure this gets called
    public void peerDisconnected(){
        // TODO Implement what happens here
    }

}

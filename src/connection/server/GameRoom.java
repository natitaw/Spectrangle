package connection.server;

import connection.Peer;

import java.util.ArrayList;
import java.util.List;

public class GameRoom extends Room{

    public boolean hasFinished = false;

    public GameRoom (int nr) {
        this(nr, new ArrayList<>());
    }

    public GameRoom(int nr, List<Peer> peers) {
        super(nr, peers);
    }
}

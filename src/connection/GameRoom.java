package connection;

import java.util.ArrayList;
import java.util.List;

public class GameRoom extends Room{

    public GameRoom (int nr) {
        this(nr, new ArrayList<Peer>());
    }

    public GameRoom(int nr, List<Peer> peers) {
        super(nr, peers);
    }
}

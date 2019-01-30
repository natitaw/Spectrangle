package controller.server;

import controller.Peer;

import java.util.ArrayList;
import java.util.List;

public class Room {

    final List<Peer> peerList;
    private final int roomNumber;

    Room(int nr, List<Peer> peers) {
        this.roomNumber = nr;
        this.peerList = peers;
        //
    }

    public Room(int nr) {
        this(nr, new ArrayList<>());
    }

    public List<Peer> getPeerList() {
        return peerList;
    }

    public boolean addPeer(Peer p) {
        return peerList.add(p);
    }

    public boolean removePeer(Peer p) {
        return peerList.remove(p);
    }

    public int getRoomNumber() {
        return roomNumber;
    }
}

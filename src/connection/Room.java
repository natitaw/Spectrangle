package connection;

import java.util.ArrayList;
import java.util.List;

public class Room {

    private int roomNumber;
    private List<Peer> peerList;

    public Room(int nr, List<Peer> peers) {
        this.roomNumber = nr;
        this.peerList=peers;
        //
    }

    public Room(int nr) {
        this(nr, new ArrayList<Peer>());
    }

    public List<Peer> getPeerList() {
        return peerList;
    }

    public void setPeerList(List<Peer> peerList) {
        this.peerList = peerList;
    }

    public boolean addPeer(Peer p){
        return peerList.add(p);
    }

    public boolean removePeer(Peer p){
        return peerList.remove(p);
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }
}

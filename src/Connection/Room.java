package Connection;

import java.util.List;

public class Room {

    private int roomNumber;
    public Room (int nr, List<Peer> peerList) {
        this.roomNumber = nr;
        //
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }
}

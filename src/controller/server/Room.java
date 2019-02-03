package controller.server;

import controller.Peer;

import java.util.ArrayList;
import java.util.List;
/**
 * Room class. Allows peers to be placed in the same "location" for chatting and receiving commands.
 *
 * @author Bit 4 - Group 4
 */
public class Room {

    final List<Peer> peerList;
    private final int roomNumber;

    /**
     * Constructor, fills the room with a list of peers and gives the room a number.
     *
     * @param nr Specifies room number. Should be unique (one higher than existing nr of rooms).
     * @param peers A list of peers that the room will contain.
     * @author Bit 4 - Group 4
     */
    Room(int nr, List<Peer> peers) {
        this.roomNumber = nr;
        this.peerList = peers;
        //
    }
    /**
     * Calls the other constructor with an empty list, for if the room starts empty
     * @param nr Room number, as specified elsewhere.
     *
     * @author Bit 4 - Group 4
     */
    public Room(int nr) {
        this(nr, new ArrayList<>());
    }

    /**
     * Returns the peers in the room as a list
     *
     * @return peerList field
     * @author Bit 4 - Group 4
     */
    public List<Peer> getPeerList() {
        return peerList;
    }

    /**
     * Adds a peer to the room's list of peers
     *
     * @return true if list was changed because of this
     * @param p Peer object to be added to the room
     * @author Bit 4 - Group 4
     */
    public boolean addPeer(Peer p) {
        return peerList.add(p);
    }

    /**
     * Removes a peer from the room, if it exists. Returns a boolean on whether it succeeded
     * @return true if list was changed because of this
     * @param p Peer object to be removed form the room.
     * @author Bit 4 - Group 4
     */
    public boolean removePeer(Peer p) {
        return peerList.remove(p);
    }

    /**
     * Returns the room's number (a getter function)
     * @return the roomNumber field content
     * @author Bit 4 - Group 4
     */
    public int getRoomNumber() {
        return roomNumber;
    }
}

package model;

/**
 * Exception handler for when there is no piece that can be found at the location
 *
 * @author Bit 4 - Group 4
 */
class NoPieceException extends Exception {
    public NoPieceException(String s) {
        super(s);
    }
}

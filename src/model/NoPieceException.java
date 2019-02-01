package model;

/**
 * Exception handler for when there is no piece found
 *
 * @author Group4
 */
class NoPieceException extends Exception {
    public NoPieceException(String s) {
        super(s);
    }
}

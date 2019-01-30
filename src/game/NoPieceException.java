package game;

/**
 * Exception handler for when there is no piece found
 * @author User
 *
 */
class NoPieceException extends Exception {
    public NoPieceException(String s){
        super(s);
    }
}

package model;

/**
 * Exception thrown when a bag of pieces is empty but a method attempts to extract a piece.
 * @author Bit 4 - Group 4
 */
public class EmptyBagException extends Exception {
    public EmptyBagException(String s) {
        super(s);
    }
}

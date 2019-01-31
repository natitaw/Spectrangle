package model;

/**
 * An exception handler when TileBag is empty
 * @author Group4
 *
 */
public class EmptyBagException extends Exception {
    public EmptyBagException(String s) {
        super(s);
    }
}

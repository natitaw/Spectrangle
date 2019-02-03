package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Tile Bag Generator. Generates a "bag" (array) of Pieces
 *
 * @author Bit 4 - Group 4
 */

public class TileBag {

    private final ArrayList<Piece> pieces;

    /**
     * TileBag This class generates a bag of size with 36 tiles according to the
     * rules of Spectrangle
     */

    public TileBag(int sizeInput) {
        this.pieces = new ArrayList<>(sizeInput);
    }

    /**
     * Generates a new TileBag from a list of tiles in shortened string form (e.g. RRR6)
     * @param stringList List of strings of tiles
     * @return a new TileBag that contains these tiles as Piece objects
     */
    public static TileBag generateBag(List<String> stringList) {
        TileBag result = new TileBag(4);
        for (String s : stringList) {
            result.addPiece(new Piece(s));
        }
        return result;
    }

    /**
     * This method returns a random tile from the tile bag generated
     *
     * @return A random Piece
     */
    public Piece takeRandomPiece() throws EmptyBagException {
        if (pieces.size() != 0) {
            int random = (int) (Math.random() * this.pieces.size() - 1);
            Piece p = pieces.get(random);
            pieces.remove(random);
            return p;
        } else {
            throw new EmptyBagException("Bag is empty!");
        }
    }

    /**
     * Views a Piece object out of this TileBag from its ArrayList of pieces
     * @param i Index to be used to view a Piece from the TileBag
     * @return Piece object to be viewed from TileBag
     */
    public Piece viewPiece(int i) {
        return pieces.get(i);
    }

    /**
     * Fetches a Piece object out of this TileBag from its ArrayList of pieces
     * @param index Index to be used to fetch a Piece from the TileBag
     * @return Piece object fetched from TileBag
     */
    public Piece takePiece(int index) throws EmptyBagException {
        if (pieces.size() != 0) {
            Piece p = pieces.get(index);
            pieces.remove(index);
            return p;
        } else {
            throw new EmptyBagException("Bag is empty!");
        }
    }

    /**
     * Checks if a given Piece exists (matches with) in the TileBag
     * @param inputPiece Piece to be checked against existing pieces in TileBag
     * @return The location of the match in the TileBag
     */
    public int findPiece(Piece inputPiece) {
        for (Piece piece : pieces) {
            if (piece.equalsRotated(inputPiece) >= 0) {
                return pieces.indexOf(piece);
            }

        }
        return -1;
    }

    /**
     * Adds a piece in the TileBag
     * @param p
     */
    public void addPiece(Piece p) {
        pieces.add(p);
    }

    /**
     * equals method for checking if this bag equals another
     * Just compars the inner ArrayLists for equality
     * @param obj The other object (usuall a TileBag) to compare to
     * @return boolean that specifies whether they are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TileBag) {
            return ((TileBag) obj).getBag().equals(getBag());
        } else {
            return false;
        }
    }

    /**
     * This function will be called in the constructor and it will populate the bag
     * according to the rules of Spectrangle and includes Joker
     */
    public void populateBag() {

        // 6 Points

        for (ColorDefinition c : ColorDefinition.values()) {

            if (c != ColorDefinition.WHITE) {

                pieces.add(new Piece(c, c, c, 6));
            } else {
                // Joker
                pieces.add(new Piece(c, c, c, 1));
            }

        }

        // 5 Points

        for (ColorDefinition c : ColorDefinition.values()) {

            if (c == ColorDefinition.RED) {
                pieces.add(new Piece(c, c, ColorDefinition.YELLOW, 5));
                pieces.add(new Piece(c, c, ColorDefinition.PURPLE, 5));

            } else if (c == ColorDefinition.BLUE) {
                pieces.add(new Piece(c, c, ColorDefinition.RED, 5));
                pieces.add(new Piece(c, c, ColorDefinition.PURPLE, 5));
            } else if (c == ColorDefinition.GREEN) {
                pieces.add(new Piece(c, c, ColorDefinition.RED, 5));
                pieces.add(new Piece(c, c, ColorDefinition.BLUE, 5));
            } else if (c == ColorDefinition.YELLOW) {
                pieces.add(new Piece(c, c, ColorDefinition.BLUE, 5));
                pieces.add(new Piece(c, c, ColorDefinition.GREEN, 5));
            } else if (c == ColorDefinition.PURPLE) {
                pieces.add(new Piece(c, c, ColorDefinition.YELLOW, 5));
                pieces.add(new Piece(c, c, ColorDefinition.GREEN, 5));
            }
        }

        // 4 Points

        for (ColorDefinition c : ColorDefinition.values()) {

            if (c == ColorDefinition.RED) {
                pieces.add(new Piece(c, c, ColorDefinition.BLUE, 4));
                pieces.add(new Piece(c, c, ColorDefinition.GREEN, 4));

            } else if (c == ColorDefinition.BLUE) {
                pieces.add(new Piece(c, c, ColorDefinition.GREEN, 4));
                pieces.add(new Piece(c, c, ColorDefinition.YELLOW, 4));
            } else if (c == ColorDefinition.GREEN) {
                pieces.add(new Piece(c, c, ColorDefinition.YELLOW, 4));
                pieces.add(new Piece(c, c, ColorDefinition.PURPLE, 4));
            } else if (c == ColorDefinition.YELLOW) {
                pieces.add(new Piece(c, c, ColorDefinition.RED, 4));
                pieces.add(new Piece(c, c, ColorDefinition.PURPLE, 4));
            } else if (c == ColorDefinition.PURPLE) {
                pieces.add(new Piece(c, c, ColorDefinition.RED, 4));
                pieces.add(new Piece(c, c, ColorDefinition.BLUE, 4));
            }
        }

        // 3 Points
        pieces.add(new Piece(ColorDefinition.YELLOW, ColorDefinition.BLUE, ColorDefinition.PURPLE, 3));
        pieces.add(new Piece(ColorDefinition.RED, ColorDefinition.GREEN, ColorDefinition.YELLOW, 3));
        pieces.add(new Piece(ColorDefinition.BLUE, ColorDefinition.GREEN, ColorDefinition.PURPLE, 3));
        pieces.add(new Piece(ColorDefinition.GREEN, ColorDefinition.RED, ColorDefinition.BLUE, 3));

        // 2 Points
        pieces.add(new Piece(ColorDefinition.BLUE, ColorDefinition.RED, ColorDefinition.PURPLE, 2));
        pieces.add(new Piece(ColorDefinition.YELLOW, ColorDefinition.PURPLE, ColorDefinition.RED, 2));
        pieces.add(new Piece(ColorDefinition.YELLOW, ColorDefinition.PURPLE, ColorDefinition.GREEN, 2));

        // 1 Point
        pieces.add(new Piece(ColorDefinition.GREEN, ColorDefinition.RED, ColorDefinition.PURPLE, 1));
        pieces.add(new Piece(ColorDefinition.BLUE, ColorDefinition.YELLOW, ColorDefinition.GREEN, 1));
        pieces.add(new Piece(ColorDefinition.RED, ColorDefinition.YELLOW, ColorDefinition.BLUE, 1));

    }

    /**
     * Get an ArrayList<Piece> of Piece objects that will be used every time a Board
     * is generated
     *
     * @return An ArrayList of Piece objects (the TileBag)
     */
    public ArrayList<Piece> getBag() {
        return this.pieces;
    }

    /**
     * Method to get the number of pieces at any point in the model
     *
     * @return An integer that indicates the number of pieces left in this TileBag
     */
    public int getNumberOfPieces() {
        return getBag().size();
    }
}

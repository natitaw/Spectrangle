package game;

import java.util.ArrayList;

/**
 * Tile Bag Generator. Generates a bag of Piece(tile)
 * 
 * @author User
 *
 */

public class TileBag {

	private int maxSize;

	private ArrayList<Piece> pieces;

	/**
	 * TileBag This class generates a bag of size with 36 tiles according to the
	 * rules of Spectrangle
	 */
	// TODO update javadoc here
	public TileBag(int sizeInput) {
		this.pieces = new ArrayList<>(sizeInput);
		this.maxSize=sizeInput;
	}

	/**
	 * takeRandomPiece() returns a random tile from the tile bag generated
	 * 
	 * @return
	 */
	public Piece takeRandomPiece() {
		int random = (int) (Math.random() * this.pieces.size() - 1);
		Piece p = pieces.get(random);
		pieces.remove(random);
		return p;
	}

	public Piece viewPiece(int i) {
		return pieces.get(i);
	}

	public Piece takePiece(int index){
		Piece p = pieces.get(index);
		pieces.remove(index);
		return p;
	}

	public void addPiece(Piece p){
		pieces.add(p);
	}

	/**
	 * This function will be called in the constructor and it will populate the bag
	 * according to the rules of Spectrangle and includes Joker
	 */
	public void populateBag() {

		// 6 Points

		for (ColorDefinition c : ColorDefinition.values()) {
			ColorDefinition color = c;

			if (color != ColorDefinition.WHITE) {

				pieces.add(new Piece(color, color, color, 6));
			} else if (color == ColorDefinition.WHITE) {
				// Joker
				pieces.add(new Piece(color, color, color, 1));
			}

		}

		// 5 Points

		for (ColorDefinition c : ColorDefinition.values()) {
			ColorDefinition color = c;

			if (color == ColorDefinition.RED) {
				pieces.add(new Piece(color, color, ColorDefinition.YELLOW, 5));
				pieces.add(new Piece(color, color, ColorDefinition.PURPLE, 5));

			} else if (color == ColorDefinition.BLUE) {
				pieces.add(new Piece(color, color, ColorDefinition.RED, 5));
				pieces.add(new Piece(color, color, ColorDefinition.PURPLE, 5));
			} else if (color == ColorDefinition.GREEN) {
				pieces.add(new Piece(color, color, ColorDefinition.RED, 5));
				pieces.add(new Piece(color, color, ColorDefinition.BLUE, 5));
			} else if (color == ColorDefinition.YELLOW) {
				pieces.add(new Piece(color, color, ColorDefinition.BLUE, 5));
				pieces.add(new Piece(color, color, ColorDefinition.GREEN, 5));
			} else if (color == ColorDefinition.PURPLE) {
				pieces.add(new Piece(color, color, ColorDefinition.YELLOW, 5));
				pieces.add(new Piece(color, color, ColorDefinition.GREEN, 5));
			}
		}

		// 4 Points

		for (ColorDefinition c : ColorDefinition.values()) {
			ColorDefinition color = c;

			if (color == ColorDefinition.RED) {
				pieces.add(new Piece(color, color, ColorDefinition.BLUE, 4));
				pieces.add(new Piece(color, color, ColorDefinition.GREEN, 4));

			} else if (color == ColorDefinition.BLUE) {
				pieces.add(new Piece(color, color, ColorDefinition.GREEN, 4));
				pieces.add(new Piece(color, color, ColorDefinition.YELLOW, 4));
			} else if (color == ColorDefinition.GREEN) {
				pieces.add(new Piece(color, color, ColorDefinition.YELLOW, 4));
				pieces.add(new Piece(color, color, ColorDefinition.PURPLE, 4));
			} else if (color == ColorDefinition.YELLOW) {
				pieces.add(new Piece(color, color, ColorDefinition.RED, 4));
				pieces.add(new Piece(color, color, ColorDefinition.PURPLE, 4));
			} else if (color == ColorDefinition.PURPLE) {
				pieces.add(new Piece(color, color, ColorDefinition.RED, 4));
				pieces.add(new Piece(color, color, ColorDefinition.BLUE, 4));
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
	 * Get an ArrayList<Piece> of Piece objects that will be used everytime a Board
	 * is generated
	 * 
	 * @return
	 */
	public ArrayList<Piece> getBag() {
		return this.pieces;
	}

	/**
	 * Method to get the number of peices at anypoint in the game
	 * 
	 * @return
	 */
	public int getNumberOfPieces() {
		return getBag().size();
	}
}

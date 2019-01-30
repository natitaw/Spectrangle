package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Board Class representing a Spectrangle board
 * 
 * @author Group4
 *
 */
public class Board {

	public static final int DIM = 36; // loop t/m 35
	private BoardLocation[] boardLocations;
	private ArrayList<Piece> pieces;
	private TileBag tileBag;

	/*
	 * Generate a Board with the following attributes: an
	 * ArrayList of pieces List of board locations with bonusQuotients
	 */

	public Board(){
		this(new TileBag(36));
	}

	public Board(TileBag t) {
		this.tileBag = t;
		this.boardLocations = new BoardLocation[DIM];

		// populate the board

		for (int i = 0; i < Board.DIM; i++) {
			int bonusQuotient = 1;

			if (i == 10 || i == 14 || i == 30) {
				bonusQuotient = 2;

			} else if (i == 2 || i == 26 || i == 34) {
				bonusQuotient = 3;

			} else if (i == 11 || i == 13 || i == 20) {
				bonusQuotient = 4;

			}

			boardLocations[i] = new BoardLocation(i, bonusQuotient);
		}

	}

	public TileBag getTileBag() {
		return tileBag;
	}

	public void setTileBag(TileBag tileBag) {
		this.tileBag = tileBag;
	}

	/**
	 * Get a BoardLocation object that can be used to get information on the
	 * location
	 * 
	 * @param location
	 * @return
	 */
	public BoardLocation getBoardLocation(int location) {
		if (this.isValidLocation(location)) {
			return this.boardLocations[location];
		} else {
			return null;
		}
	}

	/**
	 * Get a Piece object that can be used to get information on the Piece
	 * 
	 * @param location
	 * @return
	 */
	public Piece getPiece(int location) {
		return this.boardLocations[location].getPiece();
	}

	/**
	 * Make a move on the location and return the success state This gives the
	 * BoardLocation a Piece object
	 * 
	 * @param location
	 * @param piece
	 * @return
	 */
	public boolean setMove(int location, Piece piece) {

		if (isValidLocation(location) && isEmptyLocation(location)) {

			this.boardLocations[location].movePiece(piece);

			return true;
		}
		return false;
	}

	// Queries

	// TODO: make sure that a piece can only be put next to another piece
	/**
	 * Check validity of the move Move is valid if location is valid AND: color
	 * matches at least on one side (except for Joker) board is empty then
	 * everywhere except for bonus spots board is not empty then move is valid if
	 * 
	 * @param location
	 * @param piece
	 * @return
	 */
	public boolean isValidMove(int location, Piece piece) {

		boolean result = false;

		if (isValidLocation(location)) {

			if (boardIsEmpty()) {

				result = !boardLocations[location].isBonusLocation();

			} else if (this.isValidColor(location, piece)

					&& isEmptyLocation(location)) {

				result = true;
			}
		}
		return result;
	}

	/**
	 * Convert coordinates to index
	 * 
	 * @param r
	 * @param c
	 * @return
	 */
	public int getIndex(int r, int c) {
		return ((int) (Math.pow(r, 2) + r + c));
	}

	/**
	 * Move piece according to the rules of Spectrangle
	 * 
	 * @param location
	 * @param piece
	 * @return
	 */
	public int movePiece(int location, Piece piece) {
		int point = 0;
		if (isValidLocation(location) && isValidMove(location, piece)) {

			boardLocations[location].movePiece(piece);
			point = boardLocations[location].getScorePoint() * piece.getValue();

		}
		return point;
	}

	/**
	 * Check color validity against neighbor
	 * 
	 * @param location
	 * @param piece
	 * @return
	 */

	public boolean isValidColor(int location, Piece piece) {

		// TODO: implement
		boolean result = false;

		// Joker fits at all locations

		if (isValidLocation(location) && piece.isJoker()) {

			result = true;
		}

		// TODO: check for matching colors

		return result;
	}

	// TODO: Check for left, right and bottom neighbors

	/**
	 * Get left piece of a given piece
	 * @param piece
	 * @return
	 */
	public Piece getLeftPiece(Piece piece) {
		return null;
	}

	/**
	 * Check if the given location is valid
	 * 
	 * @param location
	 * @return
	 */
	public boolean isValidLocation(int location) {
		return (location >= 0 && location < Board.DIM);
	}

	/**
	 * Check if the location is empty
	 * @param location
	 * @return
	 */
	public boolean isEmptyLocation(int location) {
		return (this.boardLocations[location].isEmptySpot());
	}

	/**
	 * check if Board is empty
	 * @return
	 */
	public boolean boardIsEmpty() {
		boolean result = true;
		for (int i = 0; i < this.boardLocations.length; i++) {
			if (!boardLocations[i].isEmptySpot()) {
				result = false;
			}
		}
		return result;
	}

	// TODO: Add a function to check if colors are valid

	public String toPrinterString(){
		  List<Integer> values =        Arrays.asList(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		  List<Character> vertical =    Arrays.asList(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		  List<Character> left =        Arrays.asList(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		  List<Character> right =       Arrays.asList(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

		for (int i=0; i<36; i++){
			if (getPiece(i)!=null){
				String pieceString = getPiece(i).toString();
				vertical.set(i,pieceString.charAt(0));
				left.set(i,pieceString.charAt(1));
				right.set(i,pieceString.charAt(2));
				values.set(i,Character.getNumericValue(pieceString.charAt(3)));
			}
		}
		return SpectrangleBoardPrinter.getBoardString(values,vertical,left,right);
	}
}

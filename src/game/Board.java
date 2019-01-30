package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
	 * Generate a Board with the following attributes: an ArrayList of pieces List
	 * of board locations with bonusQuotients
	 */

	public Board() {
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

	// Queries

	/**
	 * Check validity of the move. Move is valid if location is valid AND: color
	 * matches at least on one side (except for Joker) board is empty then
	 * everywhere except for bonus spots board is not empty then move is valid if
	 * 
	 * @param location
	 * @param piece
	 * @return
	 */

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
	 * Convert index to coordinates to determine left, right, top and bottom pieces
	 * 
	 * @param index
	 * @return
	 */
	public ArrayList<Integer> getCoordinate(int index) {
		ArrayList<Integer> tuple = new ArrayList<>();
		int r = ((int) Math.floor((int) Math.sqrt(index)));
		int c = (index - (((int) Math.pow(r, 2))));
		tuple.add(r);
		tuple.add(c);
		return tuple;
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

	public int getPotentialMoveScore(int location, Piece piece) {
		if (isValidMove(location, piece)) {
			return boardLocations[location].getScorePoint() * piece.getValue();
		} else {
			return 0;
		}
	}

	// TODO: make sure that a piece can only be put next to another piece
	// TODO: Implement properly
	public boolean isValidMove(int location, Piece piece) {

		boolean result = false;

		if (isValidLocation(location)) {

			if (boardIsEmpty()) {

				result = boardLocations[location].isBonusLocation();

			} else if (this.isValidColor(location, piece)

					&& isEmptyLocation(location)) {

				result = true;
			}
		}
		return result;
	}

	/**
	 * Check color validity against neighbor
	 * This function checks wheather or not a piece can be put on a location depending on the matching colors
	 * @param location
	 * @param piece
	 * @return
	 */
	// TODO: Implement Properly
	public boolean isValidColor(int location, Piece piece) {


		if (isValidLocation(location) && piece.isJoker()) {
			return true;
		}
		
		ArrayList<Piece> neighbors = new ArrayList<>();
		
		Piece left = (this.getLeftPiece(location));
		Piece right = (this.getRightPiece(location));
		Piece top = (this.getTopPiece(location));
		Piece bottom = (this.getBottomPiece(location));
		
		if (left != null) {
			for (ColorDefinition c : left.getColors()) {
				for (ColorDefinition d : piece.getColors()) {
					if (d.equals(c)) {
						return true;
					}
				}
			}
		}
		
		if (right != null) {
			for (ColorDefinition c : right.getColors()) {
				for (ColorDefinition d : piece.getColors()) {
					if (d.equals(c)) {
						return true;
					}
				}
			}
		}
		
		if (top != null) {
			for (ColorDefinition c : top.getColors()) {
				for (ColorDefinition d : piece.getColors()) {
					if (d.equals(c)) {
						return true;
					}
				}
			}
		}
		
		if (bottom != null) {
			for (ColorDefinition c : bottom.getColors()) {
				for (ColorDefinition d : piece.getColors()) {
					if (d.equals(c)) {
						return true;
					}
				}
			}
		}
		

		return false;
	}

	// TODO: Check for left, right and bottom neighbors

	/**
	 * Get left piece of a given piece
	 * 
	 * @param piece
	 * @return
	 */
	public Piece getLeftPiece(int index) {
		int r = getCoordinate(index).get(0);
		int c = getCoordinate(index).get(1);
		if ((c - 1) >= (-1 * r)) {
			int a = getIndex(r, c - 1);
			return this.getBoardLocation(a).getPiece();
		}
		return null;
	}

	/**
	 * Get right piece of given piece
	 * 
	 * @param piece
	 * @return
	 */
	public Piece getRightPiece(int index) {
		int r = getCoordinate(index).get(0);
		int c = getCoordinate(index).get(1);
		if ((c + 1) <= r) {
			int a = getIndex(r, c + 1);
			return this.getBoardLocation(a).getPiece();
		}
		return null;
	}

	/**
	 * Get bottom piece of given piece
	 * 
	 * @param piece
	 * @return
	 */
	public Piece getBottomPiece(int index) throws NoPieceException{
		int r = getCoordinate(index).get(0);
		int c = getCoordinate(index).get(1);
		if ((r + c) % 2 == 0) {
			if (r + 1 <= 5) {
				int a = getIndex(r + 1, c);
				return this.getBoardLocation(a).getPiece();
			}
		} else {
			
			throw new NoPieceException("No Piece Found");
		}
		
		throw new NoPieceException("No Piece Found");
	}

	/**
	 * Get top piece of given piece
	 * 
	 * @param piece
	 * @return
	 */
	public Piece getTopPiece(int index) throws NoPieceException{
		int r = getCoordinate(index).get(0);
		int c = getCoordinate(index).get(1);
		if ((r + c) % 2 != 0) {
			int a = getIndex(r - 1, c);
			return this.getBoardLocation(a).getPiece();
		} else {
			throw new NoPieceException("No Piece Found");
		}
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
	 * 
	 * @param location
	 * @return
	 */
	public boolean isEmptyLocation(int location) {
		return (this.boardLocations[location].isEmptySpot());
	}

	/**
	 * check if Board is empty
	 * 
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

	public String toPrinterString() {
		List<Integer> values = Arrays.asList(null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null);
		List<Character> vertical = Arrays.asList(null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null);
		List<Character> left = Arrays.asList(null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null);
		List<Character> right = Arrays.asList(null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null);

		for (int i = 0; i < 36; i++) {
			if (getPiece(i) != null) {
				String pieceString = getPiece(i).toString();
				vertical.set(i, pieceString.charAt(0));
				left.set(i, pieceString.charAt(1));
				right.set(i, pieceString.charAt(2));
				values.set(i, Character.getNumericValue(pieceString.charAt(3)));
			}
		}
		return SpectrangleBoardPrinter.getBoardString(values, vertical, left, right);
	}
}

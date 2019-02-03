package model;

import view.SpectrangleBoardPrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Board Class representing a Spectrangle board.
 * Has some methods for checking moves and applying them to the board.
 *
 * @author Bit 4 - Group 4
 * @version 1.0
 */
public class Board {
	//@ private invariant boardLocations.length == DIM;
	private static final int DIM = 36; // loop t/m 35
	
	private final BoardLocation[] boardLocations;

	/**
	 * Instantiate a Board
	 */
	public Board() {
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

	/**
	 * Get a BoardLocation object that can be used to get information on the
	 * location
	 *
	 * @param location on the board
	 * @return The BoardLocation object at a given location
	 */
	//@ requires location >= 0 && location < 36;
	//@ pure
	
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
	 * @param location on the board
	 * @return The Piece object that lies on the given location
	 */
	
	//@ requires location >= 0 && location < 36;
	//@ ensures \result != null;
	//@ pure	
	public Piece getPiece(int location) {
		return this.boardLocations[location].getPiece();
	}

	/**
	 * Convert coordinates to index
	 *
	 * @param r first part of the coordinate
	 * @param c second part of the coordinate
	 * @return the index based on the conversion of the coordinates
	 */
	//@ requires r >= 0 && c >= 0;
	//@ ensures \result >=  0;
	
	//@ pure
	public int getIndex(int r, int c) {
		return ((int) (Math.pow(r, 2) + r + c));
	}

	/**
	 * Convert index to coordinates to determine left, right, top and bottom pieces
	 *
	 * @param index computed by given coordinates representing location on the board
	 *              object
	 * @return an ArrayList of integers that contains information about the
	 *         coorinates computed from a given index
	 */
	
	//@ requires index >= 0 && index <= 36;
	//@ ensures \result !=  null;
	//@ ensures !\result.isEmpty();
	//@ pure
	public ArrayList<Integer> getCoordinate(int index) {
		ArrayList<Integer> tuple = new ArrayList<>();
		int r = (int) Math.floor(Math.sqrt(index));
		int rSquaredPlusR = (int) (Math.pow(r, 2) + r);
		int c = index - rSquaredPlusR;
		tuple.add(r);
		tuple.add(c);
		return tuple;
	}

	/**
	 * Move piece according to the rules of Spectrangle
	 *
	 * @param location on the board
	 * @param piece    a Piece object that will be moved to the given locatoin
	 * @return the amount of points gained by performing such move
	 */
	//@ requires location >= 0 && location <= 36;
	//@ requires piece != null;
	//@ ensures \result >= 0 && \result <= 6;
	//@ ensures this.getPiece(location) == piece;
	public int movePiece(int location, Piece piece) {
		int point = 0;
		if (isValidLocation(location) && isValidMove(location, piece)) {

			boardLocations[location].movePiece(piece);
			point = boardLocations[location].getScorePoint() * piece.getValue();

		}
		return point;
	}

	/**
	 * This function returns the potential point that could be gained by performing
	 * the given move
	 * 
	 * @param location on the board
	 * @param piece    a Piece object that will be moved to the given location
	 * @return potential amounts of points that can be gained by such a move
	 */
	//@ requires location >= 0 && location <= 36;
	//@ requires piece != null;
	//@ ensures \result >= 0;
	//@ pure
	public int getPotentialMoveScore(int location, Piece piece) {
		if (isValidMove(location, piece)) {
			return boardLocations[location].getScorePoint() * piece.getValue();
		} else {
			return 0;
		}
	}

	// TODO: Check Implementation
	/**
	 * This function checks the validity of a move based on its location
	 * 
	 * @param location on the board
	 * @param piece    a Piece object that will be moved to the given location
	 * @return A boolean that verifies the validity of the move
	 */
	//@ requires location >= 0 && location <= 36;
	//@ requires piece != null;
	//@ pure
	public boolean isValidMove(int location, Piece piece) {

		boolean result = false;

		if (isValidLocation(location)) {

			if (boardIsEmpty()) {

				result = !boardLocations[location].isBonusLocation();

			} else if (isValidColor(location, piece) && isEmptyLocation(location)) {

				// isValidColor() implicitly ensures that the Piece is being placed next to
				// another piece

				result = true;
			}
		}
		return result;
	}

	/**
	 * Check color validity against neighbor This function checks whether or not a
	 * piece can be put on a location depending on the matching colors
	 *
	 * @param location on the board
	 * @param piece    a Piece object that will be moved to the given location
	 * @return A boolean that verifies the validity of the move based on the colors
	 */
	//@ requires location >= 0 && location <= 36;
	//@ requires piece != null;
	//@ pure
	public boolean isValidColor(int location, Piece piece) {

		if (isValidLocation(location) && piece.isJoker()) {
			return true;
		}

		ArrayList<Piece> neighbors = new ArrayList<>();

		Piece left = null;
		try {
			left = (this.getLeftPiece(location));
		} catch (NoPieceException ignored) {

		}
		Piece right = null;
		try {
			right = (this.getRightPiece(location));
		} catch (NoPieceException ignored) {

		}
		Piece top = null;
		try {
			top = (this.getTopPiece(location));
		} catch (NoPieceException ignored) {

		}
		Piece bottom = null;
		try {
			bottom = (this.getBottomPiece(location));
		} catch (NoPieceException ignored) {

		}

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


	/**
	 * Get the piece that lies on the left of the given index
	 * 
	 * @param index location index of a piece
	 * @return A Piece object
	 * @throws NoPieceException in case there is no Piece to be found next to the
	 *                          given index
	 */
	//@ requires index >= 0 && index <= 36;
	//@ ensures \result != null;
	//@ pure
	public Piece getLeftPiece(int index) throws NoPieceException {
		int r = getCoordinate(index).get(0);
		int c = getCoordinate(index).get(1);
		if ((c - 1) >= (-1 * r)) {
			int a = getIndex(r, c - 1);
			if (this.getBoardLocation(a) != null) {
				Piece piece = this.getBoardLocation(a).getPiece();
				if (piece == null) {
					return null;
				} else {
					return piece;
				}
			} else {
				return null;
			}
		} else {

			throw new NoPieceException("No Piece Found");
		}

	}

	/**
	 * Get the piece that lies on the right of the given index
	 * 
	 * @param index location index of a piece
	 * @return A Piece object
	 * @throws NoPieceException in case there is no Piece to be found next to the
	 *                          given index
	 */
	//@ requires index >= 0 && index <= 36;
	//@ ensures \result != null;
	//@ pure
	public Piece getRightPiece(int index) throws NoPieceException {
		int r = getCoordinate(index).get(0);
		int c = getCoordinate(index).get(1);
		if ((c + 1) <= r) {
			int a = getIndex(r, c + 1);
			if (this.getBoardLocation(a) != null) {
				Piece piece = this.getBoardLocation(a).getPiece();
				if (piece == null) {
					return null;
				} else {
					return piece;
				}
			} else {
				return null;
			}
		} else {

			throw new NoPieceException("No Piece Found");
		}

	}

	/**
	 * Get the piece that lies on the bottom of the given index
	 * 
	 * @param index location index of a piece
	 * @return A Piece object
	 * @throws NoPieceException in case there is no Piece to be found next to the
	 *                          given index
	 */
	//@ requires index >= 0 && index <= 36;
	//@ ensures \result != null;
	//@ pure
	public Piece getBottomPiece(int index) throws NoPieceException {
		int r = getCoordinate(index).get(0);
		int c = getCoordinate(index).get(1);
		if ((r + c) % 2 == 0) {
			if (r + 1 <= 5) {
				int a = getIndex(r + 1, c);
				if (this.getBoardLocation(a) != null) {
					Piece piece = this.getBoardLocation(a).getPiece();
					if (piece == null) {
						return null;
					} else {
						return piece;
					}
				} else {
					return null;
				}

			}
		} else {

			throw new NoPieceException("No Piece Found");
		}

		throw new NoPieceException("No Piece Found");
	}

	/**
	 * Get the piece that lies on the top of the given index
	 * 
	 * @param index location index of a piece
	 * @return A Piece object
	 * @throws NoPieceException in case there is no Piece to be found next to the
	 *                          given index
	 */
	//@ requires index >= 0 && index <= 36;
	//@ ensures \result != null;
	//@ pure
	public Piece getTopPiece(int index) throws NoPieceException {
		int r = getCoordinate(index).get(0);
		int c = getCoordinate(index).get(1);
		if ((r + c) % 2 != 0) {
			int a = getIndex(r - 1, c);
			if (this.getBoardLocation(a) != null) {
				Piece piece = this.getBoardLocation(a).getPiece();
				if (piece == null) {
					return null;
				} else {
					return piece;
				}
			} else {
				return null;
			}
		} else {
			throw new NoPieceException("No Piece Found");
		}
	}

	/**
	 * Check if the given location is valid
	 *
	 * @param location A number representing a location on the board
	 * @return A boolean that verifies the validity of the location
	 */
	//@ requires location >= 0 && location <= 36;
	//@ pure
	public boolean isValidLocation(int location) {
		return (location >= 0 && location < Board.DIM);
	}

	/**
	 * Check if the location is empty
	 *
	 * @param location A number representing a location on the board
	 * @return A boolean that verifies the emptiness of the location
	 */
	//@ requires location >= 0 && location <= 36;
	//@ pure
	public boolean isEmptyLocation(int location) {
		return (this.boardLocations[location].isEmptySpot());
	}

	/**
	 * Check if this Board object is empty
	 * @return A boolean that verifies the emptiness of the Board
	 */
	//@ pure;
	public boolean boardIsEmpty() {
		boolean result = true;
		for (int i = 0; i < this.boardLocations.length; i++) {
			if (!boardLocations[i].isEmptySpot()) {
				result = false;
			}
		}
		return result;
	}
	
	/**
	 * This function prints the Board in a presentable manner
	 * @return A String object that can be clearly printed
	 */
	//@ ensures \result != null;
	//@ pure
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

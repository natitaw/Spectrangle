package game;

/**
 * Class representing information on a location of a Spectrangle Board
 * 
 * @author Group4
 *
 */
public class BoardLocation {

	
	private int location;
	private int scorePoint;
	private Piece piece;
	
	/**
	 * Initialize Board location
	 * @param location
	 * @param scorePoint
	 */
	public BoardLocation(int location, int scorePoint) {
		this.location = location;
		this.scorePoint = scorePoint;

	}

	// Queries
	/**
	 * Return the amounts of point of this location
	 * @return
	 */
	public int getScorePoint() {
		return scorePoint;
	}
	
	/**
	 * Return the location of this spot
	 * @return
	 */
	public int getLocation() {
		return this.location;
	}
	/**
	 * Return the piece of this spot
	 * @return
	 */
	public Piece getPiece() {
		return this.piece;
	}

	/**
	 * Set a piece on this spot
	 * @param p
	 */
	public void movePiece(Piece p) {
		this.piece = p;
	}
	
	/**
	 * Check if this spot is a bonus location
	 * @return
	 */
	public boolean isBonusLocation() {
		return this.scorePoint > 1;
	}
	
	/**
	 * Check if this spot is an empty spot
	 * @return
	 */
	public boolean isEmptySpot() {
		return (this.piece == null);
	}

}

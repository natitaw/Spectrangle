package Game;

public class BoardLocation {
	
	// BoardLocation class to represent information about
	// a spot on a board
	// a spot has: piece, location(id), and scorePoint(multiplier)
	
	private int location;
	private int scorePoint;
	private Piece piece;
	
	public BoardLocation(int location, int scorePoint) {
		this.location = location;
		this.scorePoint = scorePoint;
	}
	
	// Queries
	
	public int getScorePoint() {
		return this.scorePoint;
	}
	
	public int getLocation() {
		return this.location;
	}
	
	public Piece getPiece() {
		return this.piece;
	}
	
	
	public void movePiece(Piece p) {
		this.piece = p;
	}
	
	public boolean isBonusLocation() {
		return this.scorePoint > 1;
	}
	
	public boolean isEmptySpot() {
		return (this.piece == null);
	}

}

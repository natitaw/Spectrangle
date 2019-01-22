package Game;

public class BoardLocation {
	
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

}

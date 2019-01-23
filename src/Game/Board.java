package Game;

public class Board {

	public static final int DIM = 36;
	private BoardLocation[] boardLocations;
	private TileBagGenerator bag;

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
		
		bag = new TileBagGenerator();

	}


	public BoardLocation getLocation(int location) {
		if (this.isValidLocation(location)) {
			return this.boardLocations[location];
		} else {
			return null;
		}
	}

	public Piece getPiece(int location) {
		return this.boardLocations[location].getPiece();
	}

	public boolean setMove(int location, Piece piece) {

		if (isValidLocation(location) && isEmptyLocation(location)) {

			this.boardLocations[location].movePiece(piece);
			return true;
		}
		return false;
	}

	// Queries

	public boolean isValidMove(int location) {
		boolean result = false;

		if (isValidLocation(location)) {

			if (boardIsEmpty()) {

				return (boardLocations[location].isBonusLocation());

			} else if (isEmptyLocation(location)) {
				result = true;
			}
		}
		return result;
	}
	
	//TODO: program makeMove
	
	public int movePiece(int location, Piece piece) {
		int point = 0;
		if(isValidLocation(location) && isValidMove(location)) {
			
			boardLocations[location].movePiece(piece);
			point = boardLocations[location].getScorePoint() * piece.getPoint();	
			
		}
		return point;
	}

	public boolean isValidLocation(int location) {
		return (location < 0 && location > Board.DIM);
	}

	public boolean isEmptyLocation(int location) {
		return (this.boardLocations[location].isEmptySpot());
	}

	public boolean boardIsEmpty() {
		boolean result = false;
		for (int i = 0; i < this.boardLocations.length; i++) {
			if (boardLocations[i].isEmptySpot()) {
				result = true;
			}
		}
		return result;
	}

}

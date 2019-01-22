package Game;

public class Board {

	public static final int DIM = 36;
	private BoardLocation[] boardLocations;
	private boolean boardIsEmpty = false;
	
	
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
		
		//TODO: Make a method for it! 
		
		this.boardIsEmpty = true;
	}
	
	public Piece getPiece(int location) {
		return this.boardLocations[location].getPiece();
	}
	
	
	public boolean validMove(int location, Piece piece) {
		boolean result = false;
		
		if (isValidLocation(location)) {
			
			if(boardIsEmpty) {
				
				if (boardLocations[location].isBonusLocation()) {
					return false;
				}
			} else if (isEmptyLocation(location)) {
				result = true;
			}
		}
		return result;
	}
	
	public boolean setMove(int location, Piece piece) {
		
		if(isValidLocation(location) && isEmptyLocation(location)) {
			
			this.boardLocations[location].movePiece(piece);
			return true;
		}
		return false;
	}
	
	
	public boolean isValidLocation(int location) {
		if (location < 0 && location > Board.DIM) {
			return false;
		}
		return true;
	}
	
	public boolean isEmptyLocation(int location) {
		return (this.boardLocations[location].getPiece() == null);
	}
	
	
	
	
}























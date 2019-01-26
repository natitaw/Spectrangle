package Game;

public class Piece {
	
	private int point;
	private ColorDefinition colorSide1;
	private ColorDefinition colorSide2;
	private ColorDefinition colorSide3;
	
	
	public Piece(ColorDefinition one, ColorDefinition two, 
			ColorDefinition three, int point) {
		this.colorSide1 = one;
		this.colorSide2 = two;
		this.colorSide3 = three;
		this.point = point;
	}
	
	
	//TODO: add color check functionality 
	
	//TODO: add color rotation functionality
	
	public int getPoint() {
		return this.point;
	}

}

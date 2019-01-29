package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Java Class representing a Piece (tile) of a Spectrangle game
 * 
 * @author Group4
 *
 */
public class Piece {

	private int value;

	// TODO: These fields might not be needed
    private ColorDefinition bottom;
    private ColorDefinition left;
	private ColorDefinition right;

	/*
	 * Orientation is as follows: Left, Right, Bottom
	 */
	private ArrayList<ColorDefinition> orientation;

	public Piece(ColorDefinition bottom, ColorDefinition left, ColorDefinition right, int value) {
		orientation  = new ArrayList<>();
        this.bottom = bottom;
        this.left = left;
		this.right = right;
		this.value = value;
        this.orientation.add(this.bottom);
		this.orientation.add(this.left);
		this.orientation.add(this.right);
	}

    public Piece(List<ColorDefinition> orientationInput, int value) {
        this(orientationInput.get(0), orientationInput.get(1), orientationInput.get(2), value);
    }

	public Piece(String s) {
	    List<ColorDefinition> tempColors = new ArrayList<>();
        for (int i=0; i<3; i++) {


	    for (ColorDefinition tempColor : ColorDefinition.values()){
            if ( Character.toString(s.charAt(i)).equals(Character.toString(tempColor.toString().charAt(0)))){
                    tempColors.add(tempColor);
                }
            }
        }

        orientation  = new ArrayList<>();
        this.bottom = tempColors.get(0);
        this.left = tempColors.get(1);
        this.right = tempColors.get(2);
        this.value = s.charAt(3);
        this.orientation.add(this.bottom);
        this.orientation.add(this.left);
        this.orientation.add(this.right);
    }



    // TODO: add color check functionality


    public Piece getRotated() {
        List<ColorDefinition> tempOrientation = new ArrayList<>(orientation);
        Collections.rotate(tempOrientation, 1);
        return new Piece(tempOrientation, value);
    }

    public Piece getRotated2x() {
        List<ColorDefinition> tempOrientation = new ArrayList<>(orientation);
        Collections.rotate(tempOrientation, 2);
        return new Piece(tempOrientation, value);
    }
	/**
	 * rotate() This function rotates the Piece object Note: flipping is not taken
	 * into account
	 * 
	 * @return
	 */
	public void rotate() {

	    this.orientation=getRotated().getColors();
	}

	/**
	 * Rotate twice
	 * @return
	 */

	public void rotate2x() {

        this.orientation=getRotated2x().getColors();

	}
	/**
	 * Return the orientaiton of this piece
	 * @return
	 */
	public ArrayList<ColorDefinition> getColors() {
		return this.orientation;
	}

	// TODO Make javadoc
    public ColorDefinition getColor(int index) {
        return orientation.get(index);
    }

	/**
	 * Return the number of points of given tile
	 * 
	 * @return
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Function to check if tile is joker
	 * 
	 * @return
	 */
	public boolean isJoker() {
		return (orientation.get(0).equals(ColorDefinition.WHITE));
	}

	/**
	 * Check if given Piece is the same as this Piece
	 * 
	 * @param p
	 * @return
	 */
	public boolean isSamePiece(Piece p) {
		return (this.equals(p) || this.equals(p.getRotated()) || this.equals(p.getRotated2x()));
	}

	@Override
	public String toString(){
        String result = "";
        for (int pieceColorIndex = 0; pieceColorIndex <= 2; pieceColorIndex++){
            Character letter = orientation.get(pieceColorIndex).toString().charAt(0);
            result.concat(letter.toString());
        }
        result.concat(Integer.toString(value));
        return result;
	}

	public String print(){
        return PiecePrinter.printPiece(value,this.bottom.toString().charAt(0),this.left.toString().charAt(0),this.right.toString().charAt(0));
    }
}

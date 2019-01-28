package Game;

import java.util.ArrayList;
import java.util.Collections;

public class Piece {

	private int point;

	// TODO: These fields might not be needed
	private ColorDefinition left;
	private ColorDefinition right;
	private ColorDefinition bottom;
	/*
	 * Orientation is as follows: Left, Right, Bottom
	 */
	private ArrayList<ColorDefinition> orientation = new ArrayList<>();

	public Piece(ColorDefinition left, ColorDefinition right, ColorDefinition bottom, int point) {
		this.left = left;
		this.right = right;
		this.bottom = bottom;
		this.point = point;
		this.orientation.add(this.left);
		this.orientation.add(this.right);
		this.orientation.add(this.bottom);
	}

	// TODO: add color check functionality

	/*
	 * rotate() This function rotates the Piece object Note: flipping is not taken
	 * into account
	 */

	public void rotate() {

		Collections.swap(orientation, 0, 2);
		Collections.swap(orientation, 1, 2);

		// TODO: This might not be needed

		this.left = orientation.get(0);
		this.right = orientation.get(1);
		this.bottom = orientation.get(2);

	}

	/*
	 * Function that rotates tile twice might be useful
	 */

	public void rotate2x() {
		
		rotate();
		rotate();

		// TODO: This might not be needed

		this.left = orientation.get(0);
		this.right = orientation.get(1);
		this.bottom = orientation.get(2);

	}

	public ArrayList<ColorDefinition> getOrientation() {
		return this.orientation;
	}

	/*
	 * Return the number of points attributed to this Piece object
	 */
	public int getPoint() {
		return point;
	}

	/*
	 * Check if this Piece object is a Joker
	 */
	public boolean isJoker() {
		return (orientation.get(0).equals(ColorDefinition.WHITE));
	}


	public static void main(String[] args) {
		Piece p = new Piece(ColorDefinition.RED, ColorDefinition.GREEN, ColorDefinition.PURPLE, 5);

		System.out.println(p.orientation);
		System.out.println(p.isJoker());
		p.rotate();
		System.out.println(p.orientation);
		p.rotate2x();
		System.out.println(p.orientation);
	}

}

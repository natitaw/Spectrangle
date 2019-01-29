package game;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Java Class representing a Piece (tile) of a Spectrangle game
 * 
 * @author Group4
 *
 */
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

	/**
	 * rotate() This function rotates the Piece object Note: flipping is not taken
	 * into account
	 * 
	 * @return
	 */
	public Piece rotate() {

		Collections.swap(orientation, 0, 2);
		Collections.swap(orientation, 1, 2);

		// TODO: This might not be needed

		this.left = orientation.get(0);
		this.right = orientation.get(1);
		this.bottom = orientation.get(2);

		return this;

	}

	/**
	 * Rotate twice
	 * @return
	 */

	public Piece rotate2x() {

		rotate();
		rotate();

		// TODO: This might not be needed

		this.left = orientation.get(0);
		this.right = orientation.get(1);
		this.bottom = orientation.get(2);

		return this;

	}
	/**
	 * Return the orientaiton of this piece
	 * @return
	 */
	public ArrayList<ColorDefinition> getOrientation() {
		return this.orientation;
	}

	/**
	 * Return the number of points of given tile
	 * 
	 * @return
	 */
	public int getPoint() {
		return point;
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
		return (this.equals(p) || this.equals(p.rotate()) || this.equals(p.rotate2x()));
	}
}

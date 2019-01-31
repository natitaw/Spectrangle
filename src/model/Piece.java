package model;

import view.PiecePrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Java Class representing a Piece (tile) of a Spectrangle model
 *
 * @author Group4
 */
public class Piece {

	private final int value;

	private ColorDefinition bottom;
	private ColorDefinition left;
	private ColorDefinition right;

	/*
	 * Orientation is as follows: Left, Right, Bottom
	 */
	private ArrayList<ColorDefinition> orientation;

	/**
	 * Initialize a Piece object by giving it the color informatoin
	 * 
	 * @param bottom Color on the bottom part of the Piece object
	 * @param left   Color on the left part of the Piece object
	 * @param right  Color on the right part of the Piece object
	 * @param value  The intrinsic value of the Piece object
	 */
	public Piece(ColorDefinition bottom, ColorDefinition left, ColorDefinition right, int value) {
		orientation = new ArrayList<>();
		this.bottom = bottom;
		this.left = left;
		this.right = right;
		this.value = value;
		this.orientation.add(this.bottom);
		this.orientation.add(this.left);
		this.orientation.add(this.right);
	}

	/**
	 * A method that returns a Piece object with the given orientation configuration
	 * 
	 * @param orientationInput List of ColorDefinition Objects that will be used to
	 *                         define the colors of a Piece
	 * @param value            The intrinsic value of a Piece object
	 */
	private Piece(List<ColorDefinition> orientationInput, int value) {
		this(orientationInput.get(0), orientationInput.get(1), orientationInput.get(2), value);
	}

	/**
	 * Alternative initializer for a Piece object
	 * 
	 * @param s
	 */
	public Piece(String s) {
		if (s.equals("null")) {
			orientation = new ArrayList<>();
			this.value = 0;
		} else {

			List<ColorDefinition> tempColors = new ArrayList<>();
			for (int i = 0; i < 3; i++) {

				for (ColorDefinition tempColor : ColorDefinition.values()) {
					if (Character.toString(s.charAt(i)).equals(Character.toString(tempColor.toString().charAt(0)))) {
						tempColors.add(tempColor);
					}
				}
			}

			orientation = new ArrayList<>();
			this.bottom = tempColors.get(0);
			this.left = tempColors.get(1);
			this.right = tempColors.get(2);
			this.value = Character.getNumericValue(s.charAt(3));
			this.orientation.add(this.bottom);
			this.orientation.add(this.left);
			this.orientation.add(this.right);
		}
	}

	/**
	 * Rotates this Piece object and returns it
	 * 
	 * @return Rotated Piece object
	 */
	public Piece getRotated() {
		List<ColorDefinition> tempOrientation = new ArrayList<>(orientation);
		Collections.rotate(tempOrientation, 1);
		return new Piece(tempOrientation, value);
	}

	/**
	 * Rotates this Piece object twice and returns it
	 * 
	 * @return Rotated (twice) Piece object
	 */
	public Piece getRotated2x() {
		List<ColorDefinition> tempOrientation = new ArrayList<>(orientation);
		Collections.rotate(tempOrientation, 2);
		return new Piece(tempOrientation, value);
	}

	/**
	 * rotate() This function rotates the Piece object. Note: flipping is not taken
	 * into account
	 *
	 * @return Adjusts the orientation of this Piece object
	 */
	public void rotate() {

		this.orientation = getRotated().getColors();
	}

	/**
	 * This function rotates the Piece object twice. Note: flipping is not taken
	 * into account
	 *
	 * @return Adjusts the orientation of this Piece object
	 */

	public void rotate2x() {

		this.orientation = getRotated2x().getColors();

	}

	/**
	 * Return the orientation of this piece
	 *
	 * @return ArrayList of ColorDefinition objects that define the color
	 *         orientation of this Piece
	 */
	public ArrayList<ColorDefinition> getColors() {
		return this.orientation;
	}

	/**
	 * Return the number of points of given Piece
	 *
	 * @return An integer with the given points of the Piece
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Function to check if tile is joker
	 *
	 * @return Boolean verifying if a Piece object is Joker
	 */
	public boolean isJoker() {
		return (orientation.get(0).equals(ColorDefinition.WHITE));
	}
	
	/**
	 * Checks if the given Object is equivalent to this Piece
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Piece) {
			Piece p = (Piece) obj;
			return (this.getColors().equals(p.getColors()) && this.getValue() == p.getValue());
		} else {
			return false;
		}
	}
	
	/**
	 * Checks if the given Object is equivalent to this Piece in all its orientations
	 * @param p Piece object to be checked against this Piece
	 * @return An integer indicating in which direction the equality occurs
	 */
	public int equalsRotated(Piece p) {
		if (this.equals(p)) {
			return 0;
		} else if (this.equals(p.getRotated())) {
			return 1;
		} else if (this.equals(p.getRotated2x())) {
			return 2;
		} else {
			return -1;
		}
	}

	/**
	 * A method that will print this object in a redable manner
	 */
	@Override
	public String toString() {
		String result = "";
		for (int pieceColorIndex = 0; pieceColorIndex <= 2; pieceColorIndex++) {
			Character letter = orientation.get(pieceColorIndex).toString().charAt(0);
			result = result.concat(letter.toString());
		}
		result = result.concat(Integer.toString(value));
		return result;
	}

	/**
	 * A method that will print this object in a redable manner
	 */
	public String toPrinterString() {
		if (value == 0) {
			return PiecePrinter.printEmptyPiece();
		} else {
			return PiecePrinter.printPiece(value, this.bottom.toString().charAt(0), this.left.toString().charAt(0),
					this.right.toString().charAt(0));
		}
	}
}

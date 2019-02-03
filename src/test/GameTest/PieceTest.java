package test.GameTest;

import model.ColorDefinition;
import model.EmptyBagException;
import model.Piece;
import model.TileBag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

class PieceTest {

	private Piece p;
	private Piece p2;
	private Piece anotherPiece;
	private Piece rotated1x;
	private Piece rotated2x;
	private TileBag tilebag;

	private ColorDefinition left;
	private ColorDefinition right;
	private ColorDefinition bottom;

	@BeforeEach
	public void setup() {
		this.tilebag = new TileBag(36);
		this.p = new Piece(ColorDefinition.GREEN, ColorDefinition.RED, ColorDefinition.PURPLE, 1);
		p2 = new Piece("null");
		anotherPiece = new Piece("RRRY");
		left = p.getColors().get(0); // Green
		right = p.getColors().get(1); // Red
		bottom = p.getColors().get(2); // Purple

		// TODO Change order of bottom,left,right in all these tests
		this.rotated1x = new Piece(bottom, left, right, p.getValue());
		this.rotated2x = new Piece(right, bottom, left, p.getValue());

	}

	@Test
	void testGetRotateed() {
		assertEquals(rotated1x, p.getRotated());
		assertEquals(rotated2x, p.getRotated().getRotated());
	}

	@Test
	void testGetRotated2x() {
		assertEquals(rotated2x, p.getRotated2x());
		assertEquals(rotated2x, p.getRotated().getRotated().getRotated2x().getRotated());

	}

	@Test
	void testRotate() {
		Piece testPiece = p;
		Piece anotherPiece = new Piece(ColorDefinition.YELLOW, ColorDefinition.RED, ColorDefinition.PURPLE, 1);

		assertTrue(p.getColors().equals(testPiece.getColors()));
		testPiece.rotate();
		assertTrue(p.getColors().equals(testPiece.getColors()));
		assertFalse(p.getColors().equals(anotherPiece.getColors()));

	}

	@Test
	void testGetColors() {
		assertNotNull(p.getColors());
		assertTrue(p.getColors().get(0).equals(left));
		assertFalse(p.getColors().get(0).equals(right));
	}

	@Test
	void testGetValue() {
		Piece p4 = new Piece(left, right, bottom, 4);
		assertEquals(4, p4.getValue());
	}

	@Test
	void testIsJoker() {
		Piece p5 = new Piece(ColorDefinition.WHITE, ColorDefinition.WHITE, ColorDefinition.WHITE, 1);
		Piece p6 = new Piece(ColorDefinition.RED, ColorDefinition.RED, ColorDefinition.RED, 6);
		assertTrue(p5.isJoker());
		assertFalse(p6.isJoker());
	}

	@Test
	void testEquals() {
		Piece joker = new Piece(ColorDefinition.WHITE, ColorDefinition.WHITE, ColorDefinition.WHITE, 1);

		assertTrue(p.equals(p));
		assertFalse(p.equals(joker));

		assertTrue(p.equals(p.getRotated().getRotated2x()));
		assertFalse(p.equals(p.getRotated2x()));

		assertFalse(p.equals(joker.getRotated2x()));

	}

	// TODO: Strange behavior here
	@Test
	void testEqualsRotated() {
		assertEquals(0, p.equalsRotated(p));

		assertEquals(1, p.equalsRotated(this.rotated2x));
		assertEquals(2, p.equalsRotated(this.rotated1x));
		Piece joker = new Piece(ColorDefinition.WHITE, ColorDefinition.WHITE, ColorDefinition.WHITE, 1);

		assertEquals(-1, p.equalsRotated(joker));
	}
	@Test
	void testToPrinterString() {
		String printed = p.toPrinterString();
		assertNotNull(printed);
	}
	
	@Test
	void testToString() {
		String printed = p.toString();
		assertNotNull(printed);
	}
	
	
	@Test
	public void testExpectedException(){
	    TileBag tbg = new TileBag(1);
	    assertNotNull(tbg);
	    try {
			tbg.takeRandomPiece();
		} catch (EmptyBagException expected) {
			assertNotNull(expected.getMessage());
		}
	}
	

}

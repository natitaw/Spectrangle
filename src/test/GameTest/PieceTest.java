package test.GameTest;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Game.BoardLocation;
import Game.ColorDefinition;
import Game.Piece;
import Game.Board;


class PieceTest {
	
	private Board b;
	private Piece p;
	private Piece p2;
	private ColorDefinition left; 
	private ColorDefinition right; 
	private ColorDefinition bottom; 
	
	
	@BeforeEach
	public void setup() {
		this.b = new Board();
		this.p = b.getTileBag().getRandomPiece();
		
		left = p.getOrientation().get(0);
		right = p.getOrientation().get(1);
		bottom = p.getOrientation().get(2);
		
		this.p2 = new Piece(bottom, left, right, p.getPoint());
		}

	@Test
	void testRotate() {
		p.rotate();
		assertEquals(p.getOrientation(), p2.getOrientation());
	}
	
	@Test
	void testRotate2x() {
		p.rotate2x();
		p2.rotate();
		assertEquals(p.getOrientation(), p2.getOrientation());
	}
	
	@Test
	void testGetOrientation() {
		assertNotNull(p.getOrientation());
		//TODO: check
	}
	
	@Test
	void testGetPoint() {
		Piece p4 = new Piece(left,right,bottom,4);
		assertEquals(4, p4.getPoint());
	}
	
	@Test
	void testIsJoker() {
		Piece p5 = new Piece(ColorDefinition.WHITE, ColorDefinition.WHITE, ColorDefinition.WHITE, 1);
		Piece p6 = new Piece(ColorDefinition.RED, ColorDefinition.RED, ColorDefinition.RED, 6);
		assertTrue(p5.isJoker());
		assertFalse(p6.isJoker());
	}

}

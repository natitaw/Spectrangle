package test.GameTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import game.TileBag;
import game.Board;

class TileBagTest {
	
	private TileBag tg;
	private Board b;
	
	@BeforeEach
	public void setup() {
		this.b = new Board();
		this.tg = new TileBag();
	
	}

	@Test
	void testGetRandomPiece() {
		int a = tg.getBag().size();
		assertEquals(a, tg.getBag().size());
		assertNotNull(tg.getRandomPiece());
		assertEquals(a - 1, tg.getBag().size());
	}
	
	@Test
	void testPopulateBag() {
		assertNotNull(tg.getBag());
	}
	
	@Test
	void testGetBag() {
		assertNotNull(tg.getBag());
	}
	
	@Test
	void testGetNumberOfPieces() {
		assertEquals(36, tg.getBag().size());
		tg.getRandomPiece();
		assertEquals(35,tg.getBag().size());
	}

}

package test.GameTest;

import model.Board;
import model.BoardLocation;
import model.TileBag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardLocationTest {

    private BoardLocation b1;
    private BoardLocation b2;
    private Board b;


    @BeforeEach
    public void setup() {
        this.b = new Board(new TileBag(36));
        b.getTileBag().populateBag();
        //Normal Location
        b.movePiece(0, b.getTileBag().takeRandomPiece());
        b1 = b.getBoardLocation(0);
        //Bonus Location
        b.movePiece(10, b.getTileBag().takeRandomPiece());
        b2 = b.getBoardLocation(10);
    }

    @Test
    void testGetScorePoint() {
        assertEquals(1, b1.getScorePoint());
        assertEquals(2, b2.getScorePoint());
    }

    @Test
    void testGetLocation() {
        assertEquals(0, b1.getLocation());
        assertEquals(10, b2.getLocation());
    }

    @Test
    void testMovePiece() {
        BoardLocation b3 = new BoardLocation(11, 1);
        assertNull(b3.getPiece());
        b3.movePiece(b.getTileBag().takeRandomPiece());
        assertNotNull(b3.getPiece());
    }

    @Test
    void testIsBonusLocation() {
        assertTrue(b2.isBonusLocation());
        assertFalse(b1.isBonusLocation());
    }

    @Test
    void testIsEmptySpot() {
        BoardLocation b4 = new BoardLocation(11, 1);
        assertTrue(b4.isEmptySpot());
        b4.movePiece(b.getTileBag().takeRandomPiece());
        assertFalse(b4.isEmptySpot());
    }

}

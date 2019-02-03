package test.GameTest;

import model.BoardLocation;
import model.ColorDefinition;
import model.Piece;
import model.TileBag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardLocationTest {

    private TileBag tilebag;
    private BoardLocation b1;
    private BoardLocation b2;
    private Piece p1;
    private Piece p2;
    private Piece p3;


    @BeforeEach
    public void setup() {
        this.tilebag = new TileBag(36);
        tilebag.populateBag();
        this.b1 = new BoardLocation(0, 1);
        this.b2 = new BoardLocation(2, 6);

        Piece p1 = new Piece(ColorDefinition.WHITE, ColorDefinition.WHITE, ColorDefinition.WHITE, 1);
        Piece p2 = new Piece(ColorDefinition.RED, ColorDefinition.RED, ColorDefinition.RED, 6);
        Piece p3 = new Piece(ColorDefinition.GREEN, ColorDefinition.RED, ColorDefinition.PURPLE, 1);

    }

    @Test
    void testGetScorePoint() {
        assertEquals(1, b1.getScorePoint());
        assertEquals(6, b2.getScorePoint());
    }

    @Test
    void testGetPiece() {
        b1.movePiece(p1);
        b2.movePiece(p2);
        assertEquals(b1.getPiece(), p1);
        assertEquals(b2.getPiece(), p2);
    }

    @Test
    void testGetLocation() {
        assertEquals(0, b1.getLocation());
        assertEquals(2, b2.getLocation());
    }

    @Test
    void testMovePiece() {
        BoardLocation b1 = new BoardLocation(0, 1);
        Piece p1 = new Piece(ColorDefinition.WHITE, ColorDefinition.WHITE, ColorDefinition.WHITE, 1);
        assertNull(b1.getPiece());
        b1.movePiece(p1);
        assertNotNull(b1.getPiece());
    }

    @Test
    void testIsBonusLocation() {
        assertTrue(b2.isBonusLocation());
        assertFalse(b1.isBonusLocation());
    }

    @Test
    void testIsEmptySpot() {
        Piece p1 = new Piece(ColorDefinition.WHITE, ColorDefinition.WHITE, ColorDefinition.WHITE, 1);

        BoardLocation b1 = new BoardLocation(0, 1);
        b1.movePiece(p1);
        assertFalse(b1.isEmptySpot());
    }

}

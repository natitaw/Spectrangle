package test.GameTest;

import game.Board;
import game.ColorDefinition;
import game.Piece;
import game.TileBag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;


class PieceTest {

    private Board b;
    private Piece p;
    private Piece p2;
    private ColorDefinition left;
    private ColorDefinition right;
    private ColorDefinition bottom;


    @BeforeEach
    public void setup() {
        this.b = new Board(new TileBag(36));
        b.getTileBag().populateBag();
        this.p = b.getTileBag().takeRandomPiece();

        left = p.getColors().get(0);
        right = p.getColors().get(1);
        bottom = p.getColors().get(2);

        // TODO Change order of bottom,left,right in all these tests
        this.p2 = new Piece(left, bottom, right, p.getValue());

    }

    @Test
    void testRotate() {
        p.rotate();
        assertEquals(p.getColors(), p2.getColors());
    }

    @Test
    void testRotate2x() {
        p.rotate2x();
        p2.rotate();
        assertEquals(p.getColors(), p2.getColors());
    }

    @Test
    void testGetOrientation() {
        assertNotNull(p.getColors());
        //TODO: check
    }

    @Test
    void testGetPoint() {
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
    void testIsSamePiece() {
        assertTrue(p.isSamePiece(p));
        assertFalse(p.isSamePiece(p2));

        assertTrue(p.isSamePiece(p.getRotated()));
        assertTrue(p.isSamePiece(p.getRotated2x()));

        assertFalse(p.isSamePiece(p2.getRotated2x()));

    }

    @Test
    void testGetOrientaiton() {
        ArrayList<ColorDefinition> o = new ArrayList<>();
        o.add(ColorDefinition.BLUE);
        o.add(ColorDefinition.GREEN);
        o.add(ColorDefinition.YELLOW);

        Piece p7 = new Piece(ColorDefinition.BLUE,
            ColorDefinition.GREEN, ColorDefinition.YELLOW, 1);

        assertEquals(o.get(0), p7.getColors().get(0));
        assertEquals(o.get(1), p7.getColors().get(1));
        assertEquals(o.get(2), p7.getColors().get(2));

    }
}

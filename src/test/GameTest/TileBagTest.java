package test.GameTest;


import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TileBagTest {

    private TileBag tg;
    private Board b;
    private int randomInt;

    @BeforeEach
    public void setup() {
        this.b = new Board();
        this.tg = new TileBag(36);
        tg.populateBag();
        randomInt = (int) (Math.random() * 35);

    }

    @Test
    void testGetRandomPiece() throws EmptyBagException {
        int a = tg.getBag().size();
        assertEquals(a, tg.getBag().size());
        assertNotNull(tg.takeRandomPiece());
        assertEquals(a - 1, tg.getBag().size());
    }

    @Test
    void testViewPiece() throws EmptyBagException {
        assertNotNull(tg.viewPiece(randomInt));
        assertEquals(tg.viewPiece(randomInt), tg.takePiece(randomInt));

        assertEquals(tg.viewPiece(randomInt), tg.takePiece(randomInt));
    }

    @Test
    void testTakePiece() throws EmptyBagException {
        assertNotNull(tg.takePiece(randomInt));
        assertEquals(35, tg.getBag().size());
    }

    @Test
    void testFindPiece() throws EmptyBagException {
        Piece joker = new Piece(ColorDefinition.WHITE, ColorDefinition.WHITE, ColorDefinition.WHITE, 1);
        assertNotNull(tg.findPiece(joker));
        int a = tg.findPiece(joker);
        tg.takePiece(a);
        assertEquals(-1, tg.findPiece(joker));
    }

    @Test
    void testAddPiece() throws EmptyBagException {
        Piece joker = new Piece(ColorDefinition.WHITE, ColorDefinition.WHITE, ColorDefinition.WHITE, 1);

        tg.takePiece(randomInt);
        assertEquals(35, tg.getBag().size());
        tg.addPiece(joker);
        assertEquals(36, tg.getBag().size());
    }

    @Test
    void testPopulateBag() {
        assertNotNull(tg.getBag());
        assertEquals(36, tg.getBag().size());
    }

    @Test
    void testGetBag() {
        assertNotNull(tg.getBag());
    }

    @Test
    void testGetNumberOfPieces() throws EmptyBagException {
        assertEquals(36, tg.getBag().size());
        tg.takeRandomPiece();
        assertEquals(35, tg.getBag().size());
    }

}

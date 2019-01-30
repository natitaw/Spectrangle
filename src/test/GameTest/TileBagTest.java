package test.GameTest;

import game.Board;
import game.TileBag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TileBagTest {

    private TileBag tg;
    private Board b;

    @BeforeEach
    public void setup() {
        this.b = new Board(new TileBag(36));
        b.getTileBag().populateBag();
        this.tg = b.getTileBag();

    }

    @Test
    void testGetRandomPiece() {
        int a = tg.getBag().size();
        assertEquals(a, tg.getBag().size());
        assertNotNull(tg.takeRandomPiece());
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
        tg.takeRandomPiece();
        assertEquals(35, tg.getBag().size());
    }

}

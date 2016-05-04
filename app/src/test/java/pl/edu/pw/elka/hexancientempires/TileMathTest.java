package pl.edu.pw.elka.hexancientempires;

import org.junit.Test;

import static org.junit.Assert.*;
import static pl.edu.pw.elka.hexancientempires.TileMath.*;

public class TileMathTest {

    @Test
    public void test_tileLocation() throws Exception {
        assertEquals(new Point(0, 0), tileLocation(0, 0));
        assertEquals(new Point(0, TILE_HEIGHT), tileLocation(0, 1));
        assertEquals(new Point(TILE_WIDTH * 3 / 4, TILE_HEIGHT / 2), tileLocation(1, 0));
        assertEquals(new Point(TILE_WIDTH * 3 / 4, TILE_HEIGHT * 3 / 2), tileLocation(1, 1));
    }
}
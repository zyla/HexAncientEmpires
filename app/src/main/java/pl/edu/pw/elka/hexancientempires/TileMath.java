package pl.edu.pw.elka.hexancientempires;

/**
 * Created by zyla on 5/3/16.
 *
 * Tile-screen coordinate system conversions.
 *
 * This code handles two coordinate systems:
 *
 *  - "map" - just tile numbers
 *  - "pixel" - offset from top-left corner of tile (0, 0), as in pixels.
 *
 * It doesn't handle camera projection.
 */
public class TileMath {
    public static final int TILE_SIDE = 64;
    public static final int TILE_WIDTH = TILE_SIDE * 2;
    public static final int TILE_HEIGHT = (int) (TILE_SIDE * 1.73);

    public static Rect visibleTiles(Rect visibleArea) {
        // TODO broken
        return new Rect(-10, -10, 10, 10);
    }

    public static Point tileCenter(int mapX, int mapY) {
        Point pt = tileLocation(mapX, mapY);
        return new Point(pt.x + TILE_WIDTH / 2, pt.y + TILE_HEIGHT / 2);
    }

    public static Point tileLocation(int mapX, int mapY) {
        return new Point(TILE_WIDTH * 3 / 4 * mapX,
                TILE_HEIGHT * mapY + (mapX & 1) * TILE_HEIGHT / 2);
    }

    public static Point tileHitTest(int x, int y) {
        // TODO broken
        return new Point(0, 0);
    }
}

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
        int topLeftTile = 

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
        // TODO broken trying to fix
        //source http://stackoverflow.com/questions/7705228/hexagonal-grids-how-do-you-find-which-hexagon-a-point-is-in
        int col =  x / (TILE_WIDTH*3/4);
        int row =  (y - (col & 1)* TILE_HEIGHT/2) / TILE_HEIGHT;

        int relX = x - (col * (TILE_WIDTH*3/4));
        int relY = y - row * TILE_HEIGHT - (col & 1) * (TILE_HEIGHT / 2);

        // y = a * x + b;
        double b =TILE_HEIGHT / 2;
        double a = -2 * TILE_HEIGHT / TILE_WIDTH;
        // double a2 =  2 * TILE_HEIGHT / TILE_WIDTH; //== -a
        // Work out if the point is on left side either of the hexagon's left edges
        if (relY < (a * relX) + b) // top left edge
        {
            if (col % 2 == 0)
                row--;
            col--;
        }
        else if (relY > ( -a * relX) - b) // bottom left edge
        {
            if (col % 2 != 0)
                row++;
            col--;
        }

        return new Point(col,row);
    }
}

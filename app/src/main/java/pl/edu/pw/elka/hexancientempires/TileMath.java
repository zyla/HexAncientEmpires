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
    public static final int MARGIN = 1;

    public static final int TOPSIDE = 0;
    public static final int TOPRIGHT = 1;
    public static final int BOTRIGHT = 2;
    public static final int BOTSIDE = 3;
    public static final int BOTLEFT = 4;
    public static final int TOPLEFT = 5;


    public static Rect visibleTiles(Rect visibleArea) {
        Point topLeft = tileHitTest(visibleArea.left, visibleArea.top);
        Point botRight = tileHitTest(visibleArea.right, visibleArea.bottom);

        return new Rect(topLeft.x-MARGIN,topLeft.y-MARGIN, botRight.x+MARGIN , botRight.y+MARGIN);
    }

    public static Point tileCenter(int mapX, int mapY) {
        Point pt = tileLocation(mapX, mapY);
        return new Point(pt.x + TILE_WIDTH / 2, pt.y + TILE_HEIGHT / 2);
    }

    public static Point tileLocation(int mapX, int mapY) {
        return new Point(TILE_WIDTH * 3 / 4 * mapX,
                TILE_HEIGHT * mapY + (mapX & 1) * TILE_HEIGHT / 2);
    }

    public static Point tileLocation(Point loc) {
        return tileLocation(loc.x, loc.y);
    }

    public static Point neighbour(Point loc, int side) {
        int even = (loc.x + 1) % 2;
        switch(side) {
            case TOPSIDE:
                return new Point(loc.x , loc.y - 1);
            case TOPRIGHT:
                return new Point(loc.x + 1 , loc.y - even);
            case BOTRIGHT:
                return new Point(loc.x + 1 , loc.y + 1 - even );
            case BOTSIDE:
                return new Point(loc.x , loc.y + 1);
            case BOTLEFT:
                return new Point(loc.x - 1 , loc.y + 1 - even );
            case TOPLEFT:
                return new Point(loc.x - 1 , loc.y - even );
        }
        throw new  IndexOutOfBoundsException("Tile of this index don't exists in map");
    }

    public static Point tileHitTest(int x, int y) {
        int col = (int)Math.floor(((double) x) / (TILE_WIDTH * 3/4));
        int row = (int)Math.floor(((double)y - (col & 1)* TILE_HEIGHT/2) / TILE_HEIGHT);
       // +((y>=0)?1:-1)*
        int relX = x - (col * (TILE_WIDTH*3/4));
        int relY = y - row * TILE_HEIGHT - (col & 1) * (TILE_HEIGHT / 2);

        // y = a * x + b;
        double b = TILE_HEIGHT / 2;
        double a = -2 * TILE_HEIGHT / TILE_WIDTH;
        // double a2 =  2 * TILE_HEIGHT / TILE_WIDTH; //== -a // Work out if the point is on left side either of the hexagon's left edges
        if (relY < (a * relX) + b) // top left edge
        {
            if (col % 2 == 0)
                row--;
            col--;
        }
        else if (relY > ( -a * relX) + b) // bottom left edge
        {
            if (col % 2 != 0)
                row++;
            col--;
        }

        return new Point(col,row);
    }
}

package pl.edu.pw.elka.hexancientempires;

/**
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
    public int TILE_SIDE ;
    public int TILE_WIDTH;
    public int TILE_HEIGHT;
    public static final int MARGIN = 1;

    public static final int TOPSIDE = 0;
    public static final int TOPRIGHT = 1;
    public static final int BOTRIGHT = 2;
    public static final int BOTSIDE = 3;
    public static final int BOTLEFT = 4;
    public static final int TOPLEFT = 5;

    public TileMath(int TILE_SIDE) {
        this.TILE_SIDE = TILE_SIDE;
        TILE_WIDTH = TILE_SIDE * 2;
        TILE_HEIGHT = (int) (TILE_SIDE * 1.732);
    }
    public void update(int TILE_SIDE) {
        this.TILE_SIDE = TILE_SIDE;
        TILE_WIDTH = TILE_SIDE * 2;
        TILE_HEIGHT = (int) (TILE_SIDE * 1.732);
    }
    /**
     * @return rect of visible hexagons in hexagon geometry
     */
    public  Rect visibleTiles(Rect visibleArea) {
        Point topLeft = tileHitTest(visibleArea.left, visibleArea.top);
        Point botRight = tileHitTest(visibleArea.right, visibleArea.bottom);

        return new Rect(topLeft.x-MARGIN,topLeft.y-MARGIN, botRight.x+MARGIN , botRight.y+MARGIN);
    }

    /**
     * @return center of hexagon in pixels
     */
    public static Point tileCenter(int mapX, int mapY, int w, int h) {
        Point pt = tileLocation(mapX, mapY, w, h);
        return new Point(pt.x + w / 2, pt.y + h / 2);
    }

    /**
     * @return tile location in pixels based on tile position on the map
     */
    public static Point tileLocation(int mapX, int mapY, int w, int h) {
        return new Point(w * 3 / 4 * mapX,
                h * mapY + (mapX & 1) * h / 2);
    }


    /**
     * @return tile position based on index in map
     */
    public static Point tileLocation(Point loc, int w, int h) {
        return tileLocation(loc.x, loc.y, w , h);
    }


    /**
     * @param loc location of the base tile
     * @param side number of side of base tile numbered clockwise
     * @return coordinates of certain neighbour in map coordinates
     */
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
        throw new  IndexOutOfBoundsException("There is no side numbered like this");
    }


    /**
     * @return map coordinate of tile under given pixels
     */
    public  Point tileHitTest(int x, int y) {
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

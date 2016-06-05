package pl.edu.pw.elka.hexancientempires;

/**
 * <br>public Tile(int type);<br/>
 * <br>public Tile(Tile tile, Unit unit);<br/>
 *
 * Simple class representing tile
 * Created by Tomek on 31.05.16.
 */
public class Tile {
    public static final int NONE = 0;
    public static final int CASTLE = 1;
    public static final int GRASS = 2;
    public static final int MOUNTAIN = 3;
    public static final int ROAD = 4;
    public static final int TREE = 5;
    public static final int WATER = 6;


    public int type;
    public Unit unit;

    public Tile(int type) {
        this.type = type;
    }
    public Tile(Tile tile) {
        this.type = tile.type;
        this.unit = tile.unit;
    }

    public Tile(Tile tile, Unit unit) {
        this.type = tile.type;
        this.unit = unit;
    }
}

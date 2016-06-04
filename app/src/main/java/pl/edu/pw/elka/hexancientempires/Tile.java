package pl.edu.pw.elka.hexancientempires;

/**
 * <br>public Tile(int type);<br/>
 * <br>public Tile(Tile tile, Unit unit);<br/>
 * <br>Tile(Tile tile, boolean doColour,Point previous);<br/>
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


    /*public enum TileType {
        ROAD, TREE, WATER, MOUNTAIN, GRASS, CASTLE, NONE;

    }//none is added to make holes is the map idk If useful
    */
    public int type;
    public Unit unit;

    boolean displayRange;
    Point previous;

    public Tile(int type) {
        this.type = type;
        this.displayRange = false;
    }
    public Tile(Tile tile) {
        this.type = tile.type;
        this.unit = tile.unit;
        this.displayRange = tile.displayRange;
        this.previous = tile.previous;
    }

    public Tile(Tile tile, Unit unit) {
        this.type = tile.type;
        this.unit = unit;
        this.displayRange = tile.displayRange;
        this.previous = tile.previous;
    }

    public Tile(Tile tile, boolean doColour,Point previous ) {
        this.type = tile.type;
        this.unit = tile.unit;
        this.displayRange = doColour;
        this.previous = previous;
    }

    public boolean isDisplayRange() {
        return displayRange;
    }


    public Point getPrevious() {
        return previous;
    }


}

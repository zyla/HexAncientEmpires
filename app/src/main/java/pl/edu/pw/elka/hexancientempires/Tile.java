package pl.edu.pw.elka.hexancientempires;

/*
 * Created by Tomek on 31.05.16.
 */
public class Tile {
    public static final int NONE = 0;
    public static final int ROAD = 1;
    public static final int WATER = 2;
    public static final int MOUNTAIN = 3;
    public static final int GRASS = 4;
    public static final int CASTLE = 5;
    public static final int TREE = 6;


    /*public enum TileType {
        ROAD, TREE, WATER, MOUNTAIN, GRASS, CASTLE, NONE;

    }//none is added to make holes is the map idk If useful
    */

    public int type;

    public Tile(int type) {
        this.type = type;
    }

}

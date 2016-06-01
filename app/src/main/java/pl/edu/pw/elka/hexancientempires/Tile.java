package pl.edu.pw.elka.hexancientempires;

/*
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

    public Tile(int type) {
        this.type = type;
    }

}

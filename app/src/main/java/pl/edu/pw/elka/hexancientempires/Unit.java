package pl.edu.pw.elka.hexancientempires;

/**
 * Created by tomek on 01.06.16.
 */
public class Unit {
    public static final int WARRIOR     = 0;
    public static final int ARCHER      = 1;
    public static final int WATERELEM   = 2;
    public static final int WIZZARD     = 3;
    public static final int WISP        = 4;
    public static final int WOLF        = 5;
    public static final int GOLEM       = 6;
    public static final int CATAPULT    = 7;
    public static final int DRAGON      = 8;
    public static final int SKELETON    = 9;
    public static final int KING        = 10;


    public int type;
    public int playerID;

    public Unit(int type, int id) {
        this.type = type; this.playerID = id;
    }

}

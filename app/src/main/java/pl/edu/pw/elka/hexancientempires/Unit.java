package pl.edu.pw.elka.hexancientempires;

/**
 * simple class representing unit
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


    private int type;
    private int playerID;
    private int hp;
    private Point loc;


    public Unit(int type, int playerID, Point loc) {
        this.type = type;
        this.playerID = playerID;
        this.hp = UnitMath.STARTING_HP;
        this.loc = loc;
    }

    public int getType() {
        return type;
    }

    public int getPlayerID() {
        return playerID;
    }

    public int getHp() {
        return hp;
    }

    public Point getLoc() {
        return loc;
    }

    public void setLoc(Point loc) {
        this.loc = loc;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }
}

package pl.edu.pw.elka.hexancientempires;

/**
 * simple class representing unit
 * contains also unit identifiers
 */
public class Unit {
    public static final int WARRIOR     = 0;
    public static final int ARCHER      = 1;
    public static final int LIZARD      = 2;
    public static final int WIZARD      = 3;
    public static final int WISP        = 4;
    public static final int WOLF        = 5;
    public static final int GOLEM       = 6;
    public static final int CATAPULT    = 7;
    public static final int DRAGON      = 8;
    public static final int KING        = 9;
    public static final int SKELETON    = 10;

    /**
     * Returns name of the unit to display it in message
     */
    @Override
    public String toString() {
        switch (type){
            case WARRIOR:   return "Warrior";
            case ARCHER:    return "Archer";
            case LIZARD:    return "Lizard";
            case WIZARD:    return "Wizard";
            case WISP:      return "Wisp";
            case WOLF:      return "Wolf";
            case GOLEM:     return "Golem";
            case CATAPULT:  return "Catapult";
            case DRAGON:    return "Dragon";
            case KING:      return "King";
            case SKELETON:  return "Skeleton";
            default:        return "";
        }
    }


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

package pl.edu.pw.elka.hexancientempires;

import java.util.ArrayList;

/**
 * Created by tomek on 02.06.16.
 * this class is whole about fighting
 * but fighting is highly dependant on tile that unit is standing at
 * so more of a tile.unit fighting
 */
public class UnitMath {
    public static final int STARTING_HP = 100;
    public static final int ATACKED_DIE = -1;//no need if attack removes units
    public static final int ATACKING_DIE = 1;
    public static final int NOONE_DIE = 0;


    public static final int[] unitDamege = {
        50,//WARRIOR
        50,//ARCHER
        50,//WATERELEM
        50,//WIZZARD
        50,//WISP
        50,//WOLF
        50,//GOLEM
        50,//CATEPULT
        50,//DREGON
        50,//SKELETON
        50 //KING
    };

    public static final int[] unitRange = {
        1,//WARRIOR
        2,//ARCHER
        1,//WATERELEM
        1,//WIZZARD
        2,//WISP
        1,//WOLF
        1,//GOLEM
        5,//CATEPULT
        2,//DREGON
        1,//SKELETON
        1 //KING
    };

    public static final int[] unitSpeed = {
        3,//WARRIOR
        3,//ARCHER
        3,//WATERELEM
        2,//WIZZARD
        3,//WISP
        5,//WOLF
        2,//GOLEM
        2,//CATEPULT
        6,//DREGON
        3,//SKELETON
        3 //KING
    };

    //i have to change value of hp in one unit, but idk if possible in java
    public int atack(Tile attacking, Tile attacked){
        int attackValue1 = unitDamege[attacking.unit.type];
        int attackedHp = attacked.unit.hp;
        if(attackedHp <= attackValue1)
            return ATACKED_DIE;
        attacked.unit.hp = attackedHp -attackValue1;
        int attackingHp = attacking.unit.hp;
        int attackValue2 = unitDamege[attacked.unit.type];
        if(attackingHp <= attackValue2)
            return ATACKING_DIE;
        attacking.unit.hp = attackingHp - attackValue2;
        return NOONE_DIE;
    }
}

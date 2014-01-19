package KAT;
//
// MasterThief.java
// kingsandthings/
// @author Brandon Schurman
//

public class MasterThief extends Creature
{
    /**
     * CTOR 
     */
    public MasterThief(){
        super("path/to/front.png", "path/to/back.png", "Master Thief", 
                "any", 4, false, false, false, false);
        setType("SpecialCharacter");
    }

    public void stealGold( /* param later */ ){
        // TODO
    }

    public void stealRandomCounter( /* param */ ){
        // TODO
    }
}

package KAT;
//
// AssassinPrimus.java
// kingsandthings/
// @author Brandon Schurman
//

public class AssassinPrimus extends SpecialCharacter
{
    /**
     * CTOR
     */
    public AssassinPrimus(){
        super("path/to/front.png", "path/to/back.png", "Assassin Primus", 
                4, false, false, false, false);
    }

    /**
     * The assassin primus can attack any creature or special character
     * on the board during the special powers phase.
     * @param creature - the character on the board to attack
     */ 
    void attack( Creature creature ){
        creature.inflict();
    }
}

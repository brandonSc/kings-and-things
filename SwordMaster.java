package KAT;
//
// SwordMaster.java
// kingsandthings/
// @author Brandon Schurman
//

public class SwordMaster extends SpecialCharacter
{
    /**
     * CTOR
     */
    public SwordMaster(){
        super("path/to/front.png", "path/to/back.png", "Sword Master", 
                4, false, false, false, false);
    }

    /**
     * SwordMaster cannot take damage on a roll of 2 through 5.
     * Gets saving throw for one hit applied per each combat round
     */
    void inflict( int roll ){
        ;; // TODO dont really understand how this will work yet..
    }
}

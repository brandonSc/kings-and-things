package KAT;
//
// BaronMunchausen.java
// kingsandthings/
// @author Brandon Schurman
//
import java.util.ArrayList;

public class BaronMunchausen extends SpecialCharacter
{
    /**
     * CTOR
     */
    public BaronMunchausen(){
        super("path/to/front.png", "path/to/back.png", "Baron Munchausen",
                4, false, false, false, false);
    }

    /**
     * The Baron Munchausen inflicts one damage to each fort before battle.
     * @param forts - a set of all forts contained on a hex (datatype may change)
     */
    void attack( ArrayList<Fort> forts ){
        for( Fort f : forts ){
            f.inflict();
        }
    }
}

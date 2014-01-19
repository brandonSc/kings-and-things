//
// BaronMunchausen.java
// kingsandthings/
// @author Brandon Schurman
//
import java.util.ArrayList;

public class BaronMunchausen extends Creature
{
    /**
     * CTOR
     */
    public BaronMunchausen(){
        super("path/to/front.png", "path/to/back.png", "Baron Munchausen",
                "any", 4, false, false, false, false);
        setType("SpecialCharacter");
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

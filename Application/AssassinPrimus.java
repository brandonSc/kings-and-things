package KAT;
//
// AssassinPrimus.java
// kingsandthings/
// @author Brandon Schurman
//

public class AssassinPrimus extends SpecialCharacter implements Performable
{
    /**
     * CTOR
     */
    public AssassinPrimus(){
        super("Images/Hero_AssassinPrimus.png", "Images/Creature_Back.png", "Assassin Primus", "", 4, false, false, false, false);
        setType("Special Character");
    }

    /**
     * The assassin primus can attack any creature or special character
     * on the board during the special powers phase.
     * @param creature - the character on the board to attack
     */ 
    void attack( Creature creature ){
        creature.inflict();
    }

    public void specialAbility() {
        GameLoop.getInstance().pause();
        Game.getHelpText().setText("Assassin Primus ability");

        try { Thread.sleep(1000); } catch( Exception e ){ return; }

        System.out.println("assassin primus done");

        GameLoop.getInstance().unPause();

        System.out.println("game is unpaused");
    }

    public void performAbility() {
        return;
    }

    public boolean hasSpecial() { return true; }
    public boolean hasPerform() { return false; }
}

package KAT;

import javafx.scene.image.Image;

/*
 * The Creature class inherits from the Piece class
 * 
 * Note* The creature "Genie" in the old game has been replaced with "Djinn" in the new
 */
public class Creature extends Piece implements Combatable 
{
	private int    	combatValue;
	private boolean flying;
	private boolean magic;
	private boolean charging;
	private boolean ranged;

	/**
	 * ----------Constructor
	 */
	public Creature( String front, String back, String name, 
            String terrainType, int combatValue, 
            boolean flying, boolean magic, boolean charging, boolean ranged ){
		super("Creature", front, "Images/creature_Back", name);
		setType(name);
		this.setTerrain(terrainType.toUpperCase());
		this.combatValue = combatValue;
		this.flying = flying;
		this.magic = magic;
		this.charging = charging;
		this.ranged = ranged;
	}


	/* 
	 * ----------Get/Set methods
	 */
	public void setName(String s) { super.setName(s); }
	public void setCombatValue(int i) { combatValue = i; }
	public void setFlying(boolean b) { flying = b; }
	public void setMagic(boolean b) { magic = b; }
	public void setCharging(boolean b) { charging = b; }
	public void setRanged(boolean b) { ranged = b; }
	
	public String getName() { return name; }
	public int getCombatValue() { return combatValue; }
	public Image getImage() { return image; }
	public boolean isFlying() { return flying; }
	public boolean isMagic() { return magic; }
	public boolean isCharging() { return charging; }
	public boolean isRanged() { return ranged; }
	/*
	 * ----------Instance methods
	 */
    /**
     * Call when this creature is hit during combat
     */ 
    public void inflict(){
        //TheCup.getInstance().addToCup(this); // return to cup
        // should remove this creature from the hex
    }

    /*
     * Silly method to generate a string based on a boolean. Used for the toString method
     */
    private String boolString(boolean b) {
    	String tmp;
    	if (b)
    		tmp = "Yes";
    	else
    		tmp = "No";
    	return tmp;
    }
    
    @Override
    public String toString() {
    	String str = name+"\nTerrain: "+ getTerrain()+"\nCombat Value: "+combatValue+"\nFlying? "+boolString(flying)+"\nMagic? "+boolString(magic)+"\nCharging? "+boolString(charging)+"\nRanged? "+boolString(ranged);
    	return str;
    }
	
	public Creature getClassInstance() { return this; }
}

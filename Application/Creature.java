package KAT;

import javafx.scene.image.Image;

/*
 * The Creature class inherits from the Piece class
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
		super("Creature", front, back, name);
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
	// public void setType(String s) {
	// 	super.setName(s);
 //    	switch (name) {
 //    	case "DragonRider":
 //    		image = frozenWaste_DragonRider;
 //    		break;
 //    	case "ElkHerd":
 //    		image = frozenWaste_ElkHerd;
 //    		break;
 //    	default: 
 //    		name = "Unknown Creature";
 //    		image = creature_Back;
 //    		break;
 //    	}
	// }
	
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
    
    @Override
    public String toString() {
    	return name;
    }
	
	public Creature getClassInstance() { return this; }
}

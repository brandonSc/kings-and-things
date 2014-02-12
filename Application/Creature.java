package KAT;

import javafx.scene.image.Image;

/*
 * The Creature class inherits from the Piece class
 * 
 * Note* The creature "Genie" in the old game has been replaced with "Djinn" in the new
 */
public class Creature extends Piece implements Combatable, Movable 
{
	protected static Image creature_Back = new Image("Images/Creature_Back.png");
	
	private int    	combatValue;
	private boolean flying;
	private boolean magic;
	private boolean charging;
	private boolean ranged;
	
	private boolean doneMoving;
	private int movesLeft;

	/**
	 * ----------Constructor
	 */
	public Creature( String front, String back, String name, 
            String terrainType, int combatValue, 
            boolean flying, boolean magic, boolean charging, boolean ranged ){
		super("Creature", front, "Images/creature_Back", name);
		setName(name);
		this.doneMoving = false;
		this.movesLeft = 4;
		this.setTerrain(terrainType.toUpperCase());
		this.combatValue = combatValue;
		this.flying = flying;
		this.magic = magic;
		this.charging = charging;
		this.ranged = ranged;
		this.imageBack = creature_Back;
		if (front != null && !front.equals(""))
			this.imageFront = new Image(front);
		else
			this.imageFront = creature_Back;
		this.resetMoves();
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
	public void setMovesLeft(int i) { movesLeft = i; }
	
	public String getName() { return name; }
	public static Image getBackImage() { return creature_Back; }
	
	/*
	 * ------------ Combatable methods
	 */
	public int getCombatValue() { return combatValue; }
	public boolean isFlying() { return flying; }
	public boolean isMagic() { return magic; }
	public boolean isCharging() { return charging; }
	public boolean isRanged() { return ranged; }
    /**
     * Call when this creature is hit during combat
     */ 
    public void inflict(){
        //TheCup.getInstance().addToCup(this); // return to cup
        // should remove this creature from the hex
    }

    /*
     * ------------Instance methods
     */
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

	
	/*
	 * ------------ Movable methods 
	 */
	public int movesLeft() { return movesLeft; }
	public boolean doneMoving() { return doneMoving; }
	public void resetMoves() { movesLeft = 4; }
	public void move(Terrain t) {
		movesLeft = movesLeft - t.getMoveCost();
		if (movesLeft <= 0)
			doneMoving = true;
	}
	public boolean canMoveTo(Terrain from, Terrain to) {
		if (from.compareTo(to) == 1 && this.movesLeft > 0 && !to.getType().equals("SEA") && !(this.movesLeft < 2 && (to.getType().equals("JUNGLE") || to.getType().equals("MOUNTAINS") || to.getType().equals("FOREST") || to.getType().equals("SWAMP"))))
			return true;
		else
			return false;
	}
}

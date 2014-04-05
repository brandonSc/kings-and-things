//
// SpecialCharacter.java
// kingsandthings/
// @author Brandon Schurman
// 
package KAT;

import javafx.scene.Group;
import javafx.scene.image.Image;
import java.util.HashMap;

/*
 * TODO:
 * Conform to piece, and implement inherited methods.
 * Create group for click reg etc
 */
public class SpecialCharacter extends Creature implements Combatable, Performable {
	
    private int combatValue;
    private boolean flying;
    private boolean magic;
    private boolean charging;
    private boolean ranged;

    public SpecialCharacter( String front, String back, String name, String terrainType, int combatValue, 
            boolean flying, boolean magic, boolean charging, boolean ranged ){
        super(front, back, name, terrainType, combatValue, flying, magic, charging, ranged);
        setType("Special Character");
    }

    public SpecialCharacter( HashMap<String,Object> map ){
        super(map);
        this.combatValue = (Integer)map.get("combatVal");
        this.flying = ((Integer)map.get("flying") == 1) ? true : false;
        this.magic = ((Integer)map.get("magic") == 1) ? true : false;
        this.charging = ((Integer)map.get("charging") == 1) ? true : false;
        this.ranged  = ((Integer)map.get("ranged") == 1) ? true : false;
    }

    public boolean inflict(){
        //TheCup.getInstance().addToCup(this.getName()); // return to cup
    	return false;
    }

    @Override
    public HashMap<String,Object> toMap(){
        HashMap<String,Object> map = super.toMap();
        map.put("combatVal", combatValue);
        map.put("flying", flying ? 1 : 0);
        map.put("magic", magic ? 1 : 0);
        map.put("charging", charging ? 1 : 0);
        map.put("ranged", ranged ? 1 : 0);
        return map;
    }

    public void setCombatValue( int combatValue ){ this.combatValue = combatValue; }
    public void setFlying( boolean flying ){ this.flying = flying; }
    public void setMagic( boolean magic ){ this.magic = magic; }
    public void setCharging( boolean charging ){ this.charging = charging; }
    public void setRanged( boolean ranged ){ this.ranged = ranged; }

    public int getCombatValue(){ return combatValue; }
    public boolean isMagic(){ return magic; }
    public boolean isCharging(){ return charging; }
    public boolean isRanged(){ return ranged; }
    public boolean isFlying(){ return flying; }

    public void performAbility() { return; }
    public void specialAbility() { return; }
    public boolean hasSpecial() { return false; }
    public boolean hasPerform() { return false; }

	// @Override
	// public Group getPieceNode() {
	// 	// TODO Auto-generated method stub
	// 	return super.getPieceNode();
	// }

	// @Override
	// public Image getImage() {
	// 	// TODO Auto-generated method stub
	// 	return super.getImage();
	// }

	// @Override
	// public void setAttackResult(boolean b) {
	// 	// TODO Auto-generated method stub
		
	// }

	// @Override
	// public void resetAttack() {
	// 	// TODO Auto-generated method stub
		
	// }

}

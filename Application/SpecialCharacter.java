//
// SpecialCharacter.java
// kingsandthings/
// @author Brandon Schurman
// 
package KAT;

public class SpecialCharacter extends Piece implements Combatable
{
    private int combatValue;
    private boolean flying;
    private boolean magic;
    private boolean charging;
    private boolean ranged;

    public SpecialCharacter( String front, String back, String name, int combatValue, 
            boolean flying, boolean magic, boolean charging, boolean ranged ){
        super("SpecialCharacter", front, back, name);
        this.combatValue = combatValue;
        this.flying = flying;
        this.magic = magic;
        this.charging = charging;
        this.ranged = ranged;
    }

    public void inflict(){
        //TheCup.getInstance().addToCup(this.getName()); // return to cup
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

}

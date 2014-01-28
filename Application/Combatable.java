package KAT;
//
// Combatable.java
// kingsandthings/
// @author Brandon Schurman
//

public interface Combatable 
{
    public void inflict();
    public int getCombatValue();
    public boolean isMagic();
    public boolean isRanged();
    public boolean isCharging();
    public boolean isFlying();
}

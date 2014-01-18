//
// Fort.java
// kingsandthings/Model/
// @author Brandon Schurman
//

public class Fort extends Piece implements Combatable 
{
    private int combatValue;
    private boolean neutralized;
    private String name;
    private boolean magic;
    private boolean ranged;

    public Fort(){
        // TODO
        super("frontimg", "backimg", "Fort");
        this.name = "";
        this.magic = false;
        this.neutralized = false;
        this.ranged = false;
    }

    public void inflict(){
        if( combatValue > 0 ){
            --combatValue;
            if( combatValue == 0 ){
                neutralized = true;
            }
        } 
    }

    public void upgrade(){
        if( combatValue < 4 ){
            combatValue++;
        }

        switch( combatValue ){
            case 1:
                neutralized = false;
                name = "Tower";
                break;
            case 2:
                name = "Nef";
                break;
            case 3:
                name = "Castle";
                ranged = true;
                break;
            case 4:
                name = "Citadel";
                ranged = false;
                magic = true;
                break;
        }
    }

    public int getCombatValue(){ return combatValue; }
    public boolean isRanged(){ return ranged; }
    public boolean isMagic(){ return magic; }
}

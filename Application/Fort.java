package KAT;
import javafx.scene.Group;
//
// Fort.java
// kingsandthings/
// @author Brandon Schurman
//
import javafx.scene.image.Image;

public class Fort extends Piece implements Combatable {
	
	private static Image tower, keep, castle, citadel;
	
    private int combatValue;
    private boolean neutralized;
    private boolean magic;
    private boolean ranged;

    public Fort(){
        super("frontimg", "backimg", "Fort", "");
        this.name = "Tower";
        this.magic = false;
        this.neutralized = false;
        this.ranged = false;
        this.combatValue = 1;
        this.imageFront = tower;
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
                name = "Keep";
                this.imageFront = keep;
                break;
            case 3:
                name = "Castle";
                ranged = true;
                this.imageFront = castle;
                break;
            case 4:
                name = "Citadel";
                ranged = false;
                this.imageFront = citadel;
                magic = true;
                break;
        }
    }

    public int getCombatValue(){ return combatValue; }
    public boolean isRanged(){ return ranged; }
    public boolean isMagic(){ return magic; }
    public boolean isCharging(){ return false; }
    public boolean isFlying(){ return false; }
    public Image getImage(){ return imageFront; }
    
    public static void setClassImages() {
    	tower = new Image("Images/Fort_Tower.png");
//    	keep = new Image("Images/Fort_Tower.png");
//    	castle = new Image("Images/Fort_Tower.png");
//    	citadel = new Image("Images/Fort_Tower.png");
    	
    }

	@Override
	public Group getPieceNode() {
		// TODO Auto-generated method stub
		return null;
	}
}

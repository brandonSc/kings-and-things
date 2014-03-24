package KAT;
import javafx.scene.Group;
import java.util.HashMap;
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

    public Fort( HashMap<String,Object> map ){
        super(map);
        this.combatValue = (Integer)map.get("combatVal");
        if( combatValue == 0 ){
            neutralized = true;
        } else {
            neutralized = false;
            --combatValue;
            upgrade();
        }
    }

    @Override
    public HashMap<String,Object> toMap(){
        HashMap<String,Object> map = super.toMap();
        map.put("neutralized", neutralized ? 1 : 0);
        map.put("magic", magic ? 1 : 0);
        map.put("ranged", ranged ? 1 : 0);
        return map;
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

    public void downgrade(){
        if( combatValue > 0 ){
            combatValue--;
        }

        switch( combatValue ){
            case 0:
                //remove from tile
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

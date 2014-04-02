package KAT;

import java.util.Iterator;

import javafx.scene.image.Image;

public class ClickObserver {

	private static ClickObserver uniqueInstance;
	
	private Terrain clickedTerrain, previouslyClickedTerrain;
	private Player activePlayer;
	private Piece clickedPiece;
	private Player clickedPlayer;
	
	/*
	 * String terrainFlag is used for determining what state the game is in when a terrain is clicked. 
	 * 
	 * 		""; 									no phase. Default value. 
     * 		"Disabled": 							no effect when clicked
	 * 		"Setup: deal": 							populates board
	 * 		"Setup: SelectStartTerrain":			setup phase. Player picking starting positions
	 * 		"Setup: SelectTerrain":       			player adding a tile
	 * 		"Setup: RemoveBadAdjWater"				player selecting a Sea terrain to replace
	 * 		"RecruitingThings: PlaceThings":		Place things from rack to board
     * 		"Movement: SelectMoveSpot":				Once creatures are selected from infoPanel, select terrain to move to
     * 		"Construction: ConstructFort":      	player picking a tile for construction
	 */
	private String terrainFlag;
	/*
	 * String creatureFlag is used for determining what state the game is in when a creature is clicked. 
	 * 
	 * 		""; 									no phase. Default value. 
	 * 		"Movement: SelectMovers":				Selecting Creatures to move from infoPanel
	 * 		"Combat: SelectCreatureToAttack":		Combat. Select opponents creature to attack
	 */
	 private String creatureFlag;
	 /*
	  * String playerFlag is used for determining what state the game is in when a player is clicked
	  * 
	  * 	"":										no phase. Default value
	  * 	"Attacking: SelectPlayerToAttackWith"	When more that two combatants on a terrain, select which one to attack
	  * 	"Combat: SelectPieceToGetHit"			Select a piece to get hit
	  */
	 private String playerFlag;
	
	/*
	 * --------- Constructor
	 */
	private ClickObserver () {
		terrainFlag = "";
		creatureFlag = "";
		playerFlag = "";
	}
	
	/*
	 * --------- Gets and Sets
	 */
	public Terrain getClickedTerrain() { return clickedTerrain; }
	public Player getActivePlayer() { return activePlayer; }
	public String getCreatureFlag() { return creatureFlag; }
	
	public void setClickedTerrain(Terrain t) { 
		previouslyClickedTerrain = clickedTerrain;
		clickedTerrain = t; 
    	
	}
	public void setClickedCreature(Creature c) { 
		clickedPiece = c;
		clickedPiece.getPieceNode().toFront();
	}
	public void setClickedPlayer(Player p) { 
		clickedPlayer = p;
	}
	public void setTerrainFlag(String s) { terrainFlag = s; }
	public void setCreatureFlag(String s) { creatureFlag = s; }
	public void setPlayerFlag(String s) { playerFlag = s; }
	public void setActivePlayer(Player p) { activePlayer = p; }
    
    
    public static ClickObserver getInstance(){
        if(uniqueInstance == null){
            uniqueInstance = new ClickObserver();
        }
        return uniqueInstance;
    }
	
	public void whenTerrainClicked() {
		switch (terrainFlag) {
		
			case "Setup: SelectStartTerrain":
				GameLoop.getInstance().addStartingHexToPlayer();
				break;
			case "Setup: SelectTerrain":
				GameLoop.getInstance().addHexToPlayer();
				break;
			case "Setup: RemoveBadAdjWater":
				Board.switchBadWater(clickedTerrain.getCoords());
				break;
	        case "Construction: ConstructFort":
	            GameLoop.getInstance().constructFort();
	            InfoPanel.showTileInfo(clickedTerrain);
	            clickedTerrain.moveAnim();
	            break;
	        case "Setup: deal":
	            Board.populateGameBoard();
	            PlayerRackGUI.updateRack();
	            terrainFlag = "";
	        case "Disabled":
	            PlayerRackGUI.updateRack();
	        	clickedTerrain = previouslyClickedTerrain;
	             // disable display of other terrain pieces
	             break;
			case "RecruitingThings: PlaceThings":
				GameLoop.getInstance().playThings(); 
				InfoPanel.showTileInfo(clickedTerrain);
	            clickedTerrain.moveAnim();
	            PlayerRackGUI.updateRack();
				break;
			case "Movement: SelectMoveSpot":
				
				terrainFlag = "Disabled";
				if (clickedTerrain.moveStack(previouslyClickedTerrain) != 0) {
					Board.removeCovers();
					Board.animStackMove(previouslyClickedTerrain, clickedTerrain, activePlayer.getName());
					InfoPanel.showTileInfo(previouslyClickedTerrain);
					clickedTerrain = previouslyClickedTerrain;
				}
					
				break;
			default:
				InfoPanel.showTileInfo(clickedTerrain);
	            clickedTerrain.moveAnim();
	            PlayerRackGUI.updateRack();
				break;
		}
	}
	
	public void whenCreatureClicked() {
		switch (creatureFlag) {
			case "Movement: SelectMovers":
				((Movable)clickedPiece).toggleAboutToMove();
		        Board.removeCovers();

		        for (Creature c : clickedTerrain.getContents(activePlayer.getName()).filterCreatures(clickedTerrain.getContents(activePlayer.getName()).getStack())) {
		        	if (c.isAboutToMove()) {
		        		Board.applyCovers(c);
		        	}
		        }
		        clickedPiece.getStackedIn().cascade(clickedPiece);
				
				if (clickedTerrain.countMovers(activePlayer.getName()) == 0)
					terrainFlag = "";
				else 
					terrainFlag = "Movement: SelectMoveSpot";
				break;
			case "Combat: SelectCreatureToAttackWith":
				clickedPiece.getStackedIn().cascade(clickedPiece);
				GameLoop.getInstance().setPieceClicked(clickedPiece);
				break;
			case "Combat: SelectPieceToGetHit":
				clickedPiece.getStackedIn().cascade(clickedPiece);
				GameLoop.getInstance().setPieceClicked(clickedPiece);
				break;
			default:
				clickedPiece.getStackedIn().cascade(clickedPiece);
				break;
		}
	}

	public void whenPlayerClicked() {
		switch (playerFlag) {
			case "Attacking: SelectPlayerToAttack":
				
				GameLoop.getInstance().setPlayerClicked(clickedPlayer);
				GameLoop.getInstance().unPause();
				
				break;
			default:
				break;
		
		}
	}
}

package KAT;

import java.util.Iterator;

import javafx.scene.image.Image;

public class ClickObserver {

	private static ClickObserver uniqueInstance;
	
	private Terrain clickedTerrain, previouslyClickedTerrain;
	private Player activePlayer;
	private Creature clickedCreature;
	
	/*
	 * String terrainFlag is used for determining what state the game is in when a terrain is clicked. 
	 * 
	 * 		""; 								no phase. Default value. 
     * 		"Disabled": 						no effect when clicked
	 * 		"Setup: deal": 						populates board
	 * 		"Setup: SelectStartTerrain":		setup phase. Player picking starting positions
	 * 		"Setup: SelectTerrain":       		player adding a tile
	 * 		"RecruitingThings: PlaceThings":	Place things from rack to board
     * 		"Movement: SelectMoveSpot":			Once creatures are selected from infoPanel, select terrain to move to
     * 		"Construction: ConstructFort":      player picking a tile for construction
	 */
	private String terrainFlag;
	/*
	 * String creatureFlag is used for determining what state the game is in when a creature is clicked. 
	 * 
	 * 		""; 								no phase. Default value. 
	 * 		"Movement: SelectMovers":			Selecting Creatures to move from infoPanel
	 * 		"Combat: SelectCreatureToAttack":	Combat. Select opponents creature to attack
	 */
	 private String creatureFlag;
	
	/*
	 * --------- Constructor
	 */
	private ClickObserver () {
		terrainFlag = "";
		creatureFlag = "";
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
		clickedCreature = c;
		clickedCreature.getPieceNode().toFront();
	}
	public void setTerrainFlag(String s) { terrainFlag = s; }
	public void setCreatureFlag(String s) { creatureFlag = s; }
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
	        case "Construction: ConstructFort":
	            GameLoop.getInstance().constructFort();
	            InfoPanel.showTileInfo(clickedTerrain);
	            clickedTerrain.moveAnim();
	            break;
	        case "Setup: deal":
	            Board.populateGameBoard(TileDeck.getInstance());
	        case "Disabled":
	        	clickedTerrain = previouslyClickedTerrain;
	             // disable display of other terrain pieces
	             break;
			case "RecruitingThings: PlaceThings":
				GameLoop.getInstance().playThings(); 
				InfoPanel.showTileInfo(clickedTerrain);
	            clickedTerrain.moveAnim();
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
				break;
		}
	}
	
	public void whenCreatureClicked() {
		switch (creatureFlag) {
			case "Movement: SelectMovers":
		        clickedCreature.toggleAboutToMove();
		        Board.removeCovers();
		        for (Creature c : clickedTerrain.getContents(activePlayer.getName()).getStack()) {
		        	if (c.isAboutToMove()) {
		        		Board.applyCovers(c);
		        	}
		        }
				clickedCreature.getStackedIn().cascade(clickedCreature);
				
				if (clickedTerrain.countMovers(activePlayer.getName()) == 0)
					terrainFlag = "";
				else 
					terrainFlag = "Movement: SelectMoveSpot";
				break;
			case "Combat: SelectCreatureToAttack":
				
				break;
			default:
				clickedCreature.getStackedIn().cascade(clickedCreature);
				break;
		}
	}
}

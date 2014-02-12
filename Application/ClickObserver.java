package KAT;

import javafx.scene.image.Image;

public class ClickObserver {

	private static ClickObserver uniqueInstance;
	
	private Terrain clickedTerrain, previouslyClickedTerrain;
	private Player activePlayer;
	private Creature clickedCreature;
	private int clickedI, clickedJ;
	
	/*
	 * String terrainFlag is used for determining what state the game is in when a terrain is clicked. 
	 * 
	 * 		""; 								no phase. Default value. 
	 * 		"Setup: deal": 						populates board
	 * 		"Setup: SelectStartTerrain":		setup phase. Player picking starting positions
	 * 		"Setup: SelectTerrain":       		player adding a tile
	 * 		"RecruitingThings: PlaceThings":	Place things from rack to board
     * 		"Movement: SelectMoveSpot":			Once creatures are selected from infoPanel, select terrain to move to
     * 		"Combat: disableTerrainSelection": 	player choosing an enemy piece to attack
     * 		"Construction: ConstructFort":      player picking a tile for construction
	 */
	private String terrainFlag;
	/*
	 * String creatureFlag is used for determining what state the game is in when a creature is clicked. 
	 * 
	 * 		""; 							no phase. Default value. 
	 * 		"Movement: SelectMovers":		Selecting Creatures to move from infoPanel
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
	
	public void setClickedTerrain(Terrain t) { 
		previouslyClickedTerrain = clickedTerrain;
		clickedTerrain = t; 
	}
	public void setClickedCreature(Creature c) { clickedCreature = c; }
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
            /*
<<<<<<< HEAD
		case "Terrain: SelectStartTerrain":
			GameLoop.getInstance().addStartingHexToPlayer();
			clickedTerrain.setOwnerImage();
			break;
		case "Terrain: SelectTerrain":
			GameLoop.getInstance().addHexToPlayer();
			clickedTerrain.setOwnerImage();
			break;
        case "Terrain: ConstructFort":
            GameLoop.getInstance().constructFort();
            InfoPanel.showTileInfo(clickedTerrain);
            break;
        case "TileDeck: deal":
            Board.populateGameBoard(TileDeck.getInstance());
            break;
        case "Combat: disableTerrainSelection":
             // disable display of other terrain pieces
             break;
		case "Terrain: PlaceThings":
			GameLoop.getInstance().playThings(); 
			InfoPanel.showTileInfo(clickedTerrain);
			break;
		case "InfoPanel: SelectMoveSpot":
			for (int i = 0; i < InfoPanel.getMovers().size(); i++) {
				clickedTerrain.addToStack(activePlayer.getName(), (Creature)InfoPanel.getMovers().get(i));
				previouslyClickedTerrain.removeFromStack(activePlayer.getName(), (Creature)InfoPanel.getMovers().get(i));
				activePlayer.addHexNoOwner(clickedTerrain);
			}
			InfoPanel.showTileInfo(clickedTerrain);
			terrainFlag = "";
			break;
		default:
			InfoPanel.showTileInfo(clickedTerrain);
			break;
=======
*/
			case "Setup: SelectStartTerrain":
				GameLoop.getInstance().addStartingHexToPlayer();
				clickedTerrain.setOwnerImage();
				break;
			case "Setup: SelectTerrain":
				GameLoop.getInstance().addHexToPlayer();
				clickedTerrain.setOwnerImage();
				break;
	        case "Construction: ConstructFort":
	            GameLoop.getInstance().constructFort();
	            InfoPanel.showTileInfo(clickedTerrain);
	            clickedTerrain.moveAnim();
	            break;
	        case "Setup: deal":
	            Board.populateGameBoard(TileDeck.getInstance());
	            break;
	        case "Combat: disableTerrainSelection":
	             // disable display of other terrain pieces
	             break;
			case "RecruitingThings: PlaceThings":
				GameLoop.getInstance().playThings(); 
				InfoPanel.showTileInfo(clickedTerrain);
	            clickedTerrain.moveAnim();
				break;
			case "Movement: SelectMoveSpot":
				
				for (int i = 0; i < InfoPanel.getMovers().size(); i++) {
					if (((Creature)InfoPanel.getMovers().get(i)).canMoveTo(previouslyClickedTerrain, clickedTerrain)) {
						((Creature)InfoPanel.getMovers().get(i)).move(clickedTerrain);
						clickedTerrain.addToStack(activePlayer.getName(), (Creature)InfoPanel.getMovers().get(i));
						previouslyClickedTerrain.removeFromStack(activePlayer.getName(), (Creature)InfoPanel.getMovers().get(i));	
				        activePlayer.addHexNoOwner(clickedTerrain);
					} else {
						InfoPanel.removeMover((Creature)InfoPanel.getMovers().get(i), i);
						i--;
					}
					
				}
				// If there is actually anything to move
				if (InfoPanel.getMovers().size() > 0) {
					// Image to be displayed while moving
					Image movingImg = InfoPanel.getMovers().get(0).getImage();
					Board.animStackMove(previouslyClickedTerrain, clickedTerrain, movingImg);
					InfoPanel.showTileInfo(clickedTerrain);
		            clickedTerrain.moveAnim();
		            terrainFlag = "";
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
		        terrainFlag = "Movement: SelectMoveSpot";
				break;
			default:
				break;
		}
	}
}

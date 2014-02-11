package KAT;

public class ClickObserver {

	private static ClickObserver uniqueInstance;
	
	private Terrain clickedTerrain, previouslyClickedTerrain;
	private Player activePlayer;
	private Creature clickedCreature;
	private int clickedI, clickedJ;
	
	/*
	 * String terrainFlag is used for determining what state the game is in when a terrain is clicked. 
	 * 
	 * 		""; 							no phase. Default value. 
	 * 		"TileDeck: deal": 				populates board
	 * 		"Terrain: SelectStartTerrain":	setup phase. Player picking starting positions
	 * 		"Terrain: SelectTerrain":       player adding a tile
     * 		"Terrain: ConstructFort":       player picking a tile for construction
     * 		"Combat: disableTerrainSelection": player choosing an enemy piece to attack
	 */
	private String terrainFlag;
	/*
	 * String terrainFlag is used for determining what state the game is in when a terrain is clicked. 
	 * 
	 * 		""; 							no phase. Default value. 
	 * 		"InfoPanel: SelectMovers":		Selecting Creatures to move
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
			}
			InfoPanel.showTileInfo(clickedTerrain);
			terrainFlag = "";
			break;
		default:
			InfoPanel.showTileInfo(clickedTerrain);
			break;
		}
	}
	
	public void whenCreatureClicked() {
		switch (creatureFlag) {
		case "InfoPanel: SelectMovers":
	        terrainFlag = "InfoPanel: SelectMoveSpot";
	        InfoPanel.addMover(clickedCreature);
			break;
		default:
			break;
		}
	}
}

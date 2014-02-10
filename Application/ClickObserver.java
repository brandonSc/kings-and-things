package KAT;

public class ClickObserver {

	private static ClickObserver uniqueInstance;
	
	private Terrain clickedTerrain;
	private Player activePlayer;
	
	/*
	 * String flag is used for determining what state the game is in. 
	 * 
	 * 		""; 							no phase. Default value. 
	 * 		"TileDeck: 						deal": populates board
	 * 		"Terrain: SelectStartTerrain":	setup phase. Player picking starting positions
	 * 		"Terrain: SelectTerrain":       player adding a tile
     * 		"Terrain: ConstructFort":       player picking a tile for construction
	 */
	private String flag;
//	private ArrayList<Terrain> click
	
	/*
	 * --------- Constructor
	 */
	private ClickObserver () {
		flag = "";
	}
	
	/*
	 * --------- Gets and Sets
	 */
	public Terrain getClickedTerrain() { return clickedTerrain; }
	
	public void setClickedTerrain(Terrain t) { clickedTerrain = t; }
	public void setFlag(String s) { flag = s; }
	public void setActivePlayer(Player p) { activePlayer = p; }
	
	
	public static ClickObserver getInstance(){
        if(uniqueInstance == null){
            uniqueInstance = new ClickObserver();
        }
        return uniqueInstance;
    }
	
	public void whenTerrainClicked() {
		switch (flag) {
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
            break;
		case "TileDeck: deal":
			Board.populateGameBoard(TileDeck.getInstance());
			break;
		case "Terrain: PlaceThings":
			GameLoop.getInstance().playThings();
			break;
		default:
			InfoPanel.showTileInfo(clickedTerrain);
			break;
		}
	}
}

package KAT;

public class ClickObserver {

    private static ClickObserver uniqueInstance;
    
    private Terrain clickedTerrain;
    private Player activePlayer;
    
    /*
     * String flag is used for determining what state the game is in. 
     * 
     *      "";                                no phase. Default value. 
     *      "TileDeck:                         deal": populates board
     *      "Terrain: SelectStartTerrain":     setup phase. Player picking starting positions
     *      "Terrain: SelectTerrain":          player adding a tile
     *      "Terrain: ConstructFort":          player picking a tile for construction
     *      "Terrain: PlaceThings":            player adds their things to a tile
     *      "Combat: disableTerrainSelection": player choosing an enemy piece to attack
     */
    private String flag;
//  private ArrayList<Terrain> click
    
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
        default:
            InfoPanel.showTileInfo(clickedTerrain);
            break;
        }
    }
}

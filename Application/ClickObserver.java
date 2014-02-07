package KAT;

public class ClickObserver {

	private static ClickObserver uniqueInstance;
	
	private Terrain clickedTerrain;
	private GameLoop theGameLoop;
	private Player activePlayer;
	
	/*
	 * int flag is used for determining what state the game is in. This might be better understood
	 * if the int is changed to String? ie. instead of 0, "setup" or "redPlayerSetup" etc...
	 * 
	 * 		-1; no phase. Default value. 
	 * 		0: setup phase. Player picking starting positions
	 */
	private int flag;
//	private ArrayList<Terrain> click
	
	/*
	 * --------- Constructor
	 */
	public ClickObserver () {
		flag = -1;
	}
	
	/*
	 * --------- Gets and Sets
	 */
	public Terrain getClickedTerrain() { return clickedTerrain; }
	
	public void setClickedTerrain(Terrain t) { clickedTerrain = t; }
	public void setGameLoop(GameLoop gl) { theGameLoop = gl; }
	public void setFlag(int i) { flag = i; }
	public void setActivePlayer(Player p) { activePlayer = p; }
	
	
	public static ClickObserver getInstance(){
        if(uniqueInstance == null){
            uniqueInstance = new ClickObserver();
        }
        return uniqueInstance;
    }
	
	public void whenTerrainClicked() {
		switch (flag) {
		case 0:
			GameLoop.getInstance().addHexToPlayer(activePlayer);
			clickedTerrain.setOwnerImage();
			break;
		default:
			InfoPanel.showTileInfo(clickedTerrain);
			break;
		}
	}
	
	
}

package KAT;

import java.util.HashMap;

import javafx.scene.Group;
import javafx.scene.layout.VBox;

public class PlayerBoard {

	private int numPlayers;
	private HashMap<String, Player> players;
	private HashMap<String, Group> playerDisplay;
	private VBox playerDisplayBox;
	
	
	public PlayerBoard() {
		players = new HashMap<String, Player>();
		playerDisplay = new HashMap<String, Group>();
		
		
		
	}
	
	
}

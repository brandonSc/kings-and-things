package KAT;

import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Board {
	
	private ArrayList<Terrain> terrains;
	private Group boardNode;
	private boolean showTiles; //Tiles upside down or not?
	private Hex hexClip;
	private double height = 650;
	
	/*
	 * Constructors
	 */
	public Board(BorderPane bp) {
		showTiles = false;
		boardNode = new Group();
		terrains = new ArrayList<Terrain>();
		Terrain.setBaseImages();
		generateHexes(bp);
		showTiles = false;
		
		
		
		bp.getChildren().add(boardNode);
		
	}
	
	/*
	 * getters and setters
	 */
	public ArrayList<Terrain> getTerrains() { return terrains; }
	public void setTerrains(ArrayList<Terrain> terrains) { this.terrains = terrains; }
	
	/*
	 * Creates the hex shapes used for clipping for both the board pane,
	 * and the child hexes within (terrain peices)
	 */
	private void generateHexes(BorderPane bp) {
		
		/*
		 * Set up large hex that defines the board:
		 */
	
		hexClip = new Hex(height, false);
		boardNode.setClip(hexClip.getHex());
		boardNode.relocate((bp.getWidth() - hexClip.getWidthNeeded())/2, (bp.getHeight() - hexClip.getHeightNeeded())/2);
		boardNode.getChildren().add(new Rectangle(Math.sqrt(3) * height, height, Color.LIGHTGRAY));
		
		/*
		 * Set up small hexes within larger hex:
		 */
		double smallHexSideLength = (height * 3)/(Math.sqrt(3)*22);
		Terrain.setSideLength(smallHexSideLength);
		
		// loop which creates the hex spaces...does not do so as the rules say yet
		for (int x = -3; x < 4; x++) {
			for (int y = -3; y < 4; y++) {
				for (int z = -3; z < 4; z++) {
					if (x + y + z == 0) 
						terrains.add(new Terrain(boardNode, "desert", new int[]{x, y, z}));		
				}
			}
		}	
		
	}
	
}

package KAT;

import java.util.ArrayList;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Board {
	
	private ArrayList<Terrain> terrains;
	private static Group boardNode;
	private boolean showTiles; //Tiles upside down or not?
	private Hex hexClip;
	private static Hex smallHexClip;
	private double height = 650;
	private static ImageView animView;
	
	/*
	 * Constructors
	 */
	public Board(BorderPane bp) {
		showTiles = false;
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
	 * Creates the hex shapes used for clipping for the board pane,
	 * Calculates the child hexes size (terrain peices)
	 * Sets up the hex shaped animation for a selected tile
	 */
	private void generateHexes(BorderPane bp) {
		
		/*
		 * Set up large hex that defines the board:
		 */
		hexClip = new Hex(height, false);
		boardNode = GroupBuilder.create()
				.clip(hexClip)
				.layoutX((bp.getWidth() - hexClip.getWidthNeeded())/2)
				.layoutY((bp.getHeight() - hexClip.getHeightNeeded())/2)
				.build();
		
		/*
		 * Calculate small hex size
		 */
		double smallHexSideLength = (height * 3)/(Math.sqrt(3)*22);
		Terrain.setSideLength(smallHexSideLength);
		smallHexClip = new Hex(smallHexSideLength * Math.sqrt(3), true);
		
		
		/* anim stuff  ------------------------------------------------------------*/
		animView = ImageViewBuilder.create()
				.fitHeight(smallHexClip.getHeightNeeded() * 1.08)
				.layoutY(smallHexClip.getHeightNeeded() * -0.03)
				.preserveRatio(true)
				.build();
		boardNode.getChildren().add(animView);
		
		final Image selectedImage[] = new Image[]{
    			new Image("Images/Hex_tile_select_0.png"),
    			new Image("Images/Hex_tile_select_1.png"),
    			new Image("Images/Hex_tile_select_2.png"),
    			new Image("Images/Hex_tile_select_3.png"),
    			new Image("Images/Hex_tile_select_4.png"),
    			new Image("Images/Hex_tile_select_5.png"),
    			new Image("Images/Hex_tile_select_6.png"),
    			new Image("Images/Hex_tile_select_7.png"),
    			new Image("Images/Hex_tile_select_8.png"),
    			new Image("Images/Hex_tile_select_9.png")
    		};
    	final Animation tileSelected = new Transition() {
    	     {
    	         setCycleDuration(Duration.millis(1000));
    	         setCycleCount(INDEFINITE);
    	     }
    	     protected void interpolate(double frac) {
    	         if ((int) Math.floor(frac * 10) < 10) {
    	        	 animView.setImage(selectedImage[(int) Math.floor(frac * 10)]); 
    	         } 
    	     }
    	};
    	tileSelected.play(); 
		/* end anim stuff  ------------------------------------------------------------*/

	}
	
	public void populateGameBoard (TileDeck td) {
		
		// loop which creates the hex spaces...does not do so as the rules say yet
		for (int x = -3; x < 4; x++) {
			for (int y = -3; y < 4; y++) {
				for (int z = -3; z < 4; z++) {
					if (x + y + z == 0) 
						terrains.add(td.getRandomTile().positionNode(boardNode, new int[]{x, y, z}));
				}
			}
		}
	}
	
	/*
	 * moves the animation for the selected tile piece
	 */
	public static void setSelectedAnimationLocation(int[] xyz) {
    	
		animView.relocate(1.5 * smallHexClip.getSideLength() * (xyz[0] + 3), (6 - xyz[1] + xyz[2]) * smallHexClip.getSideLength() * Math.sqrt(3)/2 + (0.8 * Math.sqrt(3)*smallHexClip.getSideLength())/6);
    	
    }
	
}

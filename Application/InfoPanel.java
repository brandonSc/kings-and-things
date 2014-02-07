package KAT;
/*
 * InfoPanel.java:
 * 
 * A panel on the left of the game that shows info on the object which was last clicked:
 * 
 * 		Tiles: Shows the tile type, and a list of Pieces on it
 * 		Players: maybe a small blurb about what they have got on the board etc...
 * 		More stuff:....
 * 
 * 
 */

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.text.TextBuilder;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;

public class InfoPanel {

	private static Group infoNode;//, textGroup;
	private static ImageView currentImageView, tileImageView, playerImageView;
	private static Image tileImage;
	private static int[] currentTileCoords;
	private static TileDeck tileDeck;
    private static Terrain currHex;
    private static ListView<String> piecesList;
	
    /*
	 * Constructors
	 */
	public InfoPanel (BorderPane bp, TileDeck td){
		
		tileDeck = td;
		currentTileCoords = new int[]{-1, -1, -1};
		infoNode = GroupBuilder.create()
				.children(RectangleBuilder.create()
						.width(bp.getWidth() * 0.2)
						.height(bp.getHeight() * 0.8)
						.fill(Color.LIGHTGRAY)
						.build())
				.layoutX(0)
				.layoutY(bp.getHeight() * 0.1)
				.clip(RectangleBuilder.create()
						.width(bp.getWidth() * 0.2 + 30)
						.height(bp.getHeight() * 0.8)
						.arcHeight(60)
						.arcWidth(60)
						.x(-30)
						.build())
				.build();
		bp.getChildren().add(infoNode);
		setUpImageViews();
        currHex = null;
        piecesList = new ListView<String>();
        piecesList.setItems(null);
        piecesList.setPrefWidth(100);
        piecesList.setPrefHeight(150);
        piecesList.setLayoutX(0);
        piecesList.setLayoutY(bp.getHeight() * 0.25);
        infoNode.getChildren().add(piecesList);
	}
	
	/*
	 * Displays tile info:
	 * 
	 * Type of terrain is displayed on top.
	 * List of Peices will be displayed
	 * Owner of tile etc
	 */
	public static void showTileInfo(Terrain t) {

		if (t.getCoords()[0] != currentTileCoords[0] || t.getCoords()[1] != currentTileCoords[1] || t.getCoords()[2] != currentTileCoords[2]) {
			
			// Image on top of panel 
			infoNode.getChildren().remove(currentImageView);
			tileImage = t.getImage();
			tileImageView.setImage(tileImage);
			currentImageView = tileImageView;
			infoNode.getChildren().add(currentImageView);
			
			// List of things on that tile. Owner of tile
			
			currHex = t;
			if (currHex.getOwner() != null) {
				ObservableList<String> newItems = FXCollections.observableArrayList();
				ArrayList<String> tileList = new ArrayList<String>();
				System.out.println(currHex.getOwner());
				System.out.println(currHex.getContents());
				tileList = PlayerRack.printList(currHex.getContents(currHex.getOwner().getName()));
				System.out.println(tileList);
				newItems.addAll(tileList);
				piecesList.setItems(newItems);
			}
		} 
	}
    
    public Terrain getCurrHex(){
        return currHex;
    }
	
	/*
	 * Is called on setup of infopanel. Creates image views needed for terrain (Hex)
	 */
	private void setUpImageViews() {
		Hex imageHex = new Hex(infoNode.getChildren().get(0).getLayoutBounds().getWidth(), true);
		tileImageView = ImageViewBuilder.create()
				.clip(imageHex)
				.layoutX(-imageHex.getWidthNeeded() + infoNode.getChildren().get(0).getLayoutBounds().getWidth())
				.layoutY(-imageHex.getHeightNeeded() * 0.5)
				.fitHeight(imageHex.getHeightNeeded())
				.preserveRatio(true)
				.build();

	}
}

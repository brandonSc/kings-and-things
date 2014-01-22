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

import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.RectangleBuilder;

public class InfoPanel {

	private static Group infoNode;
	private static ImageView currentImageView, tileImageView;
	private static Image tileImage;
	private static int[] currentTileCoords;
	
	/*
	 * Constructors
	 */
	public InfoPanel (BorderPane bp){
		
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
	}
	
	
	public static void showTileInfo(Terrain t) {

		if (t.getCoords()[0] != currentTileCoords[0] || t.getCoords()[1] != currentTileCoords[1] || t.getCoords()[2] != currentTileCoords[2]) {
			infoNode.getChildren().remove(currentImageView);
			tileImage = t.getImage();
			tileImageView.setImage(tileImage);
			currentImageView = tileImageView;
			infoNode.getChildren().add(currentImageView);
			
			
		} 
		
	}
	
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

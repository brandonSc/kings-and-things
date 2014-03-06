package KAT;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;

public class StartingMenu {

	private static StartingMenu uniqueInstance;
	private double width, height;
	
	private Rectangle clip;
	private ImageView backingImgV;
	private Group startingMenuNode;
	private GameButton loadGameButton;
	private GameButton newGameButton;
	private GameButton exitButton;
	
	
	public StartingMenu () {
		width = Game.getWidth() * 0.8;
		height = Game.getHeight() * 0.8;
		
		loadGameButton = new GameButton(200, 50, "Load Game");
		loadGameButton.getNode().relocate(width*0.6, height * 0.5);
		
		newGameButton = new GameButton(200, 50, "New Game");
		newGameButton.getNode().relocate(width*0.6, height * 0.5 + loadGameButton.getHeight());
		
		exitButton = new GameButton(200, 50, "Exit");
		exitButton.getNode().relocate(width*0.6, height * 0.5 + loadGameButton.getHeight() + newGameButton.getHeight());
		
		clip = RectangleBuilder.create()
				.width(width)
				.height(height)
				.arcHeight(height * 0.05)
				.arcWidth(width * 0.05)
				.build();
		
		backingImgV = ImageViewBuilder.create()
				.image(new Image("Images/RackCover.jpg"))
				.preserveRatio(true)
				.fitHeight(height)
				.build();
		
		startingMenuNode = GroupBuilder.create()
				.clip(clip)
				.children(backingImgV)
				.layoutX(width * 0.1/0.8)
				.layoutY(height * 0.1/0.8)
				.build();
		
		startingMenuNode.getChildren().addAll(loadGameButton.getNode());
		startingMenuNode.getChildren().addAll(newGameButton.getNode());
		startingMenuNode.getChildren().addAll(exitButton.getNode());
		
	}
	
	/*
	 * Gets and Sets
	 */
	public static StartingMenu getInstance(){
        if(uniqueInstance == null){
            uniqueInstance = new StartingMenu();
        }
        return uniqueInstance;
    }

	public Group getNode() { return startingMenuNode; }
	
	
	private void setupEvents() {
		
		loadGameButton.setAction(new EventHandler(){
			@Override
			public void handle(Event event) {
				Game.loadGame();
			}
		});
		
		
	}
}

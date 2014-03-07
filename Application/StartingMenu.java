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

public class StartingMenu extends GameMenu {

	private static StartingMenu uniqueInstance;
	
	private GameButton loadGameButton;
	private GameButton newGameButton;
	private GameButton exitButton;
	
	
	public StartingMenu () {
		super();
		
		loadGameButton = new GameButton(200, 50, "Load Game");
		loadGameButton.getNode().relocate(width*0.6, height * 0.5);
		
		newGameButton = new GameButton(200, 50, "New Game");
		newGameButton.getNode().relocate(width*0.6, height * 0.5 + loadGameButton.getHeight());
		
		exitButton = new GameButton(200, 50, "Exit");
		exitButton.getNode().relocate(width*0.6, height * 0.5 + loadGameButton.getHeight() + newGameButton.getHeight());
		
		menuNode.getChildren().addAll(loadGameButton.getNode());
		menuNode.getChildren().addAll(newGameButton.getNode());
		menuNode.getChildren().addAll(exitButton.getNode());
		
		setupEvents();
		
	}
	
	/*
	 * Gets and Sets
	 */
	public static GameMenu getInstance(){
        if(uniqueInstance == null){
            uniqueInstance = new StartingMenu();
        }
        return uniqueInstance;
    }

	
	
	private void setupEvents() {
		
		loadGameButton.getImgV().setOnMouseClicked(new EventHandler(){
			@Override
			public void handle(Event event) {
				Game.loadGame();
			}
		});
		newGameButton.getImgV().setOnMouseClicked(new EventHandler(){
			@Override
			public void handle(Event event) {
				Game.getRoot().getChildren().remove(StartingMenu.getInstance().getNode());
				Game.getRoot().getChildren().add(CreateGameMenu.getInstance().getNode());
			}
		});
		exitButton.getImgV().setOnMouseClicked(new EventHandler(){
			@Override
			public void handle(Event event) {
				Game.exit();
			}
		});
		
		
	}
	
	public void remove() {
		
		Game.getRoot().getChildren().remove(menuNode);
		menuNode.getChildren().clear();
		clip = null;
		backingImgV = null;
		menuNode = null;
		loadGameButton = null;
		newGameButton = null;
		exitButton = null;
	}
}

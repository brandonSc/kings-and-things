package KAT;

import javafx.event.Event;
import javafx.event.EventHandler;

public class CreateGameMenu extends GameMenu {

	private static CreateGameMenu uniqueInstance;
	
	private GameButton createGameButton;
	private GameButton backButton;
	
	public CreateGameMenu() {
		super();
		
		createGameButton = new GameButton(200, 50, "Create Game");
		createGameButton.getNode().relocate(width*0.6, height * 0.5);
		
		backButton = new GameButton(200, 50, "Back");
		backButton.getNode().relocate(width*0.6, height * 0.5 + createGameButton.getHeight());
		
		menuNode.getChildren().addAll(createGameButton.getNode());
		menuNode.getChildren().addAll(backButton.getNode());
		
		setupEvents();
	}
	
	/*
	 * Gets and Sets
	 */
	public static GameMenu getInstance(){
        if(uniqueInstance == null){
            uniqueInstance = new CreateGameMenu();
        }
        return uniqueInstance;
    }

	
	
	private void setupEvents() {
		
		createGameButton.getImgV().setOnMouseClicked(new EventHandler(){
			@Override
			public void handle(Event event) {
				remove();
				StartingMenu.getInstance().remove();
				Game.createGame();
			}
		});
		backButton.getImgV().setOnMouseClicked(new EventHandler(){
			@Override
			public void handle(Event event) {
				Game.getRoot().getChildren().remove(CreateGameMenu.getInstance().getNode());
				Game.getRoot().getChildren().add(StartingMenu.getInstance().getNode());
			}
		});
		
		
	}
	
	public void remove() {
		
		Game.getRoot().getChildren().remove(menuNode);
		menuNode.getChildren().clear();
		clip = null;
		backingImgV = null;
		menuNode = null;
		backButton = null;
		createGameButton = null;
	}

}

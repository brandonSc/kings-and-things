package KAT;

import java.util.ArrayList;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorAdjustBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;

public class GameMenu {

	private static GameMenu uniqueInstance;
	private static ArrayList<GameButton> startingMenuButtons;
	private static ArrayList<GameButton> newGameButtons;
	
	private double width, height;
	private Rectangle clip;
	private ImageView backingImgV;
	private Group menuNode;
	private ArrayList<GameButton> buttons;
	
	
	public GameMenu() {

		buttons = new ArrayList<GameButton>();
		startingMenuButtons = new ArrayList<GameButton>();
		newGameButtons = new ArrayList<GameButton>();
		
		width = Game.getWidth() * 0.8;
		height = Game.getHeight() * 0.8;
		
		clip = RectangleBuilder.create()
				.width(width)
				.height(height)
				.arcHeight(height * 0.05)
				.arcWidth(width * 0.05)
				.build();
		
//		ColorAdjust colorAdj = ColorAdjustBuilder.create()
//				.brightness(-0.4)
//				.contrast(-0.2)
//				//.hue(-0.3)
//				.build();
		
		backingImgV = ImageViewBuilder.create()
				.image(new Image("Images/RackCover.jpg"))
				.preserveRatio(false)
				.fitHeight(height)
				.fitWidth(width)
//				.effect(colorAdj)
				.build();
		
		menuNode = GroupBuilder.create()
				.clip(clip)
				.children(backingImgV)
				.layoutX(Game.getWidth()/2 - width/2)
				.layoutY(height * 0.1/0.8)
				.build();
		
		setupButtons();
		buttons = startingMenuButtons;
		updateButtons();
	}

	public Group getNode() { return menuNode; }
	public ArrayList<GameButton> getButtons() { return buttons; }
	
	public static GameMenu getInstance() {
		if(uniqueInstance == null){
            uniqueInstance = new GameMenu();
        }
        return uniqueInstance;
	}
	
	public void addMainButton(double w, double h, double x, double y, String t, EventHandler eh) {
		buttons.add(new GameButton(w, h, x, y, t, eh));
		if (buttons.size() == 1)
			buttons.get(0).position(width*0.6, height * 0.5);
		else
			buttons.get(buttons.size() - 1).position(width*0.6, buttons.get(buttons.size() - 2).getPosition()[1] + buttons.get(buttons.size() - 2).getHeight());
	}
	
	public void updateButtons() {
		for (GameButton b : buttons) {
			menuNode.getChildren().add(b.getNode());
		}
	}
	
	public void removeButtons() {
		for (GameButton b : buttons) {
			menuNode.getChildren().remove(b.getNode());
		}
	}
	
	private void setupButtons() {
		
		// Starting menu buttons
		startingMenuButtons.add(new GameButton(200, 50, width*0.6, height * 0.5, 
                    "Play Online", new EventHandler(){
			@Override
			public void handle(Event event) {
				removeButtons();
				Game.getRoot().getChildren().remove(menuNode);
                Game.getUniqueInstance().setNetwork(true);
				Game.getUniqueInstance().createGame(); 
			}
		}));
		startingMenuButtons.add(new GameButton(200, 50, width*0.6, height * 0.5 + 50, 
                    "Local Game", new EventHandler(){
			@Override
			public void handle(Event event) {
				removeButtons();
				Game.getRoot().getChildren().remove(menuNode);
                Game.getUniqueInstance().setNetwork(false);
                Game.getUniqueInstance().createGame();
				//buttons = newGameButtons;
				//updateButtons();
			}
		}));
		startingMenuButtons.add(new GameButton(200, 50, width*0.6, height * 0.5 + 100, "Exit", new EventHandler(){
			@Override
			public void handle(Event event) {
				Game.getUniqueInstance().exit(); 
			}
		}));
		
		// new game buttons
		newGameButtons.add(new GameButton(200, 50, width*0.6, height * 0.5, "Create Game", new EventHandler(){
			@Override
			public void handle(Event event) {
				removeButtons();
				Game.getRoot().getChildren().remove(menuNode);
				Game.getUniqueInstance().createGame(); 
			}
		}));
		newGameButtons.add(new GameButton(200, 50, width*0.6, height * 0.5 + 50, "Back", new EventHandler(){
			@Override
			public void handle(Event event) {
				removeButtons();
				buttons = startingMenuButtons;
				updateButtons();
			}
		}));
	}
	
	
}

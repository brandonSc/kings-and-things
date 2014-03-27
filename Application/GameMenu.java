package KAT;

import java.util.ArrayList;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.DropShadowBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradientBuilder;
import javafx.scene.paint.StopBuilder;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;

public class GameMenu {

	private static GameMenu uniqueInstance;
	private static ArrayList<GameButton> startMenuButtons; 				// Initial screen buttons: "Online play", "Local Play", "Exit"
	private static ArrayList<GameButton> onlineMenuButtons;				// Buttons that buttons will change to for online menu
	private static ArrayList<GameButton> localMenuButtons;				// Buttons that local game menu will use
	private static ArrayList<inputFieldWithLabel> onlineInputFields;	// TextFields that textFields will change to during 'online play' menu
	private static ArrayList<inputFieldWithLabel> localInputFields;		// TextFields that local game menu will use
	private static ArrayList<inputFieldWithLabel> startInputFields;		// TextFields that start menu will use
	
	private static ArrayList<GameButton> buttons;						// The currently used buttons
	private static ArrayList<inputFieldWithLabel> inputFields;			// The currently used TextFields
	
	private static DropShadow dShadow = DropShadowBuilder.create()
					.radius(3)
					.color(Color.WHITESMOKE)
					.offsetX(1)
					.offsetY(1)
					.build();
	
	private double width, height;
	private Rectangle clip;
	private ImageView backingImgV;
	private Group menuNode;
	private Font labelFont;
	private Group fieldList;
	private Rectangle fieldListBacking;
	private Rectangle fieldListBorder;
	
	
	public GameMenu() {

		buttons = new ArrayList<GameButton>();
		inputFields = new ArrayList<inputFieldWithLabel>();
		
		startMenuButtons = new ArrayList<GameButton>();
		startInputFields = new ArrayList<inputFieldWithLabel>();
		onlineMenuButtons = new ArrayList<GameButton>();
		onlineInputFields = new ArrayList<inputFieldWithLabel>();
		localMenuButtons = new ArrayList<GameButton>();
		localInputFields = new ArrayList<inputFieldWithLabel>();
		
		width = Game.getWidth() * 0.8;
		height = Game.getHeight() * 0.8;

		double fontSize = height * 0.06;
		labelFont = Font.loadFont(getClass().getResourceAsStream("/Fonts/ITCBLKAD.TTF"), fontSize);				
		
		clip = RectangleBuilder.create()
				.width(width)
				.height(height)
				.arcHeight(height * 0.05)
				.arcWidth(width * 0.05)
				.build();
		
		fieldListBacking = RectangleBuilder.create()
				.arcHeight(20)
				.width(0)
				.arcWidth(20)
				.fill(Color.DARKSLATEGRAY)
				.opacity(0.5)
				.build();
		
		fieldListBorder = RectangleBuilder.create()
				.arcHeight(20)
				.arcWidth(20)
				.width(0)
				.visible(false)
				.fill(Color.TRANSPARENT)
				.stroke(Color.DARKSLATEGRAY)
				.strokeWidth(3)
				.build();
		
		fieldList = GroupBuilder.create()
				.layoutX(width * 0.3)
				.layoutY(height * 0.5)
				.children(fieldListBacking, fieldListBorder)
				.build();
		
		backingImgV = ImageViewBuilder.create()
				.image(new Image("Images/RackCover.jpg"))
				.preserveRatio(false)
				.fitHeight(height)
				.fitWidth(width)
				.build();
		
		menuNode = GroupBuilder.create()
				.clip(clip)
				.children(backingImgV, fieldList)
				.layoutX(Game.getWidth()/2 - width/2)
				.layoutY(height * 0.1/0.8)
				.build();
		
		setupStuff();
		buttons = startMenuButtons;
		updateMenu();
	}

	public Group getNode() { return menuNode; }
	public ArrayList<GameButton> getButtons() { return buttons; }
	
	public static GameMenu getInstance() {
		if(uniqueInstance == null){
            uniqueInstance = new GameMenu();
        }
        return uniqueInstance;
	}
	
//	public void addMainButton(double w, double h, double x, double y, String t, EventHandler eh) {
//		buttons.add(new GameButton(w, h, x, y, t, eh));
//		if (buttons.size() == 1)
//			buttons.get(0).position(width*0.6, height * 0.5);
//		else
//			buttons.get(buttons.size() - 1).position(width*0.6, buttons.get(buttons.size() - 2).getPosition()[1] + buttons.get(buttons.size() - 2).getHeight());
//	}
	
	public void updateMenu() {
		for (GameButton b : buttons) {
			menuNode.getChildren().add(b.getNode());
		}
		int i;
		for (i = 0; i < inputFields.size(); i++) {
			inputFieldWithLabel ifwl = inputFields.get(i);
			ifwl.position(i*20);
			fieldList.getChildren().add(ifwl.getNode());
		}
		if (i > 0)
			fieldListBorder.setVisible(true);
		fieldListBacking.setWidth(300);
		fieldListBacking.setHeight(i*40);
		fieldListBorder.setWidth(300);
		fieldListBorder.setHeight(i*40);
		
		System.out.println("input fields stuff");
		System.out.println(inputFields);
		System.out.println(onlineInputFields);
		
	}
	
	public void removeStuff() {
		for (GameButton b : buttons) {
			menuNode.getChildren().remove(b.getNode());
		}
		for (inputFieldWithLabel ifwl : inputFields) {
			fieldList.getChildren().remove(ifwl.getNode());
		}
		fieldListBacking.setWidth(0);
		fieldListBacking.setHeight(0);
		fieldListBorder.setWidth(0);
		fieldListBorder.setHeight(0);
		fieldListBorder.setVisible(false);
	}
	
	public void deleteStuff() {
		buttons.clear();
		startMenuButtons.clear();
		localMenuButtons.clear();
		onlineMenuButtons.clear();
		onlineInputFields.clear();
		localInputFields.clear();
		buttons = null;
		startMenuButtons = null;
		localMenuButtons = null;
		onlineMenuButtons = null;
		onlineInputFields = null;
		localInputFields = null;
	}
	
	private void setupStuff() {
		
		/*
		 * Starting menu buttons:
		 * - Play Online
		 * - Play local
		 * - Exit
		 */
		startMenuButtons.add(new GameButton(200, 50, width*0.6, height * 0.5, 
                    "Play Online", new EventHandler(){
			@Override
			public void handle(Event event) {
				removeStuff();
//				Game.getRoot().getChildren().remove(menuNode);
                Game.getUniqueInstance().setNetwork(true);
//				Game.getUniqueInstance().createGame(); 
                inputFields = onlineInputFields;
                buttons = onlineMenuButtons;
                updateMenu();
			}
		}));
		startMenuButtons.add(new GameButton(200, 50, width*0.6, height * 0.5 + 50, 
                    "Local Game", new EventHandler(){
			@Override
			public void handle(Event event) {
				removeStuff();
				buttons = localMenuButtons;
				inputFields = localInputFields;
				updateMenu();
			}
		}));
		startMenuButtons.add(new GameButton(200, 50, width*0.6, height * 0.5 + 100, "Exit", new EventHandler(){
			@Override
			public void handle(Event event) {
				Game.getUniqueInstance().exit(); 
			}
		}));
		
		/*
		 * Online play menu buttons
		 * 
		 * - Play
		 * - Back
		 * 
		 */
		onlineMenuButtons.add(new GameButton(200, 50, width*0.6, height * 0.5, "Play", new EventHandler(){
			@Override
			public void handle(Event event) {
                Game.getUniqueInstance().setNetwork(true);
                GameLoop.getInstance().setLocalPlayer(onlineInputFields.get(0).getText());
				removeStuff();
				Game.getRoot().getChildren().remove(menuNode);
				deleteStuff();
				Game.getUniqueInstance().createGame();
			}
		}));
		onlineMenuButtons.add(new GameButton(200, 50, width*0.6, height * 0.5 + 50, "Back", new EventHandler(){
			@Override
			public void handle(Event event) {
				removeStuff();
				buttons = startMenuButtons;
				inputFields = startInputFields;
				updateMenu();
			}
		}));
		
		/*
		 * Online play menu text Fields
		 * 
		 * - Name
		 * - (password added later)?
		 * 
		 */
		onlineInputFields.add(new inputFieldWithLabel("Name", width * 0.3, height * 0.5));
		
		
		/*
		 * Local play menu buttons
		 * 
		 * - Play
		 * - Back
		 * 
		 */
		localMenuButtons.add(new GameButton(200, 50, width*0.6, height * 0.5, "Play", new EventHandler(){
			@Override
			public void handle(Event event) {
				removeStuff();
				Game.getRoot().getChildren().remove(menuNode);
				deleteStuff();
                Game.getUniqueInstance().setNetwork(false);
				Game.getUniqueInstance().createGame();
			}
		}));
		localMenuButtons.add(new GameButton(200, 50, width*0.6, height * 0.5 + 50, "Back", new EventHandler(){
			@Override
			public void handle(Event event) {
				removeStuff();
				buttons = startMenuButtons;
				updateMenu();
			}
		}));
		
		/*
		 * Local play menu textFields
		 * 
		 * 
		 */
		
	}
	
	private class inputFieldWithLabel {
		
		private HBox ifwlNode;
		private Text label;
		private TextField textField;
		
		private double xPos;
		private double yPos;
		private String labelName;
		private double textFieldWidth = 150;
		
		public inputFieldWithLabel(String s, double x, double y) {
			
			xPos = x;
			yPos = y;
			labelName = s;
			
			label = TextBuilder.create()
					.text(labelName + ": ")
                    .fill(LinearGradientBuilder.create()
                            .startY(0)
                            .startX(1)
                            .stops(StopBuilder.create()
                                    .color(Color.BLACK)
                                    .offset(1)
                                    .build(),
                                StopBuilder.create()
                                    .color(Color.DARKSLATEGRAY)
                                    .offset(0)
                                    .build())
                            .build())
					.effect(dShadow)
					.font(labelFont)
					.build();
			label.setLayoutY(label.getLayoutBounds().getHeight()/2);
			
			textField = TextFieldBuilder.create()
					.layoutX(width - 150)
					.prefWidth(150)
					.build();
			textField.setLayoutY(textField.getLayoutBounds().getHeight()/2);
			
			ifwlNode = HBoxBuilder.create()
					.children(label, textField)
					.alignment(Pos.CENTER_RIGHT)
					.layoutX(xPos)
					.layoutY(yPos)
					.build();
					
			
		}
		
		public HBox getNode() { return ifwlNode; }
		public String getText() { return labelName; }
		
		public void position(double d) {
			ifwlNode.relocate(0, d);
		}
		
	}
	
}

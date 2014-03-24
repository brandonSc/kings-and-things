package KAT;
	
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

import java.util.Scanner;


public class Game extends Application {

    private static BorderPane root;
    private static double width, height;
    
    private static InfoPanel infoPan;
    private static Text helpText;
    private static Board hexBoard;
    private static PlayerBoard playerBoard;
    private static PlayerRackGUI rackG;
    private static GameButton doneButton;
    private static GameMenu menu;
    private static Font gameFont;
    
    private static Thread gameLoopThread;
    private static Game uniqueInstance;
    private static Stage uniqueStage;
    
    private static Image[] playerIcons;
    private static Text[] playerNames;
    private static Text[] playerGold;
    
    /*
     * Gets and Sets
     */
    public static InfoPanel getInfoPanel(){ return infoPan; }
    public static Text getHelpText(){ return helpText; }
    public static Board getBoard() { return hexBoard; }
    public static PlayerRackGUI getRackGui() { return rackG; }
    public static Image[] getPlayerIcons(){ return playerIcons; }
    public static Text[] getPlayerNames(){ return playerNames; }
    public static Text[] getPlayerGold(){ return playerGold; }
    public static GameButton getDoneButton(){ return doneButton; }
    public static double getWidth() { return width; }
    public static double getHeight() { return height; }
    public static BorderPane getRoot() { return root; }
    public static Game getUniqueInstance() { return uniqueInstance; }
    public static Font getFont() { return gameFont; }
    
    // Can change these accordingly for testing and what not
    private static boolean network = !false;	
    private static boolean startingMenu = true;
    private static boolean createGameMenu = false;
    private static boolean runGameLoop;

	@Override
	public void start(Stage primaryStage) {
		
		uniqueInstance = this;
		uniqueStage = primaryStage;
//		uniqueStage.setFullScreen(true);
		
		width = Screen.getPrimary().getVisualBounds().getWidth();
		height = Screen.getPrimary().getVisualBounds().getHeight() * 0.95;
		
		root = new BorderPane();
		Scene scene = new Scene(root,width,height);
		uniqueStage.setScene(scene);
		uniqueStage.show();
		
		// Import the game pictures.
		Board.generateHexes();
		Player.setClassImages();
		Terrain.setClassImages();
		Fort.setClassImages();
		
		// Background image
		root.getChildren().add(ImageViewBuilder.create()
				.image(new Image("Images/GameBackground.jpg"))
				.preserveRatio(false)
				.fitHeight(height)
				.fitWidth(width)
				.opacity(0.5)
				.build());
				
		
		// TODO finish starting menu for creating game
		if (startingMenu) {
			menu = GameMenu.getInstance();
			root.getChildren().add(menu.getNode());
		} else {
			createGame();
		}
		
		
		
		
	}

    public void start(){
        runGameLoop = true;
    }

    public void stop(){
        runGameLoop = false;
        GameLoop.getInstance().stop();
        if (gameLoopThread != null)
        	gameLoopThread.interrupt();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    // To launch a new  game menu (where players enter etc)
    public static void newGame() {
    	
    }
    
    public static void createGame() {
        GameLoop.setNetworked(network);	

        Player user  = new Player("User1", "YELLOW");
        Player user2 = new Player("User2", "BLUE");
        Player user3 = new Player("User3", "GREEN");
        Player user4 = new Player("User4", "RED");
        
		try {
            java.util.ArrayList<Player> tmp = new java.util.ArrayList<Player>();
            tmp.add(user);
            tmp.add(user2);
            tmp.add(user3);
            tmp.add(user4);
           
//            playerIcons = new Image[tmp.size()];
//            playerNames = new Text[tmp.size()];
//            playerGold = new Text[tmp.size()];

			
//            HBox topBox = new HBox(10);
//    //        topBox.setPadding(new Insets(5,10,5,10));
            helpText = new Text("initializing...");
            helpText.setFont(new Font(15));
            root.getChildren().add(helpText);
            helpText.relocate(width*0.25 , 0);
//       //     HBox bottomBox = new HBox(10);
//      //      bottomBox.setPadding(new Insets(10,10,10,10));
            doneButton = new GameButton(50, 25, width*0.25 + 5, height - 20, "Done", null);
            doneButton.deactivate();
            root.getChildren().add(doneButton.getNode());
// 
//            for( int i=0; i<tmp.size(); i++ ){
//                playerIcons[i] = tmp.get(i).getImage();
//                playerNames[i] = new Text(tmp.get(i).getName());
//                playerGold[i] = new Text("Gold: 000");
//                VBox vbox = new VBox(10);
//                HBox hbox = new HBox();
//                vbox.setPadding(new Insets(10,5,10,5));
//                vbox.getChildren().addAll(playerNames[i], playerGold[i]);
//                hbox.getChildren().addAll(new ImageView(playerIcons[i]), vbox);
//                topBox.getChildren().add(hbox);
//            }
      //      root.setTop(topBox);
      //      root.setBottom(bottomBox);

			
			hexBoard = new Board(root);
			GameLoop.getInstance().setPlayers(tmp);
			playerBoard = new PlayerBoard();
			
//			String[] iterOnePreSet = new String[]{"FrozenWaste","Forest","Jungle","Plains","Sea","Forest","Swamp","Plains","FrozenWaste","Mountains",
//					"FrozenWaste","Swamp","Desert","Swamp","Forest","Desert","Plains","Mountains","Jungle","Swamp","Mountains","Jungle",
//					"Swamp","Desert","Forest","Plains","Forest","FrozenWaste","Jungle","Mountains","Desert","Plains","Jungle","Mountains",
//					"Forest","FrozenWaste","Desert"};

			
			TileDeck theDeck = new TileDeck(root);
			infoPan = new InfoPanel(root);
			rackG = new PlayerRackGUI(root, tmp, infoPan);
            for (int i = 0; i < 4; i++) {
                tmp.get(i).getPlayerRack().registerObserver(rackG);
            }
			TheCupGUI theCup = new TheCupGUI(root, rackG);
            DiceGUI.getInstance().setBorderPane(root);
            DiceGUI.getInstance().draw();
            DiceGUI.getInstance().setFaceValue(0,0);
            DiceGUI.getInstance().setFaceValue(0,1);
			
			GameLoop.getInstance().initGame(uniqueInstance);
			//rackG.generateButtons();

            // execute playGame method in a background thread 
            // as to not block main GUI thread
			uniqueInstance.start();
			gameLoopThread = new Thread(new Runnable(){
                public void run(){
                    while( runGameLoop ){ 
                        GameLoop.getInstance().playGame();
                    }
                }
            });
			gameLoopThread.start();
            
            // stop the gameLoopThread if the close button is pressed
			uniqueStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
                @Override
                public void handle( WindowEvent event ){
                	uniqueInstance.stop();
                }
            });
			
		} catch(Exception e) {
			e.printStackTrace();
			uniqueInstance.stop();
		}
    }
    
    public static void loadGame() {
    	
    }
    public static void exit() {
    	Platform.exit();
    }
}

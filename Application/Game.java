package KAT;
	
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import javafx.geometry.Insets;

import java.util.Scanner;


public class Game extends Application {
	
    private static InfoPanel infoPan;
    private static Text helpText;
    private static BorderPane root;
    private static Board hexBoard;
    private static PlayerRackGUI rackG;
    private static Thread gameLoopThread;
    private static Image[] playerIcons;
    private static Text[] playerNames;
    private static Text[] playerGold;
    private static Button doneButton;
    private static double width, height;
    private static Game uniqueInstance;
    private static Stage uniqueStage;
    
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
    public static Button getDoneButton(){ return doneButton; }
    public static double getWidth() { return width; }
    public static double getHeight() { return height; }
    public static BorderPane getRoot() { return root; }
    public static Game getUniqueInstance() { return uniqueInstance; }
    
    // Can change these accordingly for testing and what not
    private static boolean network = false;	
    private static boolean startingMenu = true;
    private static boolean createGameMenu = false;
    private static boolean runGameLoop;

	@Override
	public void start(Stage primaryStage) {
		
		uniqueInstance = this;
		uniqueStage = primaryStage;
		uniqueStage.setFullScreen(true);
		
		width = Screen.getPrimary().getVisualBounds().getWidth();
		height = Screen.getPrimary().getVisualBounds().getHeight();
		
		root = new BorderPane();
		Scene scene = new Scene(root,width,height);
		uniqueStage.setScene(scene);
		uniqueStage.show();
		
		// Import the game pictures.
		Player.setClassImages();
		Terrain.setClassImages();
		Fort.setClassImages();
		
		root.getChildren().add(ImageViewBuilder.create()
				.image(new Image("Images/GameBackground.jpg"))
				.preserveRatio(false)
				.fitHeight(height)
				.build());
				
		
		// TODO finish starting menu for creating game
		if (startingMenu) {
			root.getChildren().add(StartingMenu.getInstance().getNode());
		} else {
			createGame();
		}
		
		
		
		
	}

    public void start(){
        runGameLoop = true;
    }

    public void stop(){
        runGameLoop = false;
        GameLoop.getInstance().unPause();
        if (gameLoopThread != null)
        	gameLoopThread.interrupt();
    }

    public void updateGold( Player player ){
        for( int i=0; i<playerGold.length; i++ ){
            if( playerNames[i].getText() == player.getName() ){
                int gold = player.getGold();
                String str = "Gold: ";
                if( gold < 100 ){
                    str += "0";
                } 
                if( gold < 10 ){
                    str += "0";
                }
                str += gold;
                playerGold[i].setText(str);
                return;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    // To launch a new  game menu (where players enter etc)
    public static void newGame() {
    	
    }
    
    public static void createGame() {
    	
        Player user = null;
        // will move this to GameLoop later
        if( network ){
            Scanner s = new Scanner(System.in);

            System.out.println("\nConnecting to Kings&Things server...\n");
            Client client = new Client("localhost", 8888);
            client.connect();
            
            System.out.println("Enter your username: ");
            String username = s.nextLine();

            System.out.println("\nSigning in...\n");
            client.sendLogin(username);

            user = new Player(username, "YELLOW");
        } else {
            user = new Player("User1", "YELLOW");
        }
		
        Player user2 = new Player("User2", "BLUE");
        Player user3 = new Player("User3", "GREEN");
        Player user4 = new Player("User4", "RED");
        
		try {
            java.util.ArrayList<Player> tmp = new java.util.ArrayList<Player>();
            tmp.add(user);
            tmp.add(user2);
            tmp.add(user3);
            tmp.add(user4);
           
            playerIcons = new Image[tmp.size()];
            playerNames = new Text[tmp.size()];
            playerGold = new Text[tmp.size()];

			
            HBox topBox = new HBox(10);
            topBox.setPadding(new Insets(5,10,5,10));
            helpText = new Text("initializing...");
            helpText.setFont(new Font(15));
            HBox bottomBox = new HBox(10);
            bottomBox.setPadding(new Insets(10,10,10,10));
            doneButton = new Button("Done");
            doneButton.setDisable(true);
            bottomBox.getChildren().add(doneButton);
 
            for( int i=0; i<tmp.size(); i++ ){
                playerIcons[i] = tmp.get(i).getImage();
                playerNames[i] = new Text(tmp.get(i).getName());
                playerGold[i] = new Text("Gold: 000");
                VBox vbox = new VBox(10);
                HBox hbox = new HBox();
                vbox.setPadding(new Insets(10,5,10,5));
                vbox.getChildren().addAll(playerNames[i], playerGold[i]);
                hbox.getChildren().addAll(new ImageView(playerIcons[i]), vbox);
                topBox.getChildren().add(hbox);
            }
            topBox.getChildren().add(helpText);
            root.setTop(topBox);
            root.setBottom(bottomBox);

			
			hexBoard = new Board(root);
			
//			String[] iterOnePreSet = new String[]{"FrozenWaste","Forest","Jungle","Plains","Sea","Forest","Swamp","Plains","FrozenWaste","Mountains",
//					"FrozenWaste","Swamp","Desert","Swamp","Forest","Desert","Plains","Mountains","Jungle","Swamp","Mountains","Jungle",
//					"Swamp","Desert","Forest","Plains","Forest","FrozenWaste","Jungle","Mountains","Desert","Plains","Jungle","Mountains",
//					"Forest","FrozenWaste","Desert"};

			GameLoop.getInstance().setPlayers(tmp);
			TileDeck theDeck = new TileDeck(root);
			infoPan = new InfoPanel(root);
			rackG = new PlayerRackGUI(root, tmp, infoPan);
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

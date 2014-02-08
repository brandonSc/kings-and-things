package KAT;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import java.util.ArrayList;
import java.util.Scanner;


public class Game extends Application {
    private InfoPanel infoPan;
    private Text helpText;
    private BorderPane root;
    private Board hexBoard;
    private PlayerRackGUI rackG;
    private Thread gameLoopThread;
    private boolean runGameLoop;
    
    public InfoPanel getInfoPanel(){ return infoPan; }
    public Text getHelpText(){ return helpText; }
    public Board getBoard() { return hexBoard; }
    public PlayerRackGUI getRackGui() { return rackG; }

	@Override
	public void start(Stage primaryStage) {
		
		// Import the game pictures.
		Player.setClassImages();
		Terrain.setClassImages();
		
        Player user = null;
        // will move this to GameLoop later
        if( false ){
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

			root = new BorderPane();
            helpText = new Text("initializing...");
            root.setTop(helpText);
			Scene scene = new Scene(root,1500,700);
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

			
			hexBoard = new Board(root); // Must be called before new TileDeck
			
			String[] iterOnePreSet = new String[]{"FrozenWaste","Forest","Jungle","Plains","Sea","Forest","Swamp","Plains","FrozenWaste","Mountains",
					"FrozenWaste","Swamp","Desert","Swamp","Forest","Desert","Plains","Mountains","Jungle","Swamp","Mountains","Jungle",
					"Swamp","Desert","Forest","Plains","Forest","FrozenWaste","Jungle","Mountains","Desert","Plains","Jungle","Mountains",
					"Forest","FrozenWaste","Desert"};
            
			TileDeck theDeck = new TileDeck(root, iterOnePreSet);
			GameLoop.getInstance().setPlayers(tmp);
			infoPan = new InfoPanel(root, theDeck);
			rackG = new PlayerRackGUI(root, tmp, infoPan);
			TheCupGUI theCup = new TheCupGUI(root, rackG);
			
			GameLoop.getInstance().initGame(theDeck, this);
			//rackG.generateButtons();

            // execute playGame method in a background thread 
            // as to not block main GUI thread
			runGameLoop = true;
			gameLoopThread = new Thread(new Runnable(){
                public void run(){
                    while( runGameLoop ){ 
                        GameLoop.getInstance().playGame();
                    }
                }
            });
			gameLoopThread.start();
           
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}

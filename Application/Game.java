package KAT;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
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
    private Image[] playerIcons;
    private Text[] playerNames;
    private Text[] playerGold;
    private Button doneButton;

    public InfoPanel getInfoPanel(){ return infoPan; }
    public Text getHelpText(){ return helpText; }
    public Board getBoard() { return hexBoard; }
    public PlayerRackGUI getRackGui() { return rackG; }
    public Image[] getPlayerIcons(){ return playerIcons; }
    public Text[] getPlayerNames(){ return playerNames; }
    public Text[] getPlayerGold(){ return playerGold; }
    public Button getDoneButton(){ return doneButton; }

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
        
        Player.setClassImages();

		try {
            java.util.ArrayList<Player> tmp = new java.util.ArrayList<Player>();
            tmp.add(user);
            tmp.add(user2);
            tmp.add(user3);
            tmp.add(user4);
           
            playerIcons = new Image[tmp.size()];
            playerNames = new Text[tmp.size()];
            playerGold = new Text[tmp.size()];

			root = new BorderPane();
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
			Scene scene = new Scene(root,1500,700);
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
			start();
			gameLoopThread = new Thread(new Runnable(){
                public void run(){
                    while( runGameLoop ){ 
                        GameLoop.getInstance().playGame();
                    }
                }
            });
			gameLoopThread.start();
            
            // stop the gameLoopThread if the close button is pressed
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
                @Override
                public void handle( WindowEvent event ){
                    stop();
                }
            });
            
		} catch(Exception e) {
			e.printStackTrace();
            stop();
		}
	}

    public void start(){
        runGameLoop = true;
    }

    public void stop(){
        runGameLoop = false;
        GameLoop.getInstance().unPause();
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
}

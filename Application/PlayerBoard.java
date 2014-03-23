package KAT;

import java.util.HashMap;

import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.effect.DropShadowBuilder;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradientBuilder;
import javafx.scene.paint.StopBuilder;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;

public class PlayerBoard {

	private HashMap<String, Player> players;
	private HashMap<String, Group> playerDisplay;
	private HashMap<String, Text> playerGold;
	
	private Group playerBoardNode;
	private VBox playerDisplayBox;
	private Rectangle backing;
	private double height;
	private double width;
	private double playerHeight;
	private double padding;
	private double margins;
	
	private static PlayerBoard uniqueInstance;
	
	public PlayerBoard() {
		
		height = Game.getHeight()*0.6;
		width = Game.getWidth()*0.25;
		playerHeight = height/4;
		padding = 10;
		margins = 5;
		
		players = new HashMap<String, Player>();
		playerDisplay = new HashMap<String, Group>();
		playerGold = new HashMap<String, Text>();

		playerBoardNode = GroupBuilder.create()
				.layoutX(Game.getWidth() - width)
				.layoutY(Game.getHeight() * 0.3)
				.build();
		
		backing = RectangleBuilder.create()
				.arcHeight(height*0.05)
				.arcWidth(width*0.05)
				.height(height)
				.width(width + width*0.05)
				.fill(Color.DARKSLATEGRAY)
				.opacity(0.5)
				.build();
		
		playerDisplayBox = VBoxBuilder.create()
				.padding(new Insets(padding, padding, padding, padding))
				.spacing(margins)
				.build();
				
		playerBoardNode.getChildren().addAll(backing, playerDisplayBox);
		
		for (Player p : GameLoop.getInstance().getPlayers()) {
            players.put(p.getName(), p);
            Text name = TextBuilder.create()
                    .text(p.getName())
                    .layoutX(playerHeight * 0.05)
                    .layoutY(playerHeight * 0.20)
                    .font(Font.font("Blackadder ITC", FontWeight.EXTRA_BOLD, height*0.07))
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
                    .build();
            
            Text gold = TextBuilder.create()
                    .text("Gold: 000")
                    .layoutX(playerHeight + 10)
                    .layoutY(20)
                    .font(Font.font("Blackadder ITC", FontWeight.BOLD, height*0.05))
                    .build();
            
            playerGold.put(p.getName(), gold);
            
            ImageView imgV = ImageViewBuilder.create()
                    .image(p.getImage())
                    .preserveRatio(true)
                    .fitHeight(playerHeight - padding - name.getLayoutBounds().getHeight())
                    .layoutY(name.getLayoutBounds().getHeight() - 10)
                    .effect(DropShadowBuilder.create()
                            .offsetX(5)
                            .offsetY(5)
                            .radius(5)
                            .color(Color.DARKSLATEGRAY)
                            .build())
                    .build();
            
            Group playerGroup = GroupBuilder.create()
                    .build();
            
            playerGroup.getChildren().addAll(imgV, name, gold);
            playerDisplayBox.getChildren().add(playerGroup);
            
            playerDisplay.put(p.getName(), playerGroup);
		}
		Game.getRoot().getChildren().add(playerBoardNode);
		
	}
	
	public static PlayerBoard getInstance(){
        if(uniqueInstance == null){
            uniqueInstance = new PlayerBoard();
        }
        return uniqueInstance;
    }
	
	public void updateGold( Player player ){
		
        int gold = player.getGold();
        String str = "Gold: ";
        if( gold < 100 ){
            str += "0";
        } 
        if( gold < 10 ){
            str += "0";
        }
        str += gold;
        playerGold.get(player.getName()).setText(str);
        return;
    }
	
}

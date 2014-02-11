package KAT;

import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/*
 * This class visually represents the player rack.
 */
public class PlayerRackGUI {
    private Group             rackGroup;
    private static PlayerRack        rack; //Single instance of the player rack.
    private static ArrayList<Button> pieces; //Represents the different "things" in the rack.
    private HBox              rackBox;
    private GameLoop          gLoop;
    private static InfoPanel  iPanel;
    private static Player     owner;
    private static ArrayList<Player> ownerList;
    private int               index;

    /*
     * Constructor
     */
    public PlayerRackGUI(BorderPane bp, ArrayList<Player> p, InfoPanel ip) {
        rackBox = new HBox(5);
        pieces = new ArrayList<Button>(10);
        ownerList = p;
        owner = p.get(0);
        rack = owner.getPlayerRack();
        gLoop = GameLoop.getInstance();
        iPanel = ip;

        draw(bp);

        bp.getChildren().add(rackGroup);
    }

    /*
     * Method to visually show the various components of the rack.
     */
    public void draw(BorderPane bp) {
        rackGroup = GroupBuilder.create()
                .children(RectangleBuilder.create()
                        .width(bp.getWidth() * 0.8)
                        .height(bp.getHeight() * 0.2)
                        .fill(Color.LIGHTGRAY)
                        .build())
                .layoutX(500)
                .layoutY(bp.getHeight()-55)
                .clip(RectangleBuilder.create()
                        .width(655)
                        .height(55)
                        .arcHeight(10)
                        .arcWidth(10)
                        .x(350)
                        .build())
                .build();

        rackGroup.getChildren().add(rackBox);
        rackBox.relocate(350, 0);

        //Initializes the buttons and sets all them to be hidden. Adds the event listener to them.
        for (int i = 0; i < 10; i++) {
            index = i;
            pieces.add(new Button());
            pieces.get(i).setStyle("-fx-font: 10 arial;");
            pieces.get(i).setMinSize(50,50);
            pieces.get(i).setMaxSize(60,50);
            pieces.get(i).setVisible(false);
            pieces.get(i).addEventHandler(MouseEvent.MOUSE_CLICKED, handler);
            rackBox.getChildren().add(pieces.get(i));
        }
    }

    /*
     * Event handler for the buttons on the rack.
     */
    EventHandler<MouseEvent> handler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent e) {
            //temporary to test: if the phase is 0, it allows the user to place things on the board.
            //if the user clicks on an item in the rack, it is added to the specified tile, unless it is a tile that they do not own.
            if (e.getButton() == MouseButton.PRIMARY) {
                if (gLoop.getPhase() == 0 || gLoop.getPhase() == 3) {
                    if (ClickObserver.getInstance().getClickedTerrain() != null) {
                        owner.playPiece(rack.getPieces().get(pieces.indexOf((Button)e.getSource())), ClickObserver.getInstance().getClickedTerrain());
                        ((Button)e.getSource()).setVisible(false);
                        rack.getPieces().remove(pieces.indexOf((Button)e.getSource()));
                        pieces.remove((Button)e.getSource());
                        rackBox.getChildren().remove((Button)e.getSource());
                        iPanel.showTileInfo(iPanel.getCurrHex());
                    }
                }
            }
            //If the user right clicks the piece, it goes back into the cup.
            if (e.getButton() == MouseButton.SECONDARY) {
                TheCup.getInstance().addToCup(rack.getPieces().get(pieces.indexOf((Button)e.getSource())));
                ((Button)e.getSource()).setVisible(false);
                rack.getPieces().remove(((Button)e.getSource()).getText());
            }
        }
    };

    /*
     * Method to visually generate what is on the rack.
     */
    public void generateButtons() {
        if (pieces.size() == 0) {
            System.out.println("Generating new pieces");
            for (int i = 0; i < 10; i++) {
                pieces.add(new Button());
                pieces.get(i).setMinSize(50,50);
                pieces.get(i).setMaxSize(60,50);
                pieces.get(i).setVisible(false);
                pieces.get(i).addEventHandler(MouseEvent.MOUSE_CLICKED, handler);
                rackBox.getChildren().add(pieces.get(i));
            }
        }

        for (int i = 0; i < rack.getPieces().size(); i++) {
            pieces.get(i).setVisible(true);
            if (!rack.getPieces().get(i).getFront().equals(""))
                pieces.get(i).setGraphic(new ImageView(new Image(rack.getPieces().get(i).getFront(),50,50,false,false)));
            else
                pieces.get(i).setText(rack.getPieces().get(i).getName());
            pieces.get(i).setTooltip(new Tooltip(rack.getPieces().get(i).toString()));
        }
    }

    public void setOwner(Player p) { 
        owner = p;
        rack = owner.getPlayerRack();
        generateButtons();
    }

    public Player getOwner() { return owner; }

    public static void update() {
        if (ClickObserver.getInstance().getClickedTerrain().getOwner() != owner) {
            for (Button b : pieces)
                b.setDisable(true);
        }
        else if (ClickObserver.getInstance().getClickedTerrain().getOwner() == owner) {
            for (Button b : pieces)
                b.setDisable(false);
        }
    }

    public PlayerRack getRack() { return rack; }
}

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

/*
 * This class visually represents the player rack.
 */
public class PlayerRackGUI {
    private Group             rackGroup;
    private PlayerRack        rack; //Single instance of the player rack.
    private static ArrayList<Button> pieces; //Represents the different "things" in the rack.
    private HBox              rackBox;
    private GameLoop          gLoop;
    private static InfoPanel  iPanel;
    private static Player     owner;
    private int               index;
    // private static ToolTip    pieceToolTip;

    /*
     * Constructor
     */
    public PlayerRackGUI(BorderPane bp, Player p, InfoPanel ip) {
        rackBox = new HBox(2);
        pieces = new ArrayList<Button>(10);
        rack = p.getPlayerRack();
        gLoop = GameLoop.getInstance();
        iPanel = ip;
        owner = p;

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
        System.out.println(rackBox.getLayoutX() + "," + rackBox.getLayoutY());

        //Initializes the buttons and sets all them to be hidden. Adds the event listener to them.
        for (int i = 0; i < 10; i++) {
            index = i;
            pieces.add(new Button());
            pieces.get(i).setStyle("-fx-font: 10 arial;");
            pieces.get(i).setMinSize(50,50);
            pieces.get(i).setVisible(false);
            pieces.get(i).addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        //temporary to test: if the phase is 0, it allows the user to place things on the board.
                        //if the user clicks on an item in the rack, it is added to the specified tile, unless it is a tile that they do not own.
                        if (e.getButton() == MouseButton.PRIMARY) {
                            if (gLoop.getPhase() == 0) {
                                if (iPanel.getCurrHex() != null) {
                                    owner.playPiece(rack.getPieces().get(pieces.indexOf((Button)e.getSource())), iPanel.getCurrHex());
                                    ((Button)e.getSource()).setVisible(false);
                                    rack.getPieces().remove(pieces.indexOf((Button)e.getSource()));
                                    pieces.remove((Button)e.getSource());
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
                });
            rackBox.getChildren().add(pieces.get(i));
        }
    }

    /*
     * Method to visually generate what is on the rack.
     */
    public void generateButtons() {
        for (int i = 0; i < rack.getPieces().size(); i++) {
            pieces.get(i).setVisible(true);
            pieces.get(i).setText(rack.getPieces().get(i).getName());
            pieces.get(i).setTooltip(new Tooltip(rack.getPieces().get(i).toString()));
        }
    }

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
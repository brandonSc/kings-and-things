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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

/*
 * This class visually represents the player rack.
 */
public class PlayerRackGUI {
    private Group             rackGroup;
    private PlayerRack        rack; //Single instance of the player rack.
    private Button[]          pieces; //Represents the different "things" in the rack.
    private HBox              rackBox;

    /*
     * Constructor
     */
    public PlayerRackGUI(BorderPane bp, Player p) {
        rackBox = new HBox(2);
        pieces = new Button[13];
        rack = p.getPlayerRack();

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
        for (int i = 0; i < 13; i++) {
            pieces[i] = new Button();
            pieces[i].setStyle("-fx-font: 10 arial;");
            pieces[i].setMinSize(50,50);
            pieces[i].setVisible(false);
            pieces[i].addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        //If the user right clicks the piece, it goes back into the cup.
                        if (e.getButton() == MouseButton.SECONDARY) {
                            System.out.println(((Button)e.getSource()).getText());
                            TheCup.getInstance().addToCup(((Button)e.getSource()).getText());
                            ((Button)e.getSource()).setVisible(false);
                            rack.getPieces().remove(((Button)e.getSource()).getText());
                        }
                    }
                });
            rackBox.getChildren().add(pieces[i]);
        }
    }

    /*
     * Method to visually generate what is on the rack.
     */
    public void generateButtons() {
        System.out.println(rack.getPieces().size());
        for (int i = 0; i < rack.getPieces().size(); i++) {
            pieces[i].setVisible(true);
            pieces[i].setText(""+rack.getPieces().get(i));
        }
    }

    public PlayerRack getRack() { return rack; }
}
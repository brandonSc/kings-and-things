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
public class PlayerRackGUI implements Observer {
    private Group                    rackGroup;
    private static PlayerRack        rack; //Single instance of the player rack.
    private static ArrayList<ImageView> images;
    private HBox                     rackBox;
    private GameLoop                 gLoop;
    private static InfoPanel         iPanel;
    private static Player            owner;

    /*
     * Constructor
     */
    public PlayerRackGUI(BorderPane bp, ArrayList<Player> p, InfoPanel ip) {
        rackBox = new HBox(5);
        images = new ArrayList<ImageView>();
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
                        .fill(Color.DARKSLATEGRAY)
                        .opacity(0.5)
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
                        owner.playPiece(rack.getPieces().get(images.indexOf((ImageView)e.getSource())), ClickObserver.getInstance().getClickedTerrain());
                        //System.out.println(owner.playPiece(rack.getPieces().get(pieces.indexOf((Button)e.getSource())), ClickObserver.getInstance().getClickedTerrain()));
                        ((ImageView)e.getSource()).setVisible(false);
                        rack.removePiece(images.indexOf((ImageView)e.getSource()));
                        images.remove((ImageView)e.getSource());
                        rackBox.getChildren().remove((ImageView)e.getSource());
                        iPanel.showTileInfo(iPanel.getCurrHex());
                    }
                }
            }
            //If the user right clicks the piece, it goes back into the cup.
            // if (e.getButton() == MouseButton.SECONDARY) {
            //     TheCup.getInstance().addToCup(rack.getPieces().get(pieces.indexOf((Button)e.getSource())));
            //     ((Button)e.getSource()).setVisible(false);
            //     rack.getPieces().remove(((Button)e.getSource()).getText());
            // }
        }
    };

    /*
     * Method to visually generate what is on the rack.
     */
    public void generateButtons() {
        if (images.size() < rack.getPieces().size()) {
           // System.out.println("Generating new pieces");
            for (int i = 0; i < rack.getPieces().size(); i++) {
                images.add(new ImageView());
                images.get(i).setVisible(true);
                images.get(i).setImage(new Image(rack.getPieces().get(i).getFront(),50,50,false,false));
                images.get(i).addEventHandler(MouseEvent.MOUSE_CLICKED, handler);
                rackBox.getChildren().add(images.get(i));
            }
        }
        if (ClickObserver.getInstance().getClickedTerrain() != null)
            PlayerRackGUI.updateRack();
    }

    public void setOwner(Player p) {
        System.out.println("Setting owner to: " + p.getName());
        owner = p;
        rack = owner.getPlayerRack();
        for (ImageView iv : images) {
            rackBox.getChildren().remove(iv);
        }
        images.clear();
        generateButtons();
    }

    public static void disableAll() {
        for (ImageView iv : images) {
            iv.setDisable(true);
            iv.setOpacity(0.5);
        }
    }


    public Player getOwner() { return owner; }

    public static void updateRack() {
        if (ClickObserver.getInstance().getClickedTerrain().getOwner() != owner) {
            for (ImageView iv : images) {
                iv.setDisable(true);
                iv.setOpacity(0.5);
            }
        }
        else if (ClickObserver.getInstance().getClickedTerrain().getOwner() == owner) {
            for (ImageView iv : images)
                iv.setDisable(false);
            for (int i = 0; i < rack.getPieces().size(); i++) {
                if (rack.getPieces().get(i).isPlayable()) {
                    images.get(i).setDisable(false);
                    images.get(i).setOpacity(1);
                }
                else {
                    images.get(i).setDisable(true);
                    images.get(i).setOpacity(0.5);
                }
            }
        }   
    }

    public void update() {
        System.out.println("==observer updating==");
        for (ImageView iv : images)
            rackBox.getChildren().remove(iv);
        images.clear();
        generateButtons();
    }

    public PlayerRack getRack() { return rack; }
}

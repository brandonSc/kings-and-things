package KAT;

import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.shape.Rectangle;
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
import javafx.scene.effect.Glow;
import javafx.scene.effect.GlowBuilder;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

/*
 * This class visually represents the player rack.
 */
public class PlayerRackGUI implements Observer {
    private Group                       rackGroup, view;
    private static PlayerRack           rack; //Single instance of the player rack.
    private static ArrayList<ImageView> images;
    private HBox                        rackBox, returnBox;
    private GameLoop                    gLoop;
    private static InfoPanel            iPanel;
    private static Player               owner;
    private static GameButton           toCupButton;
    private ArrayList<Piece>            piecesToReturn;
    private static Glow                 glow;

    /*
     * Constructor
     */
    public PlayerRackGUI(BorderPane bp, ArrayList<Player> p, InfoPanel ip) {
        rackBox = new HBox(5);
        returnBox = new HBox(2);
        images = new ArrayList<ImageView>();
        piecesToReturn = new ArrayList<Piece>();
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
        glow = GlowBuilder.create().build();

        toCupButton = new GameButton(75, 35, bp.getWidth()*0.4, bp.getHeight()-55, "Return", toCupHandler);
        toCupButton.deactivate();
        bp.getChildren().add(toCupButton.getNode());

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

        rackGroup.getChildren().addAll(rackBox);
        rackBox.relocate(350, 0);  
    }

    /*
     * Event handler for the buttons on the rack.
     */
    EventHandler<MouseEvent> clickHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent e) {
            //temporary to test: if the phase is 0, it allows the user to place things on the board.
            //if the user clicks on an item in the rack, it is added to the specified tile, unless it is a tile that they do not own.
            if (e.getButton() == MouseButton.PRIMARY) {
                if (gLoop.getPhase() == 0 || gLoop.getPhase() == 3) {
                    if (ClickObserver.getInstance().getClickedTerrain() != null) {
                        if (((ImageView)e.getSource()).getOpacity() < 1)
                            return;
                        owner.playPiece(rack.getPieces().get(images.indexOf((ImageView)e.getSource())), ClickObserver.getInstance().getClickedTerrain());
                        if (rack.getPieces().get(images.indexOf((ImageView)e.getSource())).getType().equals("Special Income")) {
                            if (((SpecialIncome)rack.getPieces().get(images.indexOf((ImageView)e.getSource()))).isTreasure()) {
                                PlayerBoard.getInstance().updateGold(owner);
                                TheCup.getInstance().addToCup(rack.getPieces().get(images.indexOf((ImageView)e.getSource())));
                            }
                        }
                        ((ImageView)e.getSource()).setVisible(false);
                        rack.removePiece(images.indexOf((ImageView)e.getSource()));
                        images.remove((ImageView)e.getSource());
                        rackBox.getChildren().remove((ImageView)e.getSource());
                        iPanel.showTileInfo(iPanel.getCurrHex());
                    }
                }
            }
            //If the user right clicks the piece, it gets highlighted and then if they press the return button it will send those
            //pieces back to the cup.
            if (e.getButton() == MouseButton.SECONDARY) {
                if (!piecesToReturn.contains(rack.getPieces().get(images.indexOf((ImageView)e.getSource())))) {
                    piecesToReturn.add(rack.getPieces().get(images.indexOf((ImageView)e.getSource())));
                }
                else {
                    piecesToReturn.remove(rack.getPieces().get(images.indexOf((ImageView)e.getSource())));
                }
            }
        }
    };

    /*
     * Handler for the Return to Cup button.
     * In the setup phase, there is a one-to-one ratio of pieces returned to pieces gained.
     * In phase 3 (recruit phase), for every 2 pieces to be returned, one piece is drawn.
     */
    EventHandler<MouseEvent> toCupHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent e) {
            System.out.println(piecesToReturn);
            if (GameLoop.getInstance().getPhase() == 0) {
                rack.removePieces(piecesToReturn);
                rack.addPieces(TheCup.getInstance().draw(piecesToReturn.size()));
                TheCup.getInstance().addToCup(piecesToReturn);
                toCupButton.deactivate();
                piecesToReturn.clear();
            }
            else if (GameLoop.getInstance().getPhase() == 3) {
                rack.removePieces(piecesToReturn);
                rack.addPieces(TheCup.getInstance().draw(piecesToReturn.size() / 2));
                TheCup.getInstance().addToCup(piecesToReturn);
                toCupButton.deactivate();
                piecesToReturn.clear();
            }
        }
    };

    /*
     * Method to visually generate what is on the rack.
     */
    public void generateButtons() {
        if (images.size() < rack.getPieces().size()) {
            for (int i = 0; i < rack.getPieces().size(); i++) {
                images.add(new ImageView());
                images.get(i).setVisible(true);
                images.get(i).setImage(new Image(rack.getPieces().get(i).getFront(),50,50,false,false));
                images.get(i).addEventHandler(MouseEvent.MOUSE_CLICKED, clickHandler);
                Tooltip.install(images.get(i), new Tooltip(rack.getPieces().get(i).toString()));
                rackBox.getChildren().add(images.get(i));
            }
        }
        if (ClickObserver.getInstance().getClickedTerrain() != null)
            PlayerRackGUI.updateRack();
    }

    public void setOwner(Player p) {
        owner = p;
        rack = owner.getPlayerRack();
        rackBox.getChildren().clear();
        images.clear();
        generateButtons();

        if (GameLoop.getInstance().getPhase() == 0 || GameLoop.getInstance().getPhase() == 3)
            toCupButton.activate();
        else
            toCupButton.deactivate();
    }

    public static void disableAll() {
        for (ImageView iv : images) {
            iv.setOpacity(0.5);
        }
    }


    public Player getOwner() { return owner; }

    public static void updateRack() {
        if (ClickObserver.getInstance().getClickedTerrain().getOwner() != owner) {
            for (ImageView iv : images) {
                iv.setOpacity(0.5);
            }
        }
        else if (ClickObserver.getInstance().getClickedTerrain().getOwner() == owner) {
            for (int i = 0; i < rack.getPieces().size(); i++) {
                if (rack.getPieces().get(i).isPlayable()) {
                    images.get(i).setOpacity(1);
                }
                else {
                    images.get(i).setOpacity(0.5);
                }
            }
        }
    }

    public void update() {
        rackBox.getChildren().clear();
        images.clear();
        generateButtons();
    }

    public PlayerRack getRack() { return rack; }
}

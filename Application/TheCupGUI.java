package KAT;

import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.control.TextField;
import java.lang.Character;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/*
 * Class for the GUI portion of the cup. Needs to be cleaned up.
 */
public class TheCupGUI {
    private ImageView            cupImage; //Image representing the cup.
    private VBox                 cupBox, cupVBoxRecruit; //VBox to hold all of the components
    private HBox                 cupHBoxDraw, cupHBoxRecruit, drawnPieces;
    private TheCup               cup; //One instance of the cup
    private boolean              gridExists; //used for displaying the randomly drawn pieces
    private Button[]             b; //used to represent the randomly drawn pieces. Eventually they will be displaying the images rather than random numbers
    private static Button        drawButton, freeButton, paidButton;
    private static TextField     textField; //used for specifying how many pieces to draw from the cup
    private GridPane             cupGrid;
    private static PlayerRackGUI rackG;
    private static GameLoop      gameLoop;
    private static boolean       paidPressed, freePressed;

    public TheCupGUI(BorderPane bp, PlayerRackGUI rg) {
        gridExists = false;
        cupBox = new VBox(5);
        cupVBoxRecruit = new VBox(5);
        drawnPieces = new HBox(5);
        cupHBoxDraw = new HBox(5);
        cupHBoxRecruit = new HBox(5);
        paidPressed = false;

        cup = TheCup.getInstance();
        gameLoop = GameLoop.getInstance();

        rackG = rg;

        draw(bp);
        
        bp.getChildren().add(cupBox);
    }


    /*
     * Method to show all of the GUI components of the cup.
     */
    private void draw(BorderPane bp) {
        //Displays the cup
        cupImage = new ImageView(new Image("Images/Dtopnica_chalice.png", 100,100,false,false));

        textField = new TextField();
        textField.setPromptText("How many?");
        textField.setPrefColumnCount(5);
        textField.setMinSize(90, 20);

        drawButton = new Button("Draw");
        drawButton.setMinSize(65, 20);
        drawButton.setDisable(true);
        drawButton.addEventHandler(MouseEvent.MOUSE_CLICKED, drawHandler);
        

        freeButton = new Button("Free");
        freeButton.setMinSize(50,50);
        freeButton.setDisable(true);
        freeButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                gameLoop.setFree(true);
                gameLoop.setPaid(false);
                freeButton.setDisable(true);
                textField.setDisable(true);
                paidPressed = false;
                freePressed = true;
            }
        });

        paidButton = new Button("Paid");
        paidButton.setMinSize(50,50);
        paidButton.setDisable(true);
        paidButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                gameLoop.setPaid(true);
                gameLoop.setFree(false);
                paidButton.setDisable(true);
                textField.setDisable(false);
                paidPressed = true;
            }
        });

        cupGrid = new GridPane();
        cupGrid.getColumnConstraints().add(new ColumnConstraints(55));
        cupGrid.getColumnConstraints().add(new ColumnConstraints(50));
        cupGrid.setVgap(5);

        cupHBoxDraw.getChildren().addAll(textField, drawButton);
        cupVBoxRecruit.getChildren().addAll(freeButton, paidButton);
        cupHBoxRecruit.getChildren().addAll(cupImage, cupVBoxRecruit);

        cupBox.relocate(bp.getWidth() - 175, 50);
        cupBox.getChildren().addAll(cupHBoxRecruit, cupHBoxDraw, drawnPieces);

        b = new Button[10];
        for (int i = 0; i < 10; i++) {
            b[i] = new Button();
            b[i].setStyle("-fx-font: 10 arial;");
            b[i].setPrefSize(50, 75);
            b[i].setMinSize(50,50);
            //Whenever one of the buttons is clicked, add it to the player's rack.
            b[i].setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    Button tmp = (Button)e.getSource();
                    System.out.println("ADDING PIECE TO OWNER: " + rackG.getOwner().getName());
                    rackG.getOwner().getPlayerRack().addPiece(cup.getOriginal().get(Integer.parseInt(tmp.getText())));
                    tmp.setVisible(false);
                }
            });
            drawnPieces.getChildren().add(b[i]);
        }
        setVis(b);
    }

    //Handles when the user presses the draw button.
    EventHandler<MouseEvent> drawHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent e) {
            if (e.getClickCount() == 1) {
                int k = 0, n;
                if (paidPressed && freePressed)
                    drawButton.setDisable(true);
                HashMap<Integer,Integer> strList = new HashMap<Integer,Integer>();
                if (paidPressed) {
                    if (sanitizeText(textField.getText()) * 5 > rackG.getOwner().getGold()) {
                        textField.setText("" + (rackG.getOwner().getGold() / 5));
                        rackG.getOwner().removeGold(sanitizeText(textField.getText()) * 5);
                    }
                    else {
                        rackG.getOwner().removeGold(sanitizeText(textField.getText()) * 5);
                    }
                }
                strList = cup.drawPieces(sanitizeText(textField.getText()));
                System.out.println(strList);
                textField.setText("");
                textField.setDisable(true);
                n = getSize(strList);
                //System.out.println(strList + " size=" + strList.size() + " n=" + n);

                //This section only gets executed the first time the draw button is pressed.
                if (!gridExists) {
                    for (int i = 0; i < strList.size(); i++) {
                        System.out.println("---STRLIST.GET(I): "+strList.get(i));
                        System.out.println("---CUP.GETORIGINAL.GET(STRLIST.GET(I): " + cup.getOriginal().get(strList.get(i)));
                        if (cup.getOriginal().get(strList.get(i)).getFront().equals(""))
                            b[i].setText(strList.get(i).toString());
                        else {
                            b[i].setText(strList.get(i).toString());
                            b[i].setGraphic(new ImageView(new Image(cup.getOriginal().get(strList.get(i)).getFront(),50,50,false,false)));
                        }
                        b[i].setVisible(true);
                        if (k < strList.size()-1)
                            k++;
                        else
                            break;
                    }
                    cupBox.getChildren().add(cupGrid);
                    gridExists = true;
                }
                //This section gets executed when teh draw button has already been pressed once.
                else {
                    setVis(b);
                    for (int i = 0; i < strList.size(); i++) {
                        System.out.println("---STRLIST.GET(I): " + strList.get(i));
                        System.out.println("---CUP.GETORIGINAL.GET(STRLIST.GET(I)): " + cup.getOriginal().get(strList.get(i)));
                            if (cup.getOriginal().get(strList.get(i)).getFront().equals(""))
                                b[i].setText(strList.get(i).toString());
                            else {
                                b[i].setText(strList.get(i).toString());
                                b[i].setGraphic(new ImageView(new Image(cup.getOriginal().get(strList.get(i)).getFront(),50,50,false,false)));
                            }
                            b[i].setVisible(true);
                            if (i < strList.size() - 1)
                                k++;
                            else
                                break;
                    }
                }
            }
        }
    };

    public static void update() {
        if (gameLoop.getPhase() == 3) {
            System.out.println(rackG.getOwner().getName() + " -update");
            drawButton.setDisable(false);
            freeButton.setDisable(false);
            if (rackG.getOwner().getGold() >= 5)
                paidButton.setDisable(false);
            else
                paidButton.setDisable(true);
        }
        else {
            drawButton.setDisable(true);
            paidButton.setDisable(true);
            freeButton.setDisable(true);
        }
    }

    /*
     * Method to determine the size needed to display the pieces drawn from the cup.
     */
    private int getSize(HashMap<Integer,Integer> s) {
        if (s.size() == 1)
            return 0;
        if (s.size() % 2 == 0)
            return s.size() / 2;
        else
            return s.size() / 2 + 1;
    }

    /*
     * Helper method to set all of the buttons to not visible.
     */
    private void setVis(Button[] buttons) {
        for (int i = 0; i < 10; i++)
            buttons[i].setVisible(false);
    }

    public static void setFieldText(String s) { 
        textField.setDisable(true);
        textField.setText(s);
    }

    /*
     * Method to sanitize the value of a string and convert it to an int.
     */
    private int sanitizeText(String s) {
        int val = 0;
        if (s.equals(""))
            return 0;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i)))
                return 0;
        }
        val = Integer.parseInt(s);
        if (val > 10)
            return 10;
        return val;
    }
}

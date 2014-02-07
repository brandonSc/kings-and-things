package KAT;

import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import java.util.ArrayList;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.control.TextField;
import java.lang.Character;

/*
 * Class for the GUI portion of the cup. Needs to be cleaned up.
 */
public class TheCupGUI {
    private Rectangle     cupImage; //Eventually I'm hoping we can get a picture of a chalice or something instead of a rectangle
    private Label         cupLabel; 
    private VBox          cupBox; //VBox to hold all of the components
    private HBox          cupHBox;
    private TheCup        cup; //One instance of the cup
    private boolean       gridExists; //used for displaying the randomly drawn pieces
    private Button[][]    b; //used to represent the randomly drawn pieces. Eventually they will be displaying the images rather than random numbers
    private static Button        drawButton;
    private static TextField     textField; //used for specifying how many pieces to draw from the cup
    private GridPane      cupGrid;
    private PlayerRackGUI rackG;
    private static GameLoop      gameLoop;
    private static boolean paused;

    public TheCupGUI(BorderPane bp, PlayerRackGUI rg) {
        gridExists = false;
        cupBox = new VBox(5);
        cupHBox = new HBox(5);
        paused = false;

        cup = TheCup.getInstance();
        gameLoop = GameLoop.getInstance();

        rackG = rg;

        draw(bp);
        
        bp.getChildren().add(cupBox);
    }


    /*
     * This method will eventually be broken down so it isn't so huge.
     */
    private void draw(BorderPane bp) {
        //Displays the cup. Will eventually be a chalice instead of some shitty yellow square.
        cupImage = new Rectangle(100, 100, Color.YELLOW);
        cupImage.setStroke(Color.BLACK);
        cupImage.setStrokeWidth(1.5);

        cupLabel = new Label("The Cup");
        cupLabel.setLabelFor(cupImage);
        cupLabel.setMinSize(75, 20);

        textField = new TextField();
        textField.setPromptText("How many?");
        textField.setPrefColumnCount(5);
        textField.setMinSize(90, 20);

        drawButton = new Button("Draw");
        drawButton.setMinSize(65, 20);

        cupGrid = new GridPane();
        cupGrid.getColumnConstraints().add(new ColumnConstraints(55));
        cupGrid.getColumnConstraints().add(new ColumnConstraints(50));
        cupGrid.setVgap(5);

        cupHBox.getChildren().addAll(textField, drawButton);

        cupBox.relocate(bp.getWidth() - 175, 50);
        cupBox.getChildren().addAll(cupLabel, cupImage, cupHBox);

        b = new Button[2][5];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 5; j++) {
                b[i][j] = new Button();
                b[i][j].setStyle("-fx-font: 10 arial;");
                b[i][j].setPrefSize(50, 75);
                b[i][j].setMinSize(50,50);
                //Whenever one of the buttons is clicked, add it to the player's rack.
                b[i][j].setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        Button tmp = (Button)e.getSource();
                        rackG.getRack().getPieces().add(cup.getOriginal().get(Integer.parseInt(tmp.getText())));
                        rackG.generateButtons();
                        tmp.setVisible(false);
                        System.out.println(rackG.getRack().getPieces());
                    }
                });
                cupGrid.add(b[i][j], i, j);
            }
        }
        setVis(b);
        
        //Handles when the user clicks on the draw button.
        drawButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int k = 0, n;
                    paused = true;
                    ArrayList<Piece> strList = new ArrayList<Piece>();
                    strList = cup.drawPieces(sanitizeText(textField.getText()));
                    n = getSize(strList);
                    System.out.println(strList + " size=" + strList.size() + " n=" + n);

                    //This section only gets executed the first time the draw button is pressed.
                    if (!gridExists) {
                        if (n > 0) {
                            for (int i = 0; i < 2; i++) {
                                for (int j = 0; j < n; j++) {
                                    b[i][j].setText(strList.get(k).getName());
                                    b[i][j].setVisible(true);
                                    if (k < strList.size()-1)
                                        k++;
                                    else
                                        break;
                                }
                            }
                        }
                        //This section handles when the user is only drawing one thing from the cup.
                        else if (strList.size() == 1) {
                            b[0][0].setText(strList.get(k).getName());
                            b[0][0].setVisible(true);
                        }
                        cupBox.getChildren().add(cupGrid);
                        gridExists = true;
                    }
                    //This section gets executed when teh draw button has already been pressed once.
                    else {
                        setVis(b);
                        if (n > 0) {
                            for (int i = 0; i < 2; i++) {
                                for (int j = 0; j < n; j++) {
                                    b[i][j].setText(strList.get(k).getName());
                                    b[i][j].setVisible(true);
                                    if (k < strList.size() - 1)
                                        k++;
                                    else
                                        break;
                                }
                            }
                        }
                        //This section handles when the user is only drawing one thing from the cup.
                        else if (strList.size() == 1) {
                            b[0][0].setText(strList.get(k).getName());
                            b[0][0].setVisible(true);
                        }
                    }
                }
            }
        });
    }

    public static void update() {
        if (gameLoop.getPhase() != 3)
            drawButton.setVisible(false);
        else
            drawButton.setVisible(true);
    }

    public static boolean getPaused() {
        return paused;
    }

    public static void setPaused(boolean b) { paused = b; }

    /*
     * Method to determine the size needed to display the pieces drawn from the cup.
     */
    private int getSize(ArrayList<Piece> s) {
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
    private void setVis(Button[][] buttons) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 5; j++) {
                buttons[i][j].setVisible(false);
            }
        }
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
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
    private Rectangle  cupImage; //Eventually I'm hoping we can get a picture of a chalice or something instead of a rectangle
    private Label      cupLabel; 
    private VBox       cupBox; //VBox to hold all of the components
    private HBox       cupHBox;
    private TheCup     cup; //One instance of the cup
    private boolean    gridExists; //used for displaying the randomly drawn pieces
    private Button[][] b; //used to represent the randomly drawn pieces. Eventually they will be displaying the images rather than random numbers
    private Button     drawButton;
    private TextField  textField; //used for specifying how many pieces to draw from the cup
    private GridPane   cupGrid;

    public TheCupGUI(BorderPane bp) {
        gridExists = false;
        cupBox = new VBox(5);
        cupHBox = new HBox(5);

        cup = TheCup.getInstance();

        draw(bp);

        bp.getChildren().add(cupBox);
    }


    /*
     * This method will eventually be broken down so it isn't so huge.
     */
    private void draw(BorderPane bp) {
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

        cupHBox.getChildren().addAll(textField, drawButton);

        cupBox.relocate(bp.getWidth() - 175, 50);
        cupBox.getChildren().addAll(cupLabel, cupImage, cupHBox);

        b = new Button[2][5];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 5; j++) {
                b[i][j] = new Button();
                b[i][j].setPrefSize(50, 50);
                cupGrid.add(b[i][j], i, j);
            }
        }
        setVis(b);

        drawButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int k = 0;
                    int n;
                    ArrayList<String> strList = new ArrayList<String>();
                    strList = cup.drawPieces(sanitizeText(textField.getText()));
                    n = getSize(strList);
                    System.out.println(strList + " size=" + strList.size() + " n=" + n);

                    if (!gridExists) {
                        for (int i = 0; i < 2; i++) {
                            for (int j = 0; j < n; j++) {
                                b[i][j].setText("" + strList.get(k));
                                System.out.println(k);
                                b[i][j].setVisible(true);
                                if (k < strList.size()-1)
                                    k++;
                                else
                                    break;
                            }
                        }
                        cupBox.getChildren().add(cupGrid);
                        gridExists = true;
                    }
                    else {
                        setVis(b);
                        for (int i = 0; i < 2; i++) {
                            for (int j = 0; j < n; j++) {
                                b[i][j].setText("" + strList.get(k));
                                b[i][j].setVisible(true);
                                if (k < strList.size() - 1)
                                    k++;
                                else
                                    break;
                            }
                        }
                    }
                }
            }
        });
    }

    private int getSize(ArrayList<String> s) {
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

    /*
     * Method to sanitize the value of a string and convert it to an int.
     */
    private int sanitizeText(String s) {
        int val = 0;
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
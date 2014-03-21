package KAT;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.shape.Circle;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.layout.GridPane;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.collections.FXCollections;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;

public class DiceGUI {
    private static DiceGUI uniqueInstance;
    private BorderPane borPane;
    private Rectangle mainPiece;
    private GridPane valueGridOne, valueGridTwo;
    private Circle[][] valueDotsOne;
    private Circle[][] valueDotsTwo;
    private Group diceGroupOne, diceGroupTwo;
    private Button rollButton;
    private ChoiceBox diceSelection;
    private Label rollLabelOne, rollLabelTwo;

    
    private DiceGUI() {
        mainPiece = new Rectangle();
        rollLabelOne = new Label("ROLL");
        rollLabelTwo = new Label("ROLL");

        valueGridOne = new GridPane();
        valueGridTwo = new GridPane();

        valueDotsOne = new Circle[3][3];
        valueDotsTwo = new Circle[3][3];

        diceGroupOne = new Group();
        diceGroupTwo = new Group();
        diceSelection = new ChoiceBox(FXCollections.observableArrayList("One","Two"));
    }

    public void draw() {
        diceGroupOne = GroupBuilder.create()
                    .children(RectangleBuilder.create()
                        .width(75)
                        .height(75)
                        .fill(Color.WHITE)
                        .stroke(Color.BLACK)
                        .build())
                    .layoutX(borPane.getWidth() * 0.1)
                    .layoutY(borPane.getHeight() * 0.91)
                    .clip(RectangleBuilder.create()
                        .width(75)
                        .height(75)
                        .build())
                    .build();
        diceGroupOne.addEventHandler(MouseEvent.MOUSE_CLICKED, handlerOne);
        
        diceGroupTwo = GroupBuilder.create()
                    .children(RectangleBuilder.create()
                        .width(75)
                        .height(75)
                        .fill(Color.WHITE)
                        .stroke(Color.BLACK)
                        .build())
                    .layoutX(borPane.getWidth() * 0.15)
                    .layoutY(borPane.getHeight() * 0.91)
                    .clip(RectangleBuilder.create()
                        .width(75)
                        .height(75)
                        .build())
                    .build();
        diceGroupTwo.addEventHandler(MouseEvent.MOUSE_CLICKED, handlerTwo);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                valueDotsOne[i][j] = new Circle(5.0);
                valueDotsOne[i][j].setFill(Color.BLACK);

                valueDotsTwo[i][j] = new Circle(5.0);
                valueDotsTwo[i][j].setFill(Color.BLACK);
            }
        }

        rollLabelOne.setVisible(true);
        rollLabelTwo.setVisible(true);

        diceGroupOne.setVisible(false);
        diceGroupTwo.setVisible(false);

        diceSelection.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue ov, Number value, Number new_value) {
                update(new_value.intValue());
            }
        });

        diceSelection.setLayoutX(borPane.getWidth() * 0.045);
        diceSelection.setLayoutY(borPane.getHeight() * 0.9645);
        diceSelection.setMinSize(50,20);

        valueGridOne.getColumnConstraints().add(new ColumnConstraints(30));
        valueGridOne.getRowConstraints().add(new RowConstraints(25));
        valueGridOne.getColumnConstraints().add(new ColumnConstraints(30));
        valueGridOne.getRowConstraints().add(new RowConstraints(25));
        valueGridOne.getColumnConstraints().add(new ColumnConstraints(30));
        valueGridOne.getRowConstraints().add(new RowConstraints(25));

        valueGridTwo.getColumnConstraints().add(new ColumnConstraints(30));
        valueGridTwo.getRowConstraints().add(new RowConstraints(25));
        valueGridTwo.getColumnConstraints().add(new ColumnConstraints(30));
        valueGridTwo.getRowConstraints().add(new RowConstraints(25));
        valueGridTwo.getColumnConstraints().add(new ColumnConstraints(30));
        valueGridTwo.getRowConstraints().add(new RowConstraints(25));

        diceGroupOne.getChildren().addAll(valueGridOne, rollLabelOne);
        diceGroupTwo.getChildren().addAll(valueGridTwo, rollLabelTwo);
        borPane.getChildren().addAll(diceGroupOne, diceGroupTwo, diceSelection);

        initDots();
    }

    private void update(int diceNumber) {
        if (diceNumber == 0) {
            diceGroupOne.setVisible(true);
            diceGroupTwo.setVisible(false);
            setFaceValue(0,0);
        }
        else {
            diceGroupOne.setVisible(true);
            diceGroupTwo.setVisible(true);
            setFaceValue(0,0);
            setFaceValue(0,1);
        }
    }

    private void showLabel(int die) {
        if (die == 0) {
            hideDots(1);
            rollLabelOne.setVisible(true);
        }
        if (die == 1) {
            hideDots(2);
            rollLabelTwo.setVisible(true);
        }
    }

    private void hideLabel(int die) {
        if (die == 0)
            rollLabelOne.setVisible(false);
        if (die == 1)
            rollLabelTwo.setVisible(false);
    }

    public static DiceGUI getInstance() {
        if (uniqueInstance == null)
            uniqueInstance = new DiceGUI();
        return uniqueInstance;
    }

    private void initDots() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                valueGridOne.add(valueDotsOne[i][j],i,j);
                valueDotsOne[i][j].setVisible(false);

                valueGridTwo.add(valueDotsTwo[i][j],i,j);
                valueDotsTwo[i][j].setVisible(false);
            }
        }
    }

    private void hideDots(int dice) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (dice == 1)
                    valueDotsOne[i][j].setVisible(false);
                if (dice == 2)
                    valueDotsTwo[i][j].setVisible(false);
            }
        }
    }

    public void setFaceValue(int val, int die) {
        switch (val) {
            case 1: if (die == 0) {
                        hideDots(1);
                        valueDotsOne[1][1].setVisible(true);
                        break;
                    }
                    if (die == 1) {
                        hideDots(2);
                        valueDotsTwo[1][1].setVisible(true);
                        break;
                    }

            case 2: if (die == 0) {
                        hideDots(1);
                        valueDotsOne[1][0].setVisible(true);
                        valueDotsOne[1][2].setVisible(true);
                        break;
                    }
                    if (die == 1) {
                        hideDots(2);
                        valueDotsTwo[1][0].setVisible(true);
                        valueDotsTwo[1][2].setVisible(true);
                        break;
                    }

            case 3: if (die == 0) {
                        hideDots(1);
                        valueDotsOne[0][0].setVisible(true);
                        valueDotsOne[1][1].setVisible(true);
                        valueDotsOne[2][2].setVisible(true);
                        break;
                    }
                    if (die == 1) {
                        hideDots(2);
                        valueDotsTwo[0][0].setVisible(true);
                        valueDotsTwo[1][1].setVisible(true);
                        valueDotsTwo[2][2].setVisible(true);
                        break;
                    }

            case 4: if (die == 0) {
                        hideDots(1);
                        valueDotsOne[0][0].setVisible(true);
                        valueDotsOne[2][0].setVisible(true);
                        valueDotsOne[0][2].setVisible(true);
                        valueDotsOne[2][2].setVisible(true);
                        break;
                    }
                    if (die == 1) {
                        hideDots(2);
                        valueDotsTwo[0][0].setVisible(true);
                        valueDotsTwo[2][0].setVisible(true);
                        valueDotsTwo[0][2].setVisible(true);
                        valueDotsTwo[2][2].setVisible(true);
                        break;
                    }

            case 5: if (die == 0) {
                        hideDots(1);
                        valueDotsOne[0][0].setVisible(true);
                        valueDotsOne[2][0].setVisible(true);
                        valueDotsOne[1][1].setVisible(true);
                        valueDotsOne[0][2].setVisible(true);
                        valueDotsOne[2][2].setVisible(true);
                        break;
                    }
                    if (die == 1) {
                        hideDots(2);
                        valueDotsTwo[0][0].setVisible(true);
                        valueDotsTwo[2][0].setVisible(true);
                        valueDotsTwo[1][1].setVisible(true);
                        valueDotsTwo[0][2].setVisible(true);
                        valueDotsTwo[2][2].setVisible(true);
                        break;
                    }

            case 6: if (die == 0) {
                        hideDots(1);
                        valueDotsOne[0][0].setVisible(true);
                        valueDotsOne[0][1].setVisible(true);
                        valueDotsOne[0][2].setVisible(true);
                        valueDotsOne[2][0].setVisible(true);
                        valueDotsOne[2][1].setVisible(true);
                        valueDotsOne[2][2].setVisible(true);
                        break;
                    }
                    if (die == 1) {
                        hideDots(2);
                        valueDotsTwo[0][0].setVisible(true);
                        valueDotsTwo[0][1].setVisible(true);
                        valueDotsTwo[0][2].setVisible(true);
                        valueDotsTwo[2][0].setVisible(true);
                        valueDotsTwo[2][1].setVisible(true);
                        valueDotsTwo[2][2].setVisible(true);
                        break;
                    }

            default: if (die == 0) { 
                        hideDots(1);
                        showLabel(0);
                        break;
                    }
                    if (die == 1) {
                        hideDots(2);
                        showLabel(1);
                        break;
                    }
        }
    }

    EventHandler<MouseEvent> handlerOne = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent e) {
            hideLabel(0);
            setFaceValue(Dice.roll(), 0);
        }
    };

    EventHandler<MouseEvent> handlerTwo = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent e) {
            hideLabel(1);
            setFaceValue(Dice.roll(), 1);
        }
    };

    public void setBorderPane(BorderPane bp) { borPane = bp; }
    public BorderPane getBorderPane() { return borPane; }
}
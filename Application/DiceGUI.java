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

public class DiceGUI {
	private static DiceGUI uniqueInstance;
	private BorderPane borPane;
	private Rectangle mainPiece;
	private GridPane valueGrid;
	private Circle[][] valueDots;
	private Group diceGroup;
	
	private DiceGUI() {
		mainPiece = new Rectangle();
		valueGrid = new GridPane();
		valueDots = new Circle[3][3];
		diceGroup = new Group();
	}

	public void draw() {
		System.out.println("Drawing this stupid die.");

		diceGroup = GroupBuilder.create()
					.children(RectangleBuilder.create()
						.width(75)
						.height(75)
						.fill(Color.WHITE)
						.stroke(Color.BLACK)
						.build())
					.layoutX(borPane.getWidth() * 0.22)
					.layoutY(borPane.getHeight() * 0.8)
					.clip(RectangleBuilder.create()
						.width(75)
						.height(75)
						.build())
					.build();

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				valueDots[i][j] = new Circle(5.0);
				valueDots[i][j].setFill(Color.BLACK);
			}
		}

		valueGrid.getColumnConstraints().add(new ColumnConstraints(30));
		valueGrid.getRowConstraints().add(new RowConstraints(25));
		valueGrid.getColumnConstraints().add(new ColumnConstraints(30));
		valueGrid.getRowConstraints().add(new RowConstraints(25));
		valueGrid.getColumnConstraints().add(new ColumnConstraints(30));
		valueGrid.getRowConstraints().add(new RowConstraints(25));

		diceGroup.getChildren().addAll(valueGrid);
		borPane.getChildren().add(diceGroup);

		initDots();
	}

	public static DiceGUI getInstance() {
		if (uniqueInstance == null)
			uniqueInstance = new DiceGUI();
		return uniqueInstance;
	}

	private void initDots() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				valueGrid.add(valueDots[i][j],i,j);
				valueDots[i][j].setVisible(false);
			}
		}
	}

	private void hideDots() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				valueDots[i][j].setVisible(false);
			}
		}
	}

	public void setFaceValue(int val) {
		//valueGrid.getChildren().removeAll();
		switch (val) {
			case 1: hideDots();
					valueDots[1][1].setVisible(true);
					break;
			case 2: hideDots();
					valueDots[1][0].setVisible(true);
					valueDots[1][2].setVisible(true);
					break;			
			case 3: hideDots();
					valueDots[0][0].setVisible(true);
					valueDots[1][1].setVisible(true);
					valueDots[2][2].setVisible(true);
					break;
			case 4: hideDots();
					valueDots[0][0].setVisible(true);
					valueDots[2][0].setVisible(true);
					valueDots[0][2].setVisible(true);
					valueDots[2][2].setVisible(true);
					break;
			case 5: hideDots();
					valueDots[0][0].setVisible(true);
					valueDots[2][0].setVisible(true);
					valueDots[1][1].setVisible(true);
					valueDots[0][2].setVisible(true);
					valueDots[2][2].setVisible(true);
					break;
			case 6: hideDots();
					valueDots[0][0].setVisible(true);
					valueDots[0][1].setVisible(true);
					valueDots[0][2].setVisible(true);
					valueDots[2][0].setVisible(true);
					valueDots[2][1].setVisible(true);
					valueDots[2][2].setVisible(true);
					break;
		}
	}

	public void setBorderPane(BorderPane bp) { borPane = bp; }
	public BorderPane getBorderPane() { return borPane; }
}
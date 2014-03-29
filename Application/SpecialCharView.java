package KAT;

import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import java.util.ArrayList;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;

public class SpecialCharView {
	private static GameButton specialCharButton, recruitButton, cancelButton;
	private ArrayList<SpecialCharacter> characterList;
	private ArrayList<Button> tmplist;
	private GridPane characterGrid;

	public SpecialCharView(BorderPane bp) {
		characterList = new ArrayList<SpecialCharacter>();
		tmplist = new ArrayList<Button>();
		specialCharButton = new GameButton(180, 35, bp.getWidth()*0.80, bp.getHeight()*0.175, "Special Characters", charHandler);
		recruitButton = new GameButton(100, 35, bp.getWidth()*0.84, bp.getHeight()*0.175, "Recruit", recruitHandler);
		cancelButton = new GameButton(80, 35, bp.getWidth()*0.8-5, bp.getHeight()*0.175, "Cancel", cancelHandler);
		recruitButton.hide();
		cancelButton.hide();
		//specialCharButton.deactivate();

		characterGrid = new GridPane();
		characterGrid.setVgap(5);
		characterGrid.setHgap(5);

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 8; j++) {
				tmplist.add(new Button(""+ (j + 8*i)));
				tmplist.get(j+i*8).setMinSize(50,50);

				characterGrid.add(tmplist.get(j+8*i), j, i);
			}
		}

		characterGrid.setVisible(false);
		characterGrid.relocate(bp.getWidth()*0.665, bp.getHeight()*0.06);

		bp.getChildren().addAll(characterGrid, specialCharButton.getNode(), recruitButton.getNode(), cancelButton.getNode());
	}

	EventHandler<MouseEvent> charHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
			specialCharButton.hide();
			recruitButton.show();
			recruitButton.activate();
			cancelButton.show();
			cancelButton.activate();
			characterGrid.setVisible(true);
		}
	};

	EventHandler<MouseEvent> recruitHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
			recruitButton.hide();
			recruitButton.deactivate();
			cancelButton.hide();
			cancelButton.deactivate();
			specialCharButton.show();
			specialCharButton.activate();
			characterGrid.setVisible(false);
		}
	};

	EventHandler<MouseEvent> cancelHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
			recruitButton.hide();
			recruitButton.deactivate();
			cancelButton.hide();
			cancelButton.deactivate();
			specialCharButton.show();
			specialCharButton.activate();
			characterGrid.setVisible(false);
		}
	};

	public static GameButton getSpecialButton() { return specialCharButton; }
}
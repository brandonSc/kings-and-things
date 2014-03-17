package KAT;

import javafx.scene.image.Image;
import javafx.scene.Group;

public class SpecialIncome extends Piece {
	private int value;
	private boolean treasure;

	public SpecialIncome(String front, String back, String name, int val, boolean tr) {
		super("Special Income", front, back, name);
		value = val;
		treasure = tr;
	}

	public SpecialIncome(String input) {
		separateInput(input);
	}

	private void separateInput(String in) {
		String[] input = in.split(",");
		setFront(input[0]);
		setBack(input[1]);
		setName(input[2]);
		setValue(Integer.parseInt(input[3]));
		setTreasure((input[4].equals("true")) ? true : false);
	}

	@Override
	public boolean isPlayable() {
		if (isTreasure())
			return true;
		return false;
	}

	public void setValue(int v) { value = v; }
	public int getValue() { return value; }
	public void setTreasure(boolean b) { treasure = b; }
	public boolean isTreasure() { return treasure; }

	@Override
	public String toString() {
		return "Name: " + getName() + "\n" + "Value: " + getValue() + "\n";
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override 
	public Group getPieceNode() {
		return null;
	}
}
package KAT;

public class Gold extends Piece {
	int value;

	public Gold() {
		super("Gold", "", "");
		value = 0;
	}

	public Gold(int val) {
		super("Gold", "", "");
		value = val;
	}

	public Gold(int val, String front, String back) {
		super("Gold", front, back);
		value = val;
	}
}
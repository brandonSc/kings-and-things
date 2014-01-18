import java.util.Random;

/*
 * Class to represent Dice
 */

public class Dice {
	public int val;

	public Dice() {
		this.val = 0;
	}

	/*
	 * @return int - random number between 1 and 6
	 */
	public int roll() {
		Random rand = new Random();
		val = rand.nextInt(6) + 1;

		return val;
	}
}
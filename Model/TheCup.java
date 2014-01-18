import java.util.Random;
import java.util.ArrayList;

/*
 * Class to represent the game Cup and its functionality
 * Note: Modified to use the 'singleton' pattern from our text book
 */
public class TheCup {
	//An ArrayList of pieces remaining in the cup
	private ArrayList<Piece> remainingPieces = new ArrayList<Piece>();
    //A unique and single instance of this class, retrieved by getInstance()
    private static TheCup uniqueInstance;

	private TheCup() {
		initCup();
	}

    /**
     * @return the single unique instance of this class
     */
    public static TheCup getInstance(){
        if( uniqueInstance == null ){
            uniqueInstance = new TheCup();
        }
        return uniqueInstance;
    }

	/*
	 * Function to randomly draw pieces from the Cup. Returns an arraylist of the pieces drawn.
	 */
	public ArrayList<Piece> drawPieces(int numberToDraw) {
		Random rand = new Random();
		ArrayList<Piece> pieces = new ArrayList<Piece>();
		if (remainingPieces.size() == 0) {
			System.out.println("No more pieces left to draw.");
			return null;
		}

		for (int i = 0; i < numberToDraw; i++) {
			int index = rand.nextInt(remainingPieces.size());
			pieces.add(remainingPieces.get(index));
			remainingPieces.remove(index);
		}

		return pieces;
	}

	/* 
	 * This method fills the remainingPieces arraylist with all initial game pieces.
	 */
	private void initCup() {
		//TODO

	}

	/*
	 * Method to add a piece to the cup
	 */
	public void addToCup(Piece p) {
		remainingPieces.add(p);
	}

	public ArrayList<Piece> getRemaining() { return remainingPieces; }
}

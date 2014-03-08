package KAT;

import javafx.scene.image.Image;
import javafx.scene.Group;

public class RandomEvent extends Piece {
	private String description;

	public RandomEvent(String front, String back, String name) {
		super("Random Event", front, back, name);
	}

	@Override
	public String toString() {
		return "Random Event: " + getName() + "\n" + getDescription() + "\n";
	}

	/*
	 * A random event is only playable during phase 4 - random event phase.
	 */
	public boolean isPlayable() {
		if (GameLoop.getInstance().getPhase() != 4)
			return false;
		else
			return true;
	}

	public void setDescription(String d) { description = d; }
	public String getDescription() { return description; }

	@Override
	public Image getImage() {
		return null;
	}

	@Override 
	public Group getPieceNode() {
		return null;
	}
}
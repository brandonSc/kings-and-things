package KAT;

public class BigJuju extends RandomEvent {
	private Player owner;

	public BigJuju() {
		super("Images/Event_BigJulu.png", "Images/Creature_Back.png", "Big Juju");
		owner = null;
		setDescription("Big JuJu can change one hex on the board to something completely different! View the Leaflet for the in-depth rules.");
	}

	/*
	 * Procedure to determine whether BigJuJu is playable during the random event phase.
	 *
	 * If the Owner has no magic creatures on the field, Big JuJu is not playable.
	 */
	@Override
	public boolean isPlayable() {
		if (GameLoop.getInstance().getPhase() != 4)
			return false;

		for (Terrain t : owner.getHexesWithPiece()) {
			CreatureStack c = t.getContents(owner.getName());
			for (int i = 0; i < c.getStack().size(); i++) {
				if (c.getStack().get(i).isMagic())
					return true;
			}
		}
		return false;
	}

	public void setOwner(Player p) { owner = p; }
	public Player getOwner() { return owner; }

	/*
	 * Check if the hex you wish to change is within the range of one of your magic creatures, 
	 * i.e. within a distance smaller than your magic creature's combat value.
	 *
	 * If the hex you wish to change is your own, you may look through the tile deck and pick any tile you wish.
	 * If the hex you wish to change is not your own, shuffle the tile deck and pick the top tile. If the drawn tile is the same type as the one you are 
	 * trying to change, draw again until you draw a hex that is a different type.
	 *
	 * Terrain-dependent special incomes are lost when the hex changes (they get returned to the cup).
	 *
	 * The owner of the switched hex can place unused counters from his rack in the hex.
	 *
	 * If the replacement hex is a sea hex, all creatures in the hex can move to an adjacent friendly hex.If none exists, the creatures/other pieces are returned to
	 * the cup.
	 *
	 * Big JuJu cannot be played on a hex with a Citadel.
	 */
	public void performAbility() {

	}
}
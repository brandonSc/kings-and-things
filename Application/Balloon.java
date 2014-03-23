package KAT;

public class Balloon extends MagicEvent {
	public Balloon() {
		super("Images/Magic_Balloon.png", "Images/Creature_Back.png", "Balloon");
		setOwner(null);
		setDescription("Carry any three creatures up to three hexes from where they started the phase!\nSee the leaflet for more info.");
	}

	/*
	 * The Balloon is only playable during the Movement Phase.
	 */
	@Override
	public boolean isPlayable() {
		if (GameLoop.getInstance().getPhase() == 5)
			return true;
		return false;
	}

	/*
	 * The Balloon can carry up to 3 creatures from the same hex up to 3 hexes away. The selected creatures may not move normally this turn.
	 * Place the three creatures and the balloon in the target hex.
	 * 
	 * Only creatures with a combat value less than 4 may be transported (this includes Special Characters).
	 * Creatures that were transported now fight as ranged creatures (in the turn they were transported) unless and until the Balloon is eliminated.
	 * The Balloon is eliminated if it takes a hit in combat.
	 *
	 * The Balloon and its passengers may retreat to any friendly hex within 3 hexes of the battle as long as the Balloon is still in play.
	 *
	 * The Balloon is returned to the cup at the end of the turn in which it was used.
	 */
	public void performAbility() {
		if (TheCup.getInstance().getRemaining().size() != 0)
			TheCup.getInstance().addToCup(this);
	}
}
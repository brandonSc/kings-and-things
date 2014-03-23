package KAT;

public class Sword extends MagicEvent {
	public Sword() {
		super("Images/Magic_Sword.png", "Images/Creature_Back.png", "Sword");
		setOwner(null);
		setDescription("Bestow a mighty sword onto one of your creatures!\nSee the leaflet for more info");
	}

	/*
	 * Playable any time a dice is rolled.
	 */
	@Override
	public boolean isPlayable() {
		if (GameLoop.getInstance().getPhase() == 6)
			return true;
		return false;
	}

	/*
	 * Place the Sword on top of one of your creatures engaged in battle.
	 * This creature now fights as Charged for the rest of the battle, and its combat value is increased by 1.
	 * All other symbols on the creature are removed and it now only fights as charged.
	 *
	 * The Sword may be transferred from one creature to another in the same hex during a battle by putting it on top
	 * of a new creature at the beginning of the next round.
	 *
	 * The Sword is eliminated if it takes a hit.
	 * If the creature wielding the Sword is eliminated, the Sword must be given to a new creature at the beginning of the next round.
	 *
	 * The Sword is returned to the cup at the end of the battle in which it's used.
	 */
	public void performAbility() {
		if (TheCup.getInstance().getRemaining().size() != 0)
			TheCup.getInstance().addToCup(this);
	}
}
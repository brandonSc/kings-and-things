package KAT;

public class Bow extends MagicEvent {
	public Bow() {
		super("Images/Magic_Bow.png", "Images/Creature_Back.png", "Bow");
		setOwner(null);
		setDescription("Place a bow on one of your creatures and turn it into a ranged warrior!\nSee the leaflet for more info.");
	}

	/*
	 * The Bow is only playable during the Combat Phase.
	 */
	@Override
	public boolean isPlayable() {
		if (GameLoop.getInstance().getPhase() == 6)
			return true;
		return false;
	}

	/*
	 * Place the Bow on top of one of your creatures engaged in battle.
	 * This creature now fights as Ranged for the rest of the battle, and its combat value is increased by 1.
	 * All other symbols on the creature are removed and it now only fights as ranged.
	 *
	 * The Bow may be transferred from one creature to another in the same hex during a battle by putting it on top
	 * of a new creature at the beginning of the next round.
	 *
	 * The Bow is eliminated if it takes a hit.
	 * If the creature wielding the Bow is eliminated, the Bow must be given to a new creature at the beginning of the next round.
	 *
	 * The Bow is returned to the cup at the end of the battle in which it's used.
	 */
	public void performAbility() {
		if (TheCup.getInstance().getRemaining().size() != 0)
			TheCup.getInstance().addToCup(this);
	}
}
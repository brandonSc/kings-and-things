package KAT;

public class Vandals extends RandomEvent {
	private Player owner;

	public Vandals() {
		super("Images/Event_Vandals.png", "Images/Creature_Back.png", "Vandals");
		owner = null;
		setDescription("Select an opponent and reduce the level of one of their forts! See the Leaflet for the in-depth rules.");
	}

	public void setOwner(Player p) { owner = p; }
	public Player getOwner() { return owner; }


	/*
	 * The owner of this piece selects another player. This player then loses one fort level from any fort
	 * that he currently has on the board (this player chooses).
	 *
	 * If the fort is a tower it is eliminated.
	 * If the fort is a castle or a keep it's level is reduced by one.
	 * If the fort is a citadel, nothing happens.
	 */
	public void performAbility() {
		
	}
}
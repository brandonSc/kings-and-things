package KAT;

public class Vandals extends RandomEvent {

	public Vandals() {
		super("Images/Event_Vandals.png", "Images/Creature_Back.png", "Vandals");
		setOwner(null);
		setDescription("Select an opponent and reduce the level of one of their forts!\nSee the Leaflet for the in-depth rules.");
	}

	/*
	 * The owner of this piece selects another player. This player then loses one fort level from any fort
	 * that he currently has on the board (this player chooses).
	 *
	 * If the fort is a tower it is eliminated.
	 * If the fort is a castle or a keep it's level is reduced by one.
	 * If the fort is a citadel, nothing happens.
	 */
	@Override
	public void performAbility() {
		GameLoop.getInstance().unPause();
		Terrain hex = ClickObserver.getInstance().getClickedTerrain();

		hex.getFort().downgrade();
		hex.setFortImage();

		GameLoop.getInstance().unPause();
	}
}
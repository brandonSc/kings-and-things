package KAT;

public class TerrainDisaster extends RandomEvent {
	private Player owner;

	public TerrainDisaster() {
		super("Images/Event_TerrainDisaster.png", "Images/Creature_Back.png", "Terrain Disaster");
		owner = null;
		setDescription("Cause a natural disaster in one of your opponents hexes! See the Leaflet for the in-depth rules.");
	}

	public void setOwner(Player p) { owner = p; }
	public Player getOwner() { return owner; }


	/*
	 * Select any hex (regardless of ownership) and roll two dice.
	 * If the roll is 6,7,8, the disaster happens in this hex.
	 * Otherwise, select a different hex and roll.
	 * Carry on this way until either a) A 6,7,8 is rolled or b) All owned hexes have been selected and no 6,7,8 has been rolled.
	 *
	 * If a disaster does occur, the player who owns the hex rolls a single die. If the roll is 1,6 nothing happens.
	 * If the roll is 2-5, the player loses the rolled amount of counters from the hex. The number of losses must satisfy the number rolled (this
	 * means reducing forts/villages/cities, but not citadels).
	 */
	public void performAbility() {
		
	}
}
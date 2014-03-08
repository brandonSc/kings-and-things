package KAT;

public class WillingWorkers extends RandomEvent {

	public WillingWorkers() {
		super("Images/Event_WillingWorkers.png", "Images/Creature_Back.png", "Willing Workers");
		owner = null;
		setDescription("You can build a tower or upgrade a fort for free! See the Leaflet for the in-depth rules.");
	}

	/*
	 * Place a tower in any hex you own that doesn't already contain a fort, or increase the level of an existing fort by one.
	 *
	 * A citadel may NOT be created with this event even if all of the citadel requirements are met.
	 */
	public void performAbility() {
		Terrain t = ClickObserver.getInstance().getClickedTerrain();
		ArrayList<Terrain> hexes = owner.getHexesOwned();

		for (Terrain h : hexes) {
			if (t.compareTo(h) == 0) {
				owner.constructFort(t);
				t.setFortImage();
			}
		}
	}
}
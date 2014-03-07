package KAT;

public class GoodHarvest extends RandomEvent {
	private Player owner;

	public GoodHarvest() {
		super("Images/Event_GoodHarvest.png", "Images/Creature_Back.png", "Good Harvest");
		owner = null;
		setDescription("You may collect gold as if this were the Gold Collection Phase! See the Leaflet for the in-depth rules.");
	}

	public void setOwner(Player p) { owner = p; }


	/*
	 * The owner of this event can collect gold as if this were the gold collection phase.
	 * Do not collect gold for special income counters (including cities and villages).
	 */
	public void performAbility() {
		int income = 0;

        income += owner.getHexesOwned().size(); 
        
        for( Terrain hex : owner.getHexesWithPiece() ){
        	if (hex.getContents(owner.getName()) != null) {
	            for( Piece p : hex.getContents(owner.getName()).getStack() ){
	                if( p instanceof Fort ){
	                    income += ((Fort)(p)).getCombatValue();
	                } else if( p instanceof SpecialCharacter ){
	                    income += 1;
	                }
	            }
        	}
        }
        owner.addGold(income);
	}
}
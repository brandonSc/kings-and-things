package KAT;

public class Defection extends RandomEvent {
	private Player owner;

	public Defection() {
		super("Images/Event_Defection.png", "Images/Creature_Back.png", "Defection");
		owner = null;
		setDescription("You may immediately add a special character to your forces! See the Leaflet for the in-depth rules.");
	}

	public void setOwner(Player p) { owner = p; }
	public Player getOwner() { return owner; }


	/*
	 * Decide which special character you want to control (can already be on the board under control
	 * of another player, or can be in the bank).
	 *
	 * The player wanting the special character rolls two dice against another player. If his/her roll is higher,
	 * the special character is immediately moved to any hex under his/her control.
	 *
	 * Gold may not be used to modify this roll.
	 */
	public void performAbility() {
		
	}
}
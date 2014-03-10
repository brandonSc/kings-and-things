package KAT;

public class Defection extends RandomEvent {

	public Defection() {
		super("Images/Event_Defection.png", "Images/Creature_Back.png", "Defection");
		setOwner(null);
		setDescription("You may immediately add a special character to your forces!\nSee the Leaflet for the in-depth rules.");
	}

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
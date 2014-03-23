package KAT;

public class Firewall extends MagicEvent {
	public Firewall() {
		super("Images/Magic_Firewall.png", "Images/Creature_Back.png", "Firewall");
		setOwner(null);
		setDescription("Summon a magic fort to fight for you!\nSee the leaflet for more info");
	}

	/*
	 * Playable at the start of your battle (whether attacking or defending)
	 */
	@Override
	public boolean isPlayable() {
		if (GameLoop.getInstance().getPhase() == 6)
			return true;
		return false;
	}

	/*
	 * The owner rolls one die. This value will be the combat value of the Firewall.
	 * The Firewall fights during the magic combat step.
	 *
	 * Place the Firewall in any hex where you are involved in combat.
	 *
	 * The Firewall is returned to the cup once it is eliminated, or at the end of the battle.
	 */
	public void performAbility() {
		if (TheCup.getInstance().getRemaining().size() != 0)
			TheCup.getInstance().addToCup(this);
	}
}
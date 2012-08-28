package aor.spells;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * A SpellCastEvent is called every time a spell is cast. It can be cancelled.
 * @author Jay
 */
final class SpellCastEvent extends Event implements Cancellable{
	public enum ReasonCancelled{NOT_CANCELLED, REQUIREMENTS_NOT_MET, CANCELLED_BY_SPELL, COOLDOWN}
	private static final HandlerList handlers = new HandlerList();
	private final Spell spell;
	private final Player player;
	private ReasonCancelled reasonCancelled=ReasonCancelled.NOT_CANCELLED;
	public SpellCastEvent(final Spell spell,final Player player){
		this.spell=spell;
		this.player=player;
	}
	/**
	 * @return String - the name of the spell
	 */
	public String getSpellName(){
		return spell.getName();
	}
	Spell getSpell(){
		return spell;
	}
	/**
	 * @return Player - the player who tried to cast the spell
	 */
	public Player getPlayer(){
		return player;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList() {
		return handlers;
	}
	/**
	 * @return ReasonCancelled - the reason the spell was cancelled.
	 */
	public ReasonCancelled getReasonCancelled(){
		return reasonCancelled;
	}
	/**
	 * returns boolean - whether or not the spell being cast was cancelled
	 */
	@Override
	public boolean isCancelled() {
		return !reasonCancelled.equals(ReasonCancelled.NOT_CANCELLED);
	}
	/**
	 * This method allows you to cancel a spell being cast. You may not uncancel the event and the boolean does nothing.
	 */
	@Override
	public void setCancelled(boolean cancel) {
		final Class<?> callerClass=Thread.currentThread().getStackTrace()[0].getClass();
		if(callerClass.equals(Spells.class)){
			if(cancel)reasonCancelled=ReasonCancelled.REQUIREMENTS_NOT_MET;
			else reasonCancelled=ReasonCancelled.COOLDOWN;
		}
		else reasonCancelled=ReasonCancelled.CANCELLED_BY_SPELL;
	}
}

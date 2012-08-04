package aor.spells;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpellCastEvent extends Event implements Cancellable{
	public enum ReasonCancelled{NOT_CANCELLED, REQUIREMENTS_NOT_MET, CANCELLED_BY_SPELL}
	private static final HandlerList handlers = new HandlerList();
	private final Spell spell;
	private final Player player;
	private ReasonCancelled reasonCancelled=ReasonCancelled.NOT_CANCELLED;
	public SpellCastEvent(final Spell spell,final Player player){
		this.spell=spell;
		this.player=player;
	}
	public String getSpellName(){
		return spell.getName();
	}
	Spell getSpell(){
		return spell;
	}
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
	public ReasonCancelled getReasonCancelled(){
		return reasonCancelled;
	}
	@Override
	public boolean isCancelled() {
		return !reasonCancelled.equals(ReasonCancelled.NOT_CANCELLED);
	}
	@Override
	public void setCancelled(boolean cancel) {
		final Class<?> callerClass=Thread.currentThread().getStackTrace()[0].getClass();
		if(callerClass.equals(Spells.class))reasonCancelled=ReasonCancelled.REQUIREMENTS_NOT_MET;
		else reasonCancelled=ReasonCancelled.CANCELLED_BY_SPELL;
	}
}

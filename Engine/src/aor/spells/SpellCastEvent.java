package aor.spells;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpellCastEvent extends Event implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	private final Spell spell;
	private final Player player;
	private boolean cancelled=false;
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
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	@Override
	public void setCancelled(boolean cancel) {
		cancelled|=cancel;
	}
}

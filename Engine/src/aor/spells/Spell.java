package aor.spells;

import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class Spell implements Listener {
	public abstract String getName();
	public abstract String getDescription();	
	public abstract void cast(Player player);
	public String getGroup(){
		return "";
	}
	public boolean checkRequirements(Player player){
		return true;
	}
	public void removeRequirements(Player player){}
	public void schedule(int ticks,Method m,Object... args){
		Scheduler.schedule(ticks, this, m, args);
	}
	public void schedule(int ticks,Object... args){
		Scheduler.schedule(ticks, this, null, args);
	}
	public void run(Object... args){}
	public int getCooldown(){
		return 0;
	}
}
package aor.spells;

import java.lang.reflect.Method;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public abstract class Spell implements Listener {
	private static final Runner runner=Spells.runner;
	public abstract String getName();
	public abstract String getDescription();	
	public abstract void cast(Player player);
	public boolean checkRequirements(Player player){
		return true;
	}
	public void removeRequirements(Player player){}
	public static boolean inInventory(Player player, Iterable<ItemStack> items){
		return inInventory(player.getInventory(),items);
	}
	public static boolean inInventory(PlayerInventory inventory, Iterable<ItemStack> items){
		for(ItemStack item:items){
			if(!inventory.contains(item))return false;
		}
		return true;
	}
	public static void removeFromInventory(Player player, Iterable<ItemStack> items){
		removeFromInventory(player.getInventory(),items);
	}
	public static void removeFromInventory(PlayerInventory inventory, Iterable<ItemStack> items){
		for(ItemStack item:items){
			inventory.remove(item);
		}
	}
	public void schedule(int millis,Method m,Object... args){
		runner.schedule(millis, this, m, args);
	}
	public void schedule(int millis,Object... args){
		runner.schedule(millis, this, null, args);
	}
	public void run(Object... args){}
}
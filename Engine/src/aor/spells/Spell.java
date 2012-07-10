package aor.spells;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public abstract class Spell implements Listener {
	public abstract String getName();
	public abstract String getDescription();	
	public abstract void cast(Player player);
	public boolean checkRequirements(Player player){
		return true;
	}
	public void removeRequirements(Player player){}
	public static boolean inInventory(Player player, ArrayList<ItemStack> items){
		return inInventory(player.getInventory(),items);
	}
	public static boolean inInventory(PlayerInventory inventory, ArrayList<ItemStack> items){
		for(ItemStack item:items){
			if(!inventory.contains(item))return false;
		}
		return true;
	}
	public static void removeFromInventory(Player player, ArrayList<ItemStack> items){
		removeFromInventory(player.getInventory(),items);
	}
	public static void removeFromInventory(PlayerInventory inventory, ArrayList<ItemStack> items){
		for(ItemStack item:items){
			inventory.remove(item);
		}
	}
}
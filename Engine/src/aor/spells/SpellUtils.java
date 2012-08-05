package aor.spells;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class SpellUtils {
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
}
import java.lang.reflect.Method;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import aor.spells.Spell;
import static aor.spells.SpellUtils.inInventory;
import static aor.spells.SpellUtils.removeFromInventory;



public class Invisibility extends Spell {
		private Method undo;
		public Invisibility(){
			try {
				undo=Invisibility.class.getMethod("undo", Player.class);
			} catch (Exception e) {assert false:"Who broke it???";}
		}
		@Override
		public String getName() {
			return "Invisibility";
		}
		@Override
		public String getDescription() {
			return "Makes you temporarily invisible to other players";
		}
		@Override
		public void cast(Player player) {
			for (Player players : Bukkit.getOnlinePlayers()){
                players.hidePlayer(player);
            }
			schedule(600, undo, player);
		}
		@Override
		public boolean checkRequirements(Player player) {
			return inInventory(player,Arrays.asList(new ItemStack[]{new ItemStack(Material.BONE, 1)}));
		}
		@Override
		public void removeRequirements(Player player) {
			removeFromInventory(player,Arrays.asList(new ItemStack[]{new ItemStack(Material.BONE, 1)}));
		}
		public void undo(Player player){
			for (Player players : Bukkit.getOnlinePlayers()) {
                players.showPlayer(player);
            }
		}
	}

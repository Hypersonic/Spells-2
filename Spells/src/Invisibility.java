import static aor.spells.SpellUtils.inInventory;

import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import aor.spells.Spell;



public class Invisibility extends Spell {
		private final Method undo=getMethod("undo",Player.class);
		public Invisibility(){}
		@Override
		public String getName() {
			return "Invisibility";
		}
		@Override
		public String getDescription() {
			return "Makes you temporarily invisible to other players";
		}
        @Override
        public String getRequirements() {
            return "Nobody even knows what the hell you need, it won't work.";
        }
		@Override
		public void cast(Player player) {
			for (Player players : Bukkit.getOnlinePlayers()){
                if(!players.getName().equals(player.getName()))players.hidePlayer(player);
            }
			schedule(600, undo, player);
		}
		@Override
		public boolean checkRequirements(Player player) {
			return inInventory(player,new ItemStack(Material.BONE, 1));
		}
		@Override
		public void removeRequirements(Player player) {
			player.getInventory().removeItem(new ItemStack(Material.BONE, 1));
		}
		public void undo(Player player){
			for (Player players : Bukkit.getOnlinePlayers()) {
                players.showPlayer(player);
            }
		}
	}

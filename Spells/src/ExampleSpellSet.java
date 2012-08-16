import static aor.spells.SpellUtils.inInventory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import aor.spells.Spell;
import aor.spells.SpellSet;

public class ExampleSpellSet extends SpellSet{
	public class ExampleSpell extends Spell {
		private Method sendMessage;
		public ExampleSpell(){
			try {
				sendMessage=ExampleSpell.class.getMethod("sendMessage", Player.class);
			} catch (Exception e) {assert false:"Who broke it???";}
		}
		@Override
		public String getName() {
			return "Example Spell";
		}
		@Override
		public String getDescription() {
			return "";
		}
		@Override
		public void cast(Player player) {
			schedule(10000, sendMessage, player);
		}
		@Override
		public boolean checkRequirements(Player player) {
			return inInventory(player,Arrays.asList(new ItemStack[]{new ItemStack(Material.BONE, 1)}));
		}
		@Override
		public void removeRequirements(Player player) {
			player.getInventory().removeItem(new ItemStack[]{new ItemStack(Material.BONE, 1)});
		}
		public void sendMessage(Player player){
			player.sendMessage(""+ChatColor.LIGHT_PURPLE+ChatColor.BOLD+"You cast ExampleSpell 10 seconds ago!");
		}
	}

	@Override
	public ArrayList<Spell> getSpells() {
		ArrayList<Spell> result=new ArrayList<Spell>();
		result.add(new ExampleSpell());
		return result;
	}
}

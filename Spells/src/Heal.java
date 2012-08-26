import static aor.spells.SpellUtils.getPlayerTarget;
import static aor.spells.SpellUtils.inInventory;
import static aor.spells.SpellUtils.removeFromInventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import aor.spells.Spell;

/**
 * Heals target player
 */
public class Heal extends Spell {

	public Heal(){	
	}

	@Override
	public String getName() {
		return "Heal"; 
	}
	//@Override
	//public String getGroup(){
		//return "";
	//}
	@Override
	public String getDescription() {
		return "Gives 3 seconds of powerful regeneration to target player.";
	}
    @Override
    public String getRequirements() {
        return "You need: 1 Glowstone Dust and 4 Redstone Dust.";
    }
	@Override
	public boolean checkRequirements(Player player){
		if ( !inInventory(player,new ItemStack(Material.REDSTONE, 4),new ItemStack(Material.GLOWSTONE_DUST, 1)))return false;
        Player target = getPlayerTarget(player);
        if (target == null) {
            player.sendMessage("Point at someone, you numbskull!");
            return false;
        }
        return true;
	}
    @Override
    public void removeRequirements(Player player) {
        removeFromInventory(player,new ItemStack(Material.REDSTONE, 4),new ItemStack(Material.GLOWSTONE_DUST, 1));
    }
    @Override
    public int getCooldown() {
        return 1;
    }
	@Override
	public void cast(Player player) {
        Player target = getPlayerTarget(player);
        target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1));
    }
}

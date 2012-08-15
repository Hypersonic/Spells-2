import static aor.spells.SpellUtils.inInventory;
import static aor.spells.SpellUtils.removeFromInventory;
import static aor.spells.SpellUtils.getPlayerTarget;

import java.util.Arrays;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.inventory.ItemStack;

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
	public boolean checkRequirements(Player player){
        
		if ( !inInventory(player,Arrays.asList(new ItemStack[]{
            new ItemStack(Material.REDSTONE_WIRE, 4),
            new ItemStack(Material.GLOWSTONE_DUST, 1)
        }))) { return false; }
        Player target = getPlayerTarget(player);
        if (target == null) {
            player.sendMessage("Point at someone, you numbskull!");
            return false;
        } else {
            return true;
        }
	}
    @Override
    public void removeRequirements(Player player) {
        
    }
    @Override
    public int getCooldown() {
        return 1;
    }
	@Override
	public void cast(Player player) {
        Player target = getPlayerTarget(player);
        player.sendMessage("Good work, you targetted something");
        player.sendMessage("Their old health was: " + target.getHealth());
        target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1));
        player.sendMessage("Their new health is: " + target.getHealth());
        target.sendMessage("Tada, healification!");
    }
}

import static aor.spells.SpellUtils.inInventory;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.ItemStack;

import aor.spells.Spell;

/**
 * Causes mobs around the player to turn into sand blocks.
 * @author Yulli
 */
public class Petrify extends Spell {

	public Petrify(){
		
	}
	@Override
	public String getName() {
		return "Petrify"; 
	}
	
	@Override
	public String getDescription() {
		return "Causes mobs around the player to turn into sand blocks.";
	}
    @Override
    public String getRequirements() {
        return "You need: 8 Redstone Dust.";
    }
    
    @Override
    public boolean checkRequirements(Player player) {
        return inInventory(player,Arrays.asList(new ItemStack[]{
            new ItemStack(Material.REDSTONE, 8)
        }));
    }

    @Override
    public void removeRequirements(Player player) {
        player.getInventory().removeItem(new ItemStack[]{
            new ItemStack(Material.REDSTONE, 8)
        });
    }

	@Override
	public void cast(Player player) {
		int petrifySize;
		if (player.getLevel() >= 100) {
			petrifySize = 30;
			player.sendMessage("A wave of power erupts from your body, freezing all who would dare approach you...");
		} else if (player.getLevel() >= 30) {
			petrifySize = 10;
			player.sendMessage("As the power of death rises through your body, you focus, channelling it forwards...");
		} else{
			petrifySize = 5;
			player.sendMessage("Energy flows through you, expanding into an orb around your body...");
		}
		for (Entity target : player.getNearbyEntities(petrifySize/2, petrifySize/2, petrifySize/2)) {
			if (target instanceof Slime || target instanceof Creature && !(target instanceof Tameable && ((Tameable)target).isTamed()) && !(target instanceof EnderDragon)) {
                Location entityLocation = target.getLocation();
                target.remove();
                player.getWorld().getBlockAt(entityLocation).setType(Material.SAND);
            }
		}
	}
}

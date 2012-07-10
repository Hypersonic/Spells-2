import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.Location;

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
	public void cast(Player player) {
		int petrifySize = 5;
		if (player.getLevel() > 100) {
			petrifySize = 30;
			player.sendMessage("A wave of power erupts from your body, freezing all who would dare approach you...");
		} else if (player.getLevel() > 30) {
			petrifySize = 10;
			player.sendMessage("As the power of death rises through your body, you focus, channelling it forwards...");
		}
        for (Entity target : player.getNearbyEntities(petrifySize/2, petrifySize/2, petrifySize/2)) {
            if (target instanceof LivingEntity) {
                Location entityLocation = target.getLocation();
                target.remove();
                player.getWorld().getBlockAt(entityLocation).setType(Material.SAND);
            }
        }
	}
}

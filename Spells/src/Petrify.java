import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.Location;

import aor.spells.Spell;

/**
 * Causes mobs around the player to turn into stone pillars.
 * @author Yulli
 */
public class Petrify extends Spell {

	private static final int MAXDISTANCE = 200; // Sets the maximum distance.

	public Petrify(){
		
	}
	@Override
	public String getName() {
		return "Petrify"; 
	}
	
	@Override
	public String getDescription() {
		return "Causes mobs around the player to turn into stone pillars.";
	}

	@Override
	public void cast(Player player) {
		int petrifySize = 5;
		if (player.getLevel() > 100) {
			petrifySize = 30;
			player.sendMessage("You feel the great arcane energies of your ancestors build up inside of you...");
		} else if (player.getLevel() > 30) {
			petrifySize = 10;
			player.sendMessage("You feel a power building within you, ready to explode, and focus your mind...");
		} else {
			player.sendMessage("You feel a power awaken within you, and you prepare to strike...");
		}
        for (Entity target : player.getNearbyEntities(petrifySize/2, petrifySize/2, petrifySize/2)) {
            Location jythionAPukkit = target.getLocation();
            target.remove();
            player.getWorld().getBlockAt(jythionAPukkit).setType(Material.STONE);
            player.getWorld().getBlockAt(jythionAPukkit).getRelative(0, -1, 0).setType(Material.STONE);
        }
	}
}

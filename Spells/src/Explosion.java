
import aor.spells.Spell;

import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.Material;
/**
 * Causes an explosion at the block the player is pointing at.
 */
public class Explosion extends Spell {

    private static final int MAXDISTANCE = 200; // Sets the maximum distance.

    public Explosion(){
        
    }

    @Override
    public String getName() {
        return "Explosion"; 
    }
    
    @Override
    public String getDescription() {
        return "Creates an explosion at the block the player is looking at";
    }

    @Override
    public void cast(Player player) {
        Block targetBlock = player.getTargetBlock(null, MAXDISTANCE);
        if (targetBlock.getType() != Material.AIR) {
            int explosionSize = 5;
            if (player.getLevel() > 100) {
                explosionSize = 30;
                player.sendMessage("You feel the great arcane energies of your ancestors build up inside of you...");
            } else if (player.getLevel() > 30) {
                explosionSize = 10;
                player.sendMessage("You feel a power building within you, ready to explode, and focus on your target...");
            } else {
                player.sendMessage("You feel a power awaken within you, and you prepare to strike...");
            }

            targetBlock.getWorld().createExplosion(targetBlock.getLocation().getX(), targetBlock.getLocation().getY(), targetBlock.getLocation().getZ(), explosionSize, false);

        } else {
            player.sendMessage("Could not cast, you're not pointing at anything!");
        }
    }
}

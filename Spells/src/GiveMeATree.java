import aor.spells.Spell;

import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.TreeType;
import org.bukkit.Material;
import org.bukkit.World;

/**
 * Grows a tree where the player is pointing
 * @author Yulli
 */
public class GiveMeATree extends Spell {

	private static final int MAXDISTANCE = 10;

	public GiveMeATree(){
		
	}
	@Override
	public String getName() {
		return "GiveMeATree"; 
	}
	
	@Override
	public String getDescription() {
		return "Grows a tree where the player is pointing";
	}
	@Override
	public boolean checkRequirements(Player player){
		return true;
	}
	@Override
	public void cast(Player player) {
        final Block target = player.getTargetBlock(null, MAXDISTANCE);
        final Block above = target.getRelative(BlockFace.UP);
        if ((target.getType().equals(Material.GRASS) || target.getType().equals(Material.DIRT)) && above.getType().equals(Material.AIR)) {
            target.getWorld().generateTree(above.getLocation(), TreeType.TREE);
        }
    }
}

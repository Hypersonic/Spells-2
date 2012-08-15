
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import aor.spells.Spell;

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
		final Block target = player.getTargetBlock(null, MAXDISTANCE);
        if (target.getType() == Material.GRASS || target.getType() == Material.DIRT) {
            return true;
        } else {
            return false;
        }
	}
	
	@Override
	public void cast(Player player) {
        final Block target = player.getTargetBlock(null, MAXDISTANCE);
        final Block above = target.getRelative(BlockFace.UP);
        target.getWorld().generateTree(above.getLocation(), TreeType.TREE);
    }
}

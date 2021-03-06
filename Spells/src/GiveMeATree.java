import static aor.spells.SpellUtils.inInventory;
import static aor.spells.SpellUtils.removeFromInventory;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
    public String getRequirements() {
        return "You need: 1 Sapling and 2 Redstone Dust.";
    }
	@Override
	public boolean checkRequirements(Player player){
		if ( !inInventory(player,new ItemStack(Material.REDSTONE, 2),new ItemStack(Material.SAPLING, 1)))return false;
		final Block targetBlock = player.getTargetBlock(null, MAXDISTANCE);
        if (!(targetBlock.getType() == Material.GRASS || targetBlock.getType() == Material.DIRT))return true;
        return true;
    }
    @Override
    public void removeRequirements(Player player) {
        removeFromInventory(player,new ItemStack(Material.REDSTONE, 2),new ItemStack(Material.SAPLING, 1));
    }
	
	@Override
	public void cast(Player player) {
        final Block target = player.getTargetBlock(null, MAXDISTANCE);
        final Block above = target.getRelative(BlockFace.UP);
        target.getWorld().generateTree(above.getLocation(), TreeType.TREE);
    }
    
}

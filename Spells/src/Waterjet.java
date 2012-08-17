import static aor.spells.SpellUtils.inInventory;

import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import aor.spells.Spell;

/**
 * @author Yulli
 * Creates a line of water in the direction that the player is looking at for a few seconds
 */
public class Waterjet extends Spell {
	private final Method douse=getMethod("douse",Player.class);
	private static final ItemStack[] reqs={new ItemStack(Material.REDSTONE, 4),new ItemStack(Material.WATER_BUCKET, 1)};
	private static final int MAXDISTANCE = 16;
	public Waterjet(){}
	
    @Override
	public String getName() {
		return "Waterjet"; 
	}
	
	@Override
	public String getDescription() {
		return "Creates a line of water in the direction that the player is looking at for a few seconds.";
	}
    @Override
    public String getRequirements() {
        return "You need: 1 Bucket of Water and 4 Redstone Dust. You may not be in The Nether.";
    }
	
    @Override
    public void removeRequirements(Player player) {
        player.getInventory().removeItem(reqs);
    }
    @Override
	public boolean checkRequirements(Player player){
        if (!inInventory(player,reqs)){
        	return false;
        }
        
        if (player.getWorld().getEnvironment() == Environment.NETHER) {
            return false;
        }
        return true;
	}
	
    @Override
    public int getCooldown() {
        return 100; // 5 sec cooldown
    }


    @Override
	public void cast(Player player) {
	    for (int i = 0; i < 60; i++) { // active for 3 sec
            schedule(i, douse, player);
        }
    }

    public void douse(Player player){ 
        List<Block> seenBlocks = player.getLineOfSight(null, MAXDISTANCE);
        List<Block> douseBlocks = player.getLineOfSight(null, MAXDISTANCE);
        for (Block block : seenBlocks) {
            for (int i = 0; i < block.getLocation().distance(player.getLocation())/3; i++) {
                for (BlockFace face : BlockFace.values()){
                    douseBlocks.add(block.getRelative(face, i));
                }
            }
            
        }
        Location playerLoc = player.getLocation();
        for (Block block : douseBlocks) {
            Block target = block.getRelative(BlockFace.UP);
            if (block.getType() != Material.AIR &&
                    block.getLocation().distance(playerLoc) >= 2 &&
                    target.getType() == Material.AIR
                ) {
                target.setType(Material.WATER);
                target.setData((byte)(7));
            }
        }
    }
}

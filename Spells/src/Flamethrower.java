import aor.spells.Spell;

import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.Location;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Creates a line of fire in the direction that the player is looking at for a few seconds
 */
public class Flamethrower extends Spell {

	private static final int MAXDISTANCE = 16;
    private Method ignite;
	public Flamethrower(){
		try {
			ignite=Flamethrower.class.getMethod("ignite", Player.class);
		} catch (Exception e) {assert false:"Who broke it???";}
	}
	
    @Override
	public String getName() {
		return "Flamethrower"; 
	}
	
	@Override
	public String getDescription() {
		return "Creates a line of fire in the direction that the player is looking at for a few seconds";
	}
	
    @Override
	public boolean checkRequirements(Player player){
		return true;
	}
	
    @Override
	public void cast(Player player) {
	    for (int i = 0; i < 60; i++) {
            schedule(i, ignite, player);
        }
    }

    public void ignite(Player player){ 
        List<Block> seenBlocks = player.getLineOfSight(null, MAXDISTANCE);
        List<Block> burnBlocks = player.getLineOfSight(null, MAXDISTANCE);
        for (Block block : seenBlocks) {
            burnBlocks.add(block.getRelative(BlockFace.UP));
            burnBlocks.add(block.getRelative(BlockFace.DOWN));
            burnBlocks.add(block.getRelative(BlockFace.NORTH));
            burnBlocks.add(block.getRelative(BlockFace.EAST));
            burnBlocks.add(block.getRelative(BlockFace.SOUTH));
            burnBlocks.add(block.getRelative(BlockFace.WEST));
        }
        Location playerLoc = player.getLocation();
        for (Block block : burnBlocks) {
            if (block.getType() == Material.AIR &&
                    block.getLocation().distance(playerLoc) > 1.5 
                ) {
                block.setType(Material.FIRE);
            }
        }
    }
}

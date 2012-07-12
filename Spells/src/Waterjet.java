import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.Location;

import aor.spells.Spell;

/**
 * @author Yulli
 * Creates a line of water in the direction that the player is looking at for a few seconds
 */
public class Waterjet extends Spell {

	private static final int MAXDISTANCE = 16;
    private Method douse;
	public Waterjet(){
		try {
			douse = Waterjet.class.getMethod("douse", Player.class);
		} catch (Exception e) {assert false:"Who broke it???";}
	}
	
    @Override
	public String getName() {
		return "Waterjet"; 
	}
	
	@Override
	public String getDescription() {
		return "Creates a line of water in the direction that the player is looking at for a few seconds";
	}
	
    @Override
	public boolean checkRequirements(Player player){
		return true;
	}
	
    @Override
	public void cast(Player player) {
	    for (int i = 0; i < 60; i++) {
            schedule(i, douse, player);
        }
    }

    public void ignite(Player player){ 
        List<Block> seenBlocks = player.getLineOfSight(null, MAXDISTANCE);
        List<Block> douseBlocks = player.getLineOfSight(null, MAXDISTANCE);
        for (Block block : seenBlocks) {
            for (int i = 0; i < block.getLocation().distance(player.getLocation())/3; i++) {
                douseBlocks.add(block.getRelative(BlockFace.UP, i));
                douseBlocks.add(block.getRelative(BlockFace.DOWN, i));
                douseBlocks.add(block.getRelative(BlockFace.NORTH, i));
                douseBlocks.add(block.getRelative(BlockFace.EAST, i));
                douseBlocks.add(block.getRelative(BlockFace.SOUTH, i));
                douseBlocks.add(block.getRelative(BlockFace.NORTH_EAST, i));
                douseBlocks.add(block.getRelative(BlockFace.NORTH_WEST, i));
                douseBlocks.add(block.getRelative(BlockFace.SOUTH_EAST, i));
                douseBlocks.add(block.getRelative(BlockFace.SOUTH_WEST, i));
            }
            
        }
        Location playerLoc = player.getLocation();
        for (Block block : douseBlocks) {
            if (block.getType() == Material.AIR &&
                    block.getLocation().distance(playerLoc) > 2 
                ) {
                block.setType(Material.WATER);
            }
        }
    }
}

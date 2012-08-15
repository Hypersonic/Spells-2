import static aor.spells.SpellUtils.inInventory;
import static aor.spells.SpellUtils.removeFromInventory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Arrays;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.inventory.ItemStack;

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
		} catch (Exception e) {}
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
    public void removeRequirements(Player player) {
        removeFromInventory(player,Arrays.asList(new ItemStack[]{
            new ItemStack(Material.REDSTONE, 4),
            new ItemStack(Material.WATER_BUCKET, 1)
        }));
    }
    @Override
	public boolean checkRequirements(Player player){

        if ( !inInventory(player,Arrays.asList(new ItemStack[]{
            new ItemStack(Material.REDSTONE, 4),
            new ItemStack(Material.WATER_BUCKET, 1)
        }))) { return false; }
        
        if (player.getWorld().getEnvironment() == Environment.NETHER) {
            player.sendMessage("You may not cast this spell in the Nether!");
            return false;
        } else {
            return true;
        }
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

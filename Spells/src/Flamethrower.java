import static aor.spells.SpellUtils.inInventory;
import static aor.spells.SpellUtils.removeFromInventory;

import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import aor.spells.Spell;

/**
 * Creates a line of fire in the direction that the player is looking at for a few seconds
 */
public class Flamethrower extends Spell {

	private static final int MAXDISTANCE = 12;
    private static final double SPREADMULTIPLIER = 3.5; // Multiplier for the distance to spread as it gets farther from the player
    private final Method ignite=getMethod("ignite",Player.class);
	
    
    public Flamethrower(){}
	
    @Override
	public String getName() {
		return "Flamethrower"; 
	}
	
	@Override
	public String getDescription() {
		return "Creates a line of fire in the direction that the player is looking at for a few seconds";
	}
	
    @Override
    public String getRequirements() {
        return "You need: 2 Blaze Powder and 4 Redstone Dust.";
    }
    @Override
	public boolean checkRequirements(Player player){
		return inInventory(player,new ItemStack(Material.REDSTONE, 4),new ItemStack(Material.BLAZE_POWDER, 2));
	}

    @Override
    public void removeRequirements(Player player) {
			removeFromInventory(player,new ItemStack(Material.REDSTONE, 4),new ItemStack(Material.BLAZE_POWDER, 2));
        
    }
	
    @Override
	public void cast(Player player) {
	    for (int i = 0; i < 40; i++) {
            schedule(i, ignite, player);
        }
    }

    public void ignite(Player player){ 
        List<Block> seenBlocks = player.getLineOfSight(null, MAXDISTANCE);
        List<Block> burnBlocks = player.getLineOfSight(null, MAXDISTANCE);
        for (Block block : seenBlocks) {
            for (int i = 0; i < block.getLocation().distance(player.getLocation())/SPREADMULTIPLIER; i++) {
                for (BlockFace face : BlockFace.values()){
                    burnBlocks.add(block.getRelative(face, i));
                }
            }
            
        }
        Location playerLoc = player.getLocation();
        for (Block block : burnBlocks) {
            if (block.getType() == Material.AIR &&
                    block.getLocation().distance(playerLoc) > 2 &&
                    !(block.getLocation().distance(playerLoc) > MAXDISTANCE)
                ) {
                block.setType(Material.FIRE);
            }
        }
    }
}

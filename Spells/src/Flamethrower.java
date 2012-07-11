import aor.spells.Spell;

import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.Material;

import java.util.List;

/**
 * Creates a line of fire in the direction that the player is looking at for a few seconds
 */
public class Flamethrower extends Spell {

	private static final int MAXDISTANCE = 12;

	public Flamethrower(){
		
	}
	@Override
	public String getName() {
		return "Flamethrower"; 
	}
	
	@Override
	public String getDescription() {
		return "Creates a line of fire in the direction that the player is looking at, for a few seconds";
	}
	@Override
	public boolean checkRequirements(Player player){
		return true;
	}
	@Override
	public void cast(Player player) {
	
        List<Block> seenBlocks = player.getLineOfSight(null, MAXDISTANCE);
        for (Block block : seenBlocks) {
            if (block.getType() == Material.AIR) {
                block.setType(Material.FIRE);
            }
        }

    }
}

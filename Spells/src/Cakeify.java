import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.Material;

import aor.spells.Spell;
/**
 * Creates a cake where the player is looking at (YUM!)
 */
public class Cakeify extends Spell {

	private static final int MAXDISTANCE = 10;

	public Cakeify(){
		
	}
	@Override
	public String getName() {
		return "Cakeify"; 
	}
	
	@Override
	public String getDescription() {
		return "Creates a cake where the player is looking at (YUM!)";
	}
	@Override
	public boolean checkRequirements(Player player){
		if (player.getTargetBlock(null, MAXDISTANCE).getType() == Material.AIR) {
            player.sendMessage("Nothing in range!");
        } else return true;
	}
	@Override
	public void cast(Player player) {
        final Block target = player.getTargetBlock(null, MAXDISTANCE);
        if (target.getType() != Material.BEDROCK) {
            target.setType(Material.CAKE_BLOCK);
        }
    }
}

import aor.spells.Spell;

import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.Material;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Creates a line of fire in the direction that the player is looking at for a few seconds
 */
public class Flamethrower extends Spell {

	private static final int MAXDISTANCE = 12;
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
		return "Creates a line of fire in the direction that the player is looking at, for a few seconds";
	}
	
    @Override
	public boolean checkRequirements(Player player){
		return true;
	}
	
    @Override
	public void cast(Player player) {
	    for (int i = 0; i < 3000; i++) {
            schedule(i, ignite, player);
        }
    }

    public void ignite(Player player){ 
        List<Block> seenBlocks = player.getLineOfSight(null, MAXDISTANCE);
        for (Block block : seenBlocks) {
            if (block.getType() == Material.AIR) {
                block.setType(Material.FIRE);
            }
        }
    }
}

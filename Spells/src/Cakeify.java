import static aor.spells.SpellUtils.inInventory;
import static aor.spells.SpellUtils.removeFromInventory;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
    public String getRequirements() {
        return "You need: 3 Wheat, 2 Sugar, 1 Bucket of Milk, 1 Egg, and 4 Redstone Dust.";
    }

	@Override
	public boolean checkRequirements(Player player){

        if(!inInventory(player,
            new ItemStack(Material.WHEAT, 3),
            new ItemStack(Material.SUGAR, 2),
            new ItemStack(Material.MILK_BUCKET, 1),
            new ItemStack(Material.EGG, 1),
            new ItemStack(Material.REDSTONE, 4)
        ))return false;
        
        if (player.getTargetBlock(null, MAXDISTANCE).getType() == Material.AIR) {
            player.sendMessage("Nothing in range!");
            return false;
        } else if (player.getTargetBlock(null, MAXDISTANCE).getType() == Material.BEDROCK) {
        	player.sendMessage("You cannot target bedrock!");
            return false;
        }
        return true;
	}
	
    @Override
    public void removeRequirements(Player player) {
        removeFromInventory(player,
                new ItemStack(Material.WHEAT, 3),
                new ItemStack(Material.SUGAR, 2),
                new ItemStack(Material.MILK_BUCKET, 1),
                new ItemStack(Material.EGG, 1),
                new ItemStack(Material.REDSTONE, 4));
    
    }
    @Override
	public void cast(Player player) {
        final Block target = player.getTargetBlock(null, MAXDISTANCE);
        target.setType(Material.CAKE_BLOCK);
    }
}

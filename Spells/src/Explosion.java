import static aor.spells.SpellUtils.inInventory;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import aor.spells.Spell;

/**
 * Causes an explosion at the block the player is pointing at.
 */
public class Explosion extends Spell {
	private static final int MAXDISTANCE = 200;

	public Explosion(){	
	}
	@Override
	public String getName() {
		return "Explosion"; 
	}
	@Override
	public String getGroup(){
		return "";
	}
	@Override
	public String getDescription() {
		return "Creates an explosion at the block the player is looking at";
	}
    @Override
    public String getRequirements() {
        return "You need: 2 Sulphur, 10 Sand, and 8 Redstone Dust.";
    }
	@Override
	public boolean checkRequirements(Player player){
        if (!inInventory(player,new ItemStack(Material.REDSTONE, 8),new ItemStack(Material.SULPHUR, 2),new ItemStack(Material.SAND, 10))){
            return false;
        }
        if (player.getTargetBlock(null, MAXDISTANCE).getType().equals(Material.AIR)){
			player.sendMessage("Could not cast, you're not pointing at anything!");
			return false;
		}
		else return true;
	}
    @Override
    public void removeRequirements(Player player) {
			player.getInventory().removeItem(new ItemStack(Material.REDSTONE, 8),new ItemStack(Material.SULPHUR, 2),new ItemStack(Material.SAND, 10));
    }
	@Override
	public void cast(Player player) {
		final Block targetBlock=player.getTargetBlock(null, MAXDISTANCE);
		int explosionSize;
		if (player.getLevel() >= 100) {
			explosionSize = 7;
			player.sendMessage("You feel the great arcane energies of your ancestors build up inside of you...");
		} else if (player.getLevel() >= 30) {
			explosionSize = 5;
			player.sendMessage("You feel a power building within you, ready to explode, and focus on your target...");
		} else {
			explosionSize = 2;
			player.sendMessage("You feel a power awaken within you, and you prepare to strike...");
		}
		targetBlock.getWorld().createExplosion(targetBlock.getLocation(), explosionSize, false);
	}
}

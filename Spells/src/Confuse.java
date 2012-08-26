import static aor.spells.SpellUtils.getPlayerTarget;
import static aor.spells.SpellUtils.inInventory;
import static aor.spells.SpellUtils.removeFromInventory;

import java.lang.reflect.Method;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import aor.spells.Spell;

/**
 * Confuses target player
 */
public class Confuse extends Spell {

    private final Method harass=getMethod("harass",Player.class);
    private final Method removeFake=getMethod("removeFake");
	
    public Confuse(){}

	@Override
	public String getName() {
		return "Confuse"; 
	}
	//@Override
	//public String getGroup(){
		//return "";
	//}
	@Override
	public String getDescription() {
		return "Target player begins to hallucinate for 20 seconds.";
	}
    @Override
    public String getRequirements() {
        return "You need: 1 Eye of Ender, 2 Spider Eyes, and 4 Redstone Dust.";
    }
	@Override
	public boolean checkRequirements(Player player){
        if( !inInventory(player,new ItemStack(Material.REDSTONE, 4),new ItemStack(Material.SPIDER_EYE, 2),new ItemStack(Material.EYE_OF_ENDER, 1)))return false;
        if (getPlayerTarget(player) == null) {
            player.sendMessage("Point at someone, you numbskull!");
            return false;
        }
        return true;
	}
    @Override
    public void removeRequirements(Player player) {
        removeFromInventory(player,new ItemStack(Material.REDSTONE, 4),new ItemStack(Material.SPIDER_EYE, 2),new ItemStack(Material.EYE_OF_ENDER, 1));
    }

    @Override
    public int getCooldown() {
        return 1;
    }
    public void harass(Player player) {
        int randomHeight = (int) (Math.random()  * -3);
        Block underBlock = player.getLocation().getBlock().getRelative(0, randomHeight, 0); // Block below the player*/
        
        int random1 = (int) (Math.random() * 10) - 5;
        int random2 = (int) (Math.random() * 5);
        int random3 = (int) (Math.random() * 10) - 5;
        Block changedBlock = player.getLocation().getBlock().getRelative(random1, random2, random3);
        player.sendBlockChange(changedBlock.getLocation(), underBlock.getType(), (byte) 0);
        
        schedule(60, removeFake, player, changedBlock);
        
        //Location newLoc = player.getLocation();
        //newLoc.setYaw(newLoc.getYaw() + 0.5f);
        //player.teleport(newLoc);
    }
    public void removeFake(Player player, Block block) {
        player.sendBlockChange(block.getLocation(), block.getType(), (byte) 0);
    }
	@Override
	public void cast(Player player) {
        Player target = getPlayerTarget(player);
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 500, 2));
        target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 500, 3));
        for (int i = 0; i < 350; i += 5) {
            schedule(i, harass, target);
        }
        //schedule(500, removeFakes, player);
    }
}

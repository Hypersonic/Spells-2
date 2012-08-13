import java.lang.Math;
import java.util.ArrayList;
import java.lang.reflect.Method;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Location;
import org.bukkit.block.Block;

import static aor.spells.SpellUtils.getPlayerTarget;

import aor.spells.Spell;

/**
 * Confuses target player
 */
public class Confuse extends Spell {

    private Method harass;
    private Method removeFake;
	
    private ArrayList<Block> changedBlocks = new ArrayList<Block>();

    public Confuse(){	
		try {
			harass=Confuse.class.getMethod("harass", Player.class);
			removeFake=Confuse.class.getMethod("removeFake", Player.class, Block.class);
		} catch (Exception e) {
            assert false:"Who broke it???";
        }
	}

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
		return "Gives 3 seconds of powerful regeneration to target player.";
	}
	@Override
	public boolean checkRequirements(Player player){
        if (getPlayerTarget(player) == null) {
            player.sendMessage("Point at someone, you numbskull!");
            return false;
        } else {
            return true;
        }
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
        changedBlocks.add(changedBlock);
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

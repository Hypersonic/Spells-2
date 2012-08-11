import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.Material;

import static aor.spells.SpellUtils.getPlayerTarget;

import aor.spells.Spell;

/**
 * Heals target player
 */
public class Heal extends Spell {

	public Heal(){	
	}

	@Override
	public String getName() {
		return "Heal"; 
	}
	@Override
	public String getGroup(){
		return "test";
	}
	@Override
	public String getDescription() {
		return "Heals target player";
	}
	@Override
	public boolean checkRequirements(Player player){
        return true;
	}

    @Override
    public int getCooldown() {
        return 1;
    }
	@Override
	public void cast(Player player) {
	try {
        Player target = getPlayerTarget(player);
        player.sendMessage("Good work, you targetted something");
        player.sendMessage("Their old health was: " + target.getHealth());
        target.setHealth(target.getHealth() + 1);
        player.sendMessage("Their new health is: " + target.getHealth());
        target.sendMessage("Tada, healification!");
    } catch (NullPointerException e) {
        player.sendMessage("Point at someone, you numbskull!");
    } catch (Exception e) {
    }
    }
}

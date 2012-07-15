
import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.block.Block;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;

import aor.spells.Spell;

public class Decoy extends Spell {
    private static final int MAXDISTANCE = 200;
    private static final int RADIUS = 10;

    public Decoy() {
    
    }

    @Override
    public String getName() {
        return "Decoy";
    }
    @Override
    public String getDescription() {
        return "Spawns a decoy mob that draws the attention of nearby mobs.";
    }
    @Override
    public boolean checkRequirements(Player player) {
        return true;    
    }
    @Override
    public void cast(Player player) {
        Location spawnLoc = player.getTargetBlock(null, MAXDISTANCE).getLocation().add(0,1,0); //Get the block the player is looking at, offset one up so we spawn the mob on top of it
        LivingEntity spawnedCow = player.getWorld().spawnCreature(spawnLoc, EntityType.COW);
        for (Entity nearbyEntity : spawnedCow.getNearbyEntities(RADIUS, RADIUS, RADIUS)) {
            if (nearbyEntity instanceof Creature) {
                ((Creature) nearbyEntity).setTarget(spawnedCow);
            }
        }
    }

}

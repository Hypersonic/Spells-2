
import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.block.Block;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
        Location spawnLoc = player.getTargetBlock(null, MAXDISTANCE).getLocation();
        Cow spawnedCow = player.getWorld().spawn(spawnLoc, Cow.class);
        for (Entity nearbyEntity : spawnedCow.getNearbyEntities(RADIUS, RADIUS, RADIUS)) {
            if (nearbyEntity instanceof Creature) {
                ((Creature) nearbyEntity).setTarget(spawnedCow);
            }
        }
    }



}

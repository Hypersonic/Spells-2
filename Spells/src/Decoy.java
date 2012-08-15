import static aor.spells.SpellUtils.inInventory;
import static aor.spells.SpellUtils.removeFromInventory;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
        return inInventory(player,Arrays.asList(new ItemStack[]{new ItemStack(Material.LEATHER, 2), new ItemStack(Material.REDSTONE, 2) }));
    }
    @Override
    public void removeRequirements(Player player) {
        removeFromInventory(player,Arrays.asList(new ItemStack[]{new ItemStack(Material.LEATHER, 2), new ItemStack(Material.REDSTONE, 2) }));
    }
    @Override
    public void cast(Player player) {
        Location spawnLoc = player.getTargetBlock(null, MAXDISTANCE).getLocation().add(0,1,0);//Get the block the player is looking at, offset one up so we spawn the mob on top of it
		LivingEntity spawnedCow = player.getWorld().spawn(spawnLoc, Cow.class);
        for (Entity nearbyEntity : spawnedCow.getNearbyEntities(RADIUS, RADIUS, RADIUS)) {
            if (nearbyEntity instanceof Creature) {
                ((Creature) nearbyEntity).setTarget(spawnedCow);
            }
        }
    }

}

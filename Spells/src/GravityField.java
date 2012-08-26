import static aor.spells.SpellUtils.inInventory;
import static aor.spells.SpellUtils.removeFromInventory;

import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import aor.spells.Spell;


public class GravityField extends Spell {

    private Method AttractEntities;
    
    private static final double RADIUS = 100;
    
    public GravityField() {
        try {
		    AttractEntities=GravityField.class.getMethod("AttractEntities", Player.class);
		} catch (Exception e) {
            assert false:"Who broke it???";
        }
    }

    public String getName() {
        return "Gravity Field";
    }
    
    public String getDescription() {//TODO should this say you instead of the caster?
        return "A sphere of gravity appears around the caster, and pulls nearby mobs and players towards them.";
    }

    @Override
    public String getRequirements() {
        return "You need: 16 Ender Pearls and 16 Redstone Dust.";
    }
    @Override
    public boolean checkRequirements(Player player) {
        return inInventory(player,new ItemStack(Material.REDSTONE, 16),new ItemStack(Material.ENDER_PEARL, 16));
    }

    @Override
    public void removeRequirements(Player player) {
        removeFromInventory(player,new ItemStack(Material.REDSTONE, 16), new ItemStack(Material.ENDER_PEARL, 16));
    }

    public void AttractEntities(Player player) {
        List<Entity> nearbyEntities = player.getNearbyEntities(RADIUS, RADIUS, RADIUS);
        for (Entity entity : nearbyEntities) {
            Vector relativeLoc = player.getLocation().subtract(entity.getLocation()).toVector().normalize();
            entity.setVelocity(entity.getVelocity().add(relativeLoc));
        
            Location entityLoc = entity.getLocation();
            entity.getWorld().playEffect(entityLoc, Effect.SMOKE, 0);
        }
    }

    public void cast(Player player) {
        for (int i = 0; i < 120; i += 5) {
            schedule(i, AttractEntities, player);
        }
    }
}

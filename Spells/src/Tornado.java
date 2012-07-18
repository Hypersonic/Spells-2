import java.util.List;
import java.lang.Math;
import java.lang.reflect.Method;

import org.bukkit.util.Vector;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.Location;

import aor.spells.Spell;


public class Tornado extends Spell {

    private Method cast;
    
    private static final double DILUTEAMOUNT = 10.0;
    public Tornado() {
        try {
			cast=Tornado.class.getMethod("cast", Player.class);
		} catch (Exception e) {
            assert false:"Who broke it???";
        }
    }
    public String getName() {
        return "Tornado";
    }
    
    public String getDescription() {
        return "A powerful tornado is summoned around the caster. It whips up mobs, blocks, and even other players in its tremendous power";
    }

    public void cast(Player player) {
        
        boolean castAgain = false;
        List<Entity> nearbyEntities = player.getNearbyEntities(10,50,10);
        for (Entity entity : nearbyEntities) {
            // Y is actually height in minecraft, so the value we'll be calling Y is really Z
            double playerX = player.getLocation().getX();
            double playerY = player.getLocation().getZ();
            double entityX = entity.getLocation().getX();
            double entityY = entity.getLocation().getZ();
            
            // if we increase the x value we're using by the difference in the y value... maybe
            double Ydiff = entityY - playerY;
            double Xdiff = entityX - playerX;

            double newX = Ydiff / DILUTEAMOUNT; // -1 * (Ydiff / 4);
            double newY = Xdiff / DILUTEAMOUNT; // -1 * (Xdiff / 4);//newX * ((-1 / slope) / 10);

            // and now create the vector and apply the force, hopefully.
            Vector force = new Vector(newX, 0.5, newY);

            //entity.setVelocity(entity.getVelocity().add(force));
            entity.setVelocity(force);

            entity.getLocation().getBlock().getRelative(0,-1,0).setType(Material.DIAMOND_BLOCK);
            
            player.sendMessage(force.toString());
            castAgain = true;
        }
        if (castAgain) {
            schedule(5, cast, player);
        }
    }
}

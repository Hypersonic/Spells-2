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
    private static final double RADIUS = 0.5;
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
        /*
            double newX = Ydiff / DILUTEAMOUNT; // -1 * (Ydiff / 4);
            double newY = Xdiff / DILUTEAMOUNT; // -1 * (Xdiff / 4);//newX * ((-1 / slope) / 10);

            // and now create the vector and apply the force, hopefully.
            Vector force = new Vector(newX, 0.5, newY);

            //entity.setVelocity(entity.getVelocity().add(force));
            entity.setVelocity(force);
        */

            // My attempt at actually using my trig knowledge:
            double distance = player.getLocation().distance(entity.getLocation());
            double theta = Math.acos(Xdiff / distance);
            
            //theta -= Math.PI / 2; // Decrement by 90 degrees (or Pi/2 radians)
            if (theta > (3 / 2) * Math.PI) {
                theta -= Math.PI / 2;
            } else if (theta < Math.PI / 2) {
                theta += Math.PI / 2;
            } else {
                theta += Math.PI / 2;
            }

            double rotatedX = Math.cos(theta) * RADIUS;
            double rotatedY = Math.sin(theta) * RADIUS;

            Vector force = new Vector(rotatedX, 0.5, rotatedY);
            //entity.setVelocity(entity.getVelocity().add(force));
            entity.setVelocity(force);

            entity.getLocation().getBlock().getRelative(0,-1,0).setType(Material.DIAMOND_BLOCK);
            
            player.sendMessage("Theta: " + theta);
            player.sendMessage(force.toString());
            castAgain = true;
        }
        if (castAgain) {
            schedule(5, cast, player);
        }
    }
}

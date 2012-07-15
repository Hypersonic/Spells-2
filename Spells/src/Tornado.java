import java.util.List;
import java.lang.reflect.Method;

import org.bukkit.util.Vector;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import aor.spells.Spell;


public class Tornado extends Spell {

    private Method cast;
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
    
        List<Entity> nearbyEntities = player.getNearbyEntities(5,5,5);
        for (Entity entity : nearbyEntities) {
            // Y is actually height in minecraft, so the value we'll be calling Y is really Z
            double playerX = player.getLocation().getX();
            double playerY = player.getLocation().getZ();
            double entityX = entity.getLocation().getX();
            double entityY = entity.getLocation().getZ();
            
            double slope = (playerY - entityY) / (playerX - entityX);   //Rise over run :D
            /*
            // To get a line perpendicular to a given line with the formula: y = mx + b,
            // The formula will be: y = -x/m + c
            // B and C are any number.
            
            // if we increase the x value we're using by the difference in the y value... maybe
            double Ydiff = 5; //playerY - entityY;
            double newX = entityX + Ydiff;

            double newY = playerY; //((-1 * newX) / slope / entityY) + entityY;

            // and now create the vector and apply the force, hopefully.
            Vector force = new Vector(newX - entityX, entity.getLocation().getY(), newY - entityY);
            */
            Vector force = new Vector(entityX - playerX, 0, entityY - playerY);
            entity.setVelocity(entity.getVelocity().add(force));
            
            
            player.sendMessage(force.toString());
            player.sendMessage("Slope: " + slope);
            schedule(2, cast, player);
        }
    
    }


}

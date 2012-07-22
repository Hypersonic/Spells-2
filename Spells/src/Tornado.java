import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.lang.reflect.Method;

import org.bukkit.util.Vector;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.Effect;

import aor.spells.Spell;


public class Tornado extends Spell {

    private Method cast; // We're doing this through recursion, bitches.
    
    private static final double DILUTEAMOUNT = 10.0;
    private static final double MAGNITUDE = 0.5;
    private static final double RADIUS = 10;
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
        
        boolean castAgain = false; // We only want to be casting it one additional time, so lets use a variable
        List<Entity> nearbyEntities = player.getNearbyEntities(RADIUS, RADIUS * 5, RADIUS);
        for (Entity entity : nearbyEntities) {
            // Y is actually height in minecraft, so the value we'll be calling Y is really Z
            double playerX = player.getLocation().getX();
            double playerY = player.getLocation().getZ();
            double entityX = entity.getLocation().getX();
            double entityY = entity.getLocation().getZ();
            
            // Relative X and Y values (based on the entity, not the player)
            double Ydiff = entityY - playerY;
            double Xdiff = entityX - playerX;
            
            double distance = player.getLocation().distance(entity.getLocation());
            double theta = Math.acos(Xdiff / distance); // Use the arc cosine of the X difference to get the relative theta (we have to normalize the X over the distance so it is the value on the unit circle)
            
            // We don't want the theta to go over 2Pi, because then it starts acting funky. Yeah, I know.
            if (theta > (3 / 2) * Math.PI) {
                theta -= Math.PI / 2;
            } else if (theta < Math.PI / 2) {
                theta += Math.PI / 2;
            } else {
                theta += Math.PI / 2;
            }

            double rotatedX = Math.cos(theta) * MAGNITUDE;
            double rotatedY = Math.sin(theta) * MAGNITUDE;
            
            // Slight random offsets.
            double Yoffset = (Math.random() - 0.5);
            double Xoffset = (Math.random() - 0.5);
            double HeightOffset = (Math.random());

            Vector force = new Vector(rotatedX + Xoffset, HeightOffset, rotatedY + Yoffset); // Our actual force that acts on the entity, uses a random offset
            //entity.setVelocity(entity.getVelocity().add(force)); // apply the velocity
            entity.setVelocity(force);
            
            //Location entityLoc = entity.getLocation();
            //ArrayList<Location> smokeLocs = new ArrayList<Location>();
            //for (double i = 0; i < 10; i += 0.1) {
                //smokeLocs.add(entityLoc.add(i,0,0));
                //smokeLocs.add(entityLoc.add(i,i,0));
                //smokeLocs.add(entityLoc.add(i,0,i));
                //smokeLocs.add(entityLoc.add(0,i,i));
                //smokeLocs.add(entityLoc.add(i,i,i));
                //smokeLocs.add(entityLoc.add(0,i,0));
                //smokeLocs.add(entityLoc.add(0,0,i));
            //}
            //for (Location loc : smokeLocs) {
                //entity.getWorld().playEffect(loc, Effect.SMOKE, 0);
            //}
            //entity.getLocation().getBlock().getRelative(0,-1,0).setType(Material.DIAMOND_BLOCK); //Trace the path with diamond blocks
            
            //player.sendMessage("Theta: " + theta);
            //player.sendMessage(force.toString());
            castAgain = true;
        }
        if (castAgain) {
            schedule(5, cast, player);
        }
    }
}

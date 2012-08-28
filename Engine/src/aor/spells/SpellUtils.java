package aor.spells;

import static java.lang.Math.cos;
import static java.lang.Math.min;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

/**
 * This class contains a bunch of useful helper methods that make spell creation easier
 * @author Jay
 */
public final class SpellUtils {
	/**
	 * Checks to see if the items are in the player's inventory, without removing them.
	 * @param player - the player to check
	 * @param items - the items to check for
	 * @return boolean - whether or not the items are in the player's inventory
	 */
	public static boolean inInventory(Player player, ItemStack... items){
		return inInventory(player, Arrays.asList(items));
	}
	/**
	 * Checks to see if the items are in the player's inventory, without removing them.
	 * @param player - the player to check
	 * @param items - the items to check for
	 * @return boolean - whether or not the items are in the player's inventory
	 */
	public static boolean inInventory(Player player, Iterable<ItemStack> items){
		if(player.getGameMode().equals(GameMode.CREATIVE)||player.hasPermission("spells.freespells"))return true;
		PlayerInventory inventory=player.getInventory();
		Outer:
		for(ItemStack item:items){
			int needed=item.getAmount();
			for(ItemStack inventoryItem:inventory){
				if(inventoryItem!=null&&inventoryItem.getType().equals(item.getType())&&inventoryItem.getData().equals(item.getData())){
					if(inventoryItem.getAmount()<needed)needed-=inventoryItem.getAmount();
					else continue Outer;
				}
			}
			return false;
		}
		return true;
	}
	/**
	 * Equivalent to inventory.removeItem(items), except it checks to see if the player is in creative mode or has the freespells permission first
	 * @param player - the player to check
	 * @param items - the items to check for
	 * @return boolean - whether or not the items are in the player's inventory
	 */
	public static HashMap<Integer,ItemStack> removeFromInventory(Player player, ItemStack... items){
		if(player.getGameMode().equals(GameMode.CREATIVE)||player.hasPermission("spells.freespells"))return new HashMap<Integer,ItemStack>();
		return player.getInventory().removeItem(items);
	}
	/**
	 * An easier to use version of getTarget(). It returns the player the player passed is currently looking at. It will return null if no player is close enough to where the player is pointing who is in the player's line of sight (only if needsLineOfSight is true).
	 * @param player - the player to find a target for.
	 * @param needsLineOfSight - whether or not the player returned needs to be in the player's line of sight.
	 * @return Player - the found player or null if none are found
	 */
	public static Player getPlayerTarget(Player player, boolean needsLineOfSight){
		Entity result=getTarget(player,100,.5,needsLineOfSight,Arrays.asList(new Class<?>[]{Player.class}));
		if(result!=null&&result instanceof Player)return (Player)result;
		return null;
	}
	/**
	 * An easier to use version of getTarget(). It returns the player the player passed is currently looking at. It will return null if no player is close enough to where the player is pointing who is in the player's line of sight.
	 * @param player - the player to find a target for.
	 * @return Player - the found player or null if none are found
	 */
	public static Player getPlayerTarget(Player player){
		Entity result=getTarget(player,100,.5,true,Arrays.asList(new Class<?>[]{Player.class}));
		if(result!=null&&result instanceof Player)return (Player)result;
		return null;
	}
	/**
	 * Returns the entity the player is most closely looking at that meets the requirements passed in the arguments
	 * @param player - the player to find the target entity for
	 * @param maxDistance - the size of the cube to check for entities
	 * @param maxRadiansOff - the maximum number of raidans off of where the player is looking for the entity to still be considered
	 * @param needsLineOfSight - whether or not the player must be able to see the entity
	 * @param allowedEntityClasses - Only entities that are instances or subclasses of these classes will be considered.
	 * @return
	 */
	public static Entity getTarget(LivingEntity player,int maxDistance, double maxRadiansOff,boolean needsLineOfSight,Collection<Class<?>> allowedEntityClasses){
		maxRadiansOff=cos(maxRadiansOff);
		List<Entity> nearbyEntities=player.getNearbyEntities(maxDistance, maxDistance, maxDistance);
		//double yaw=player.getLocation().getYaw();
		//double pitch=player.getLocation().getPitch();
		Vector playerDirection=player.getLocation().getDirection();//new Vector(cos(yaw)*cos(pitch),sin(yaw), cos(yaw)*sin(pitch)).normalize();
		double nearestAngle=maxRadiansOff;
		Entity bestEntity=null;
		for(Entity entity:nearbyEntities){
			if(!needsLineOfSight||player.hasLineOfSight(entity)){
				double angle=player.getEyeLocation().subtract(entity.getLocation()).toVector().normalize().dot(playerDirection);
				if(entity instanceof LivingEntity)angle=min(angle, player.getEyeLocation().subtract(((LivingEntity)entity).getEyeLocation()).toVector().normalize().dot(playerDirection));
				if(angle<nearestAngle){
					boolean isSubclass=false;
					for(Class<?> allowedEntityClass:allowedEntityClasses){
						if(allowedEntityClass.isInstance(entity)){
							isSubclass=true;
							break;
						}
					}
					if(isSubclass){
						nearestAngle=angle;
						bestEntity=entity;
					}
				}
			}
		}
		return bestEntity;
	}
	/**
	 * This method is used to make the formatting of text that spells send to players more standard, in addition to checking whether or not these messages are enabled in the config file in the first place. We hope to eventually have a way to do this for each player. This is the recommended way to send messages to players.
	 * @param player - the player to send messages to
	 * @param text - the message to send the player
	 * @return boolean - whether or not the message was sent
	 */
	public static boolean flavorText(Player player,String text){
		if(Spells.flavorTextEnabled()){
			player.sendMessage(ChatColor.GRAY+text);
			return true;
		}
		return false;
	}
}

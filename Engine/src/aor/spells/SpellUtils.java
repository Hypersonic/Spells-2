package aor.spells;

import static java.lang.Math.cos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public final class SpellUtils {
	public static boolean inInventory(Player player, Iterable<ItemStack> items){
		return inInventory(player.getInventory(),items);
	}
	public static boolean inInventory(PlayerInventory inventory, Iterable<ItemStack> items){
		for(ItemStack item:items){
			if(!inventory.contains(item,item.getAmount()))return false;
		}
		return true;
	}
	public static void removeFromInventory(Player player, Iterable<ItemStack> items){
		removeFromInventory(player.getInventory(),items);
	}
	public static void removeFromInventory(PlayerInventory inventory, Iterable<ItemStack> items){
		for(ItemStack item:items){
			int amountLeft=item.getAmount();
			while(amountLeft>0){
				int firstFound=inventory.first(item.getType());
				if(inventory.getItem(firstFound).getAmount()>=amountLeft){
					inventory.getItem(firstFound).setAmount(inventory.getItem(firstFound).getAmount()-amountLeft);
					break;
				}
				else{
					amountLeft-=inventory.getItem(inventory.first(item.getType())).getAmount();
					inventory.clear(inventory.first(item.getType()));
				}
			}
		}
	}
	public static Entity getPlayerTarget(Player player, boolean needsLineOfSight){
		Entity result=getTarget(player,100,.5,needsLineOfSight,Arrays.asList(new Class<?>[]{Player.class}));
		if(result!=null&&result instanceof Player)return (Player)result;
		return null;
	}
	public static Entity getPlayerTarget(Player player){
		Entity result=getTarget(player,100,.5,true,Arrays.asList(new Class<?>[]{Player.class}));
		if(result!=null&&result instanceof Player)return (Player)result;
		return null;
	}
	public static Entity getTarget(LivingEntity player,int maxDistance, double maxRadiansOff,boolean needsLineOfSight,Collection<Class<?>> allowedEntityClasses){
		maxRadiansOff=cos(maxRadiansOff);
		List<Entity> nearbyEntities=player.getNearbyEntities(maxDistance, maxDistance, maxDistance);
		Location playerTarget=player.getTargetBlock(new HashSet<Byte>(new ArrayList<Byte>(Arrays.asList(new Byte[]{0}))), 100).getLocation();
		double nearestAngle=maxRadiansOff;
		Entity bestEntity=null;
		Vector playerDirection=player.getLocation().subtract(playerTarget).toVector().normalize();
		for(Entity entity:nearbyEntities){
			double footAngle =  player.getLocation().subtract(entity.getLocation()).toVector().normalize().dot(playerDirection);
            double eyeAngle = player.getLocation().subtract(((LivingEntity)entity).getEyeLocation()).toVector().normalize().dot(playerDirection);
            boolean hasLineOfSight = !needsLineOfSight || player.hasLineOfSight(entity);
			boolean isAllowed = allowedEntityClasses.contains(entity.getClass());
            
            if(hasLineOfSight && footAngle < nearestAngle || (entity instanceof LivingEntity) ? eyeAngle < nearestAngle : false && isAllowed){
				if (footAngle < eyeAngle) {
                    nearestAngle = footAngle;
                } else {
                    nearestAngle = eyeAngle;
                }
				bestEntity=entity;
			}
		}
		return bestEntity;
	}
}

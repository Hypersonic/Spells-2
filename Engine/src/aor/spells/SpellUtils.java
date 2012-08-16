package aor.spells;

import static java.lang.Math.cos;
import static java.lang.Math.min;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public final class SpellUtils {
	public static boolean inInventory(Player player, ItemStack... items){
		return inInventory(player, Arrays.asList(items));
	}
	public static boolean inInventory(Player player, Iterable<ItemStack> items){
		if(player.getGameMode().equals(GameMode.CREATIVE))return true;
		PlayerInventory inventory=player.getInventory();
		Outer:
		for(ItemStack item:items){
			int needed=item.getAmount();
			for(ItemStack inventoryItem:inventory){
				if(inventoryItem!=null&&inventoryItem.getType().equals(item.getType())){
					if(inventoryItem.getAmount()<needed)needed-=inventoryItem.getAmount();
					else continue Outer;
				}
			}
			return false;
		}
		return true;
	}
	public static Player getPlayerTarget(Player player, boolean needsLineOfSight){
		Entity result=getTarget(player,100,.5,needsLineOfSight,Arrays.asList(new Class<?>[]{Player.class}));
		if(result!=null&&result instanceof Player)return (Player)result;
		return null;
	}
	public static Player getPlayerTarget(Player player){
		Entity result=getTarget(player,100,.5,true,Arrays.asList(new Class<?>[]{Player.class}));
		if(result!=null&&result instanceof Player)return (Player)result;
		return null;
	}
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
}

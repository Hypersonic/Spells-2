package aor.spells;

import static java.lang.Math.cos;
import static java.lang.Math.min;
import static java.lang.Math.sin;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
		Outer:
		for(ItemStack item:items){
			if(!inventory.contains(item,item.getAmount())){
				int needed=item.getAmount();
				for(ItemStack inventoryItem:inventory){
					if(inventoryItem.getType()==item.getType()){
						if(inventoryItem.getAmount()<needed)needed-=inventoryItem.getAmount();
						else continue Outer;
					}
				}
				return false;
			}
		}
		return true;
	}
	public static void removeFromInventory(Player player, Iterable<ItemStack> items){
		removeFromInventory(player.getInventory(),items);
	}
	public static void removeFromInventory(PlayerInventory inventory, Iterable<ItemStack> items){
		for(ItemStack item:items){
			int amountLeft=item.getAmount();
			for(int i=0;i<45;i++){
				ItemStack inventoryItem=inventory.getItem(i);
				if(inventoryItem.getType()==item.getType()){
					if(inventoryItem.getAmount()>amountLeft){
						inventoryItem.setAmount(inventoryItem.getAmount()-amountLeft);
						break;
					}
					else{
						amountLeft-=inventoryItem.getAmount();
						inventory.remove(i);
						if(amountLeft==0)break;
					}
				}
			}
		}
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
		double yaw=player.getLocation().getYaw();
		double pitch=player.getLocation().getPitch();
		Vector playerDirection=new Vector(cos(yaw)*cos(pitch),sin(yaw), cos(yaw)*sin(pitch)).normalize();
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

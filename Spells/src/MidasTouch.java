import static aor.spells.SpellUtils.inInventory;
import static aor.spells.SpellUtils.removeFromInventory;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import aor.spells.Spell;


public class MidasTouch extends Spell{
	private static final HashSet<Material> allowedBlocks=new HashSet<Material>(Arrays.asList(new Material[]{Material.BED_BLOCK,Material.BEDROCK,Material.BOAT,Material.BOOKSHELF,Material.BRICK,Material.BRICK,Material.CAKE_BLOCK,Material.COAL_ORE,Material.COBBLESTONE,Material.COBBLESTONE_STAIRS,Material.DEAD_BUSH,Material.DIAMOND_BLOCK,Material.DIAMOND_ORE,Material.DIRT,Material.FENCE,Material.GLASS,Material.GLOWING_REDSTONE_ORE,Material.GLOWSTONE,Material.GOLD_ORE,Material.GRASS,Material.GRAVEL,Material.ICE,Material.IRON_BLOCK,Material.IRON_DOOR_BLOCK,Material.IRON_ORE,Material.JACK_O_LANTERN,Material.LAPIS_BLOCK,Material.LAPIS_ORE,Material.LAVA,Material.LEAVES,Material.MOSSY_COBBLESTONE,Material.NETHERRACK,Material.OBSIDIAN,Material.PUMPKIN,Material.REDSTONE_ORE,Material.SAND,Material.SNOW_BLOCK,Material.SOIL,Material.SOUL_SAND,Material.STATIONARY_LAVA,Material.STATIONARY_WATER,Material.STEP,Material.STONE,Material.SUGAR_CANE_BLOCK,Material.TNT,Material.TRAP_DOOR,Material.WOOD,Material.LOG,Material.WOOD_STAIRS,Material.WOODEN_DOOR,Material.WOOL,Material.WORKBENCH}));
	private final Method removeMidas=getMethod("removeMidas");
	private ArrayList<Player> players=new ArrayList<Player>(0);
	private HashMap<Player,ArrayList<Location>> locations=new HashMap<Player,ArrayList<Location>>();
	private HashMap<Player,ArrayList<Material>> materials=new HashMap<Player,ArrayList<Material>>();
	private HashMap<Player,ArrayList<Byte>> bytes=new HashMap<Player,ArrayList<Byte>>();
	private boolean isMidas=false;
	public MidasTouch(){
	}
	@Override
	public String getCooldownMessage(){
		return "Are you really that greedy?";
	}
	@Override
	public boolean checkRequirements(Player player){
		return inInventory(player,new ItemStack(Material.GOLD_BLOCK,10));
	}
	@Override
	public void removeRequirements(Player player){
		removeFromInventory(player,new ItemStack(Material.GOLD_BLOCK,10));
	}
	@Override
	public void cast(Player player){
		player.sendMessage("You now have Midas' Touch!");
		players.add(player);
		locations.put(player,new ArrayList<Location>());
		materials.put(player,new ArrayList<Material>());
		bytes.put(player,new ArrayList<Byte>());
		isMidas=true;
		schedule(1200, removeMidas);
		onPlayerMove(new PlayerMoveEvent(player, null, null));
		
	}
	@Override
	public int getCooldown(){
		return 1200;
	}
	public void removeMidas(){
		while(locations.get(players.get(0)).size()>0){
			locations.get(players.get(0)).get(0).getWorld().getBlockAt(locations.get(players.get(0)).get(0)).setType(materials.get(players.get(0)).get(0));
			locations.get(players.get(0)).get(0).getWorld().getBlockAt(locations.get(players.get(0)).get(0)).setData(bytes.get(players.get(0)).get(0));
			locations.get(players.get(0)).remove(0);
			materials.get(players.get(0)).remove(0);
			bytes.get(players.get(0)).remove(0);
		}
		locations.remove(players.get(0));
		materials.remove(players.get(0));
		bytes.remove(players.get(0));
		players.get(0).sendMessage("You finally decide that you don't want everything you touch to become gold.");
		players.remove(0);
		if(players.size()==0){
			isMidas=false;
		}
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onBlockDamage(BlockDamageEvent event){
		if(isMidas){
			for(int i=0;i<players.size();i++){
				if(locations.get(players.get(i)).contains(event.getBlock().getLocation())){
					event.setCancelled(true);
					event.getPlayer().sendMessage("NOPE!!!");
				}
			}
		}
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event){
		if(isMidas){
			if(players.contains(event.getPlayer())){
				for(int i=0;i<3;i++){
					for(int i2=0;i2<3;i2++){
						for(int i3=0;i3<4;i3++){
							changeToGold(event.getPlayer().getLocation().getBlock().getRelative(i-1,i3-1,i2-1),event.getPlayer());
						}
					}
				}
			}
		}
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event){
		if(isMidas){
			for(int i=0;i<players.size();i++){
				if(locations.get(players.get(i)).contains(event.getBlock().getLocation())){
					event.setCancelled(true);
				}
			}
		}
	}
	public void changeToGold(Block block,Player player){
		if(allowedBlocks.contains(block.getType())){
			locations.get(player).add(block.getLocation());
			materials.get(player).add(block.getType());
			bytes.get(player).add(block.getData());
			block.setType(Material.GOLD_BLOCK);
		}
	}
	@Override
	public String getName() {
		return "Midas' Touch";
	}
	@Override
	public String getDescription() {
		return "This spell turns every block you touch into a gold block for one minute.";
	}
	@Override
	public String getRequirements() {
		return "You need 10 Gold Blocks to be able to cast this spell.";
	}
}
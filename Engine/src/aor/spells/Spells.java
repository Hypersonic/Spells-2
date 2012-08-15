package aor.spells;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.PluginClassLoader;

public final class Spells extends JavaPlugin implements Listener{
	public static final Logger log = Logger.getLogger("Minecraft");
	private HashMap<Player,SpellBook> spellBooks=new HashMap<Player,SpellBook>();
	private SpellGroup spells=new SpellGroup("Spells");
	public void onDisable() {
		Bukkit.getServer().getScheduler().cancelTasks(this);
		Scheduler.stop(this);
		spells=null;
		log.info("Spells 2.0 Disabled");
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,String[] args) {
		if(command.getName().equalsIgnoreCase("spellinfo")){
			if(args.length!=0){
				Spell spell=spells.getSpell(args[0]);
				if(spell!=null){
					if(spell.getDescription()==null){
						sender.sendMessage(spell.getName()+" doesn't have a description!");
					}
					else sender.sendMessage(spell.getDescription());
				}
				else{
					sender.sendMessage(args[0]+" isn't a valid spell! Please try again!");
				}
			}
			else if(sender instanceof Player){
				final Player player=((Player)sender);
				Object spellOrGroup= spellBooks.get(player).getCurrentSpellOrGroup();
				if(spellOrGroup instanceof Spell){
					player.sendMessage(((Spell)spellOrGroup).getDescription());
				}
				else{
					player.sendMessage("You must have a spell selected to be able to use spellinfo with no argument!");
				}
			}
			else {
				sender.sendMessage("You cannot use spellinfo from the commandline without giving a spell as an argument!");
			}
			return true;
		}
		else if(command.getName().equalsIgnoreCase("cast")){
			if(sender instanceof Player){
				final Player player=(Player)sender;
				if(args.length==0){
					final SpellBook spellBook=spellBooks.get(player);
					Object spellOrGroup=spellBook.getCurrentSpellOrGroup();
					if(spellOrGroup instanceof Spell){
						Bukkit.getServer().getPluginManager().callEvent(new SpellCastEvent((Spell)spellOrGroup,player));
					}
					else{
						player.sendMessage("You can't use cast unless you provide a spellname as an argument or you have a spell selected.");
					}
				}
				else{
					Spell spell=spells.getSpell(args[0]);
					if(spell!=null){
						Bukkit.getServer().getPluginManager().callEvent(new SpellCastEvent(spell,player));
					}
					else{
						player.sendMessage("That spell doesn't exist!");
					}
				}
			}
			else{
				sender.sendMessage("The console can't cast spells! That wouldn't make sense!");
			}
			return true;
		}
		else if(command.getName().equalsIgnoreCase("goin")){
			if(sender instanceof Player){
				final Player player=(Player)sender;
				if(args.length==0){
					final SpellBook spellBook=spellBooks.get(player);
					Object spellOrGroup=spellBook.getCurrentSpellOrGroup();
					if(spellOrGroup instanceof Spell){
						player.sendMessage("You can't go into a spell! You need to select a spellgroup first.");
					}
					else{
						spellBook.goInGroup();
						final Object spellOrGroup2=spellBook.getCurrentSpellOrGroup();
						player.sendMessage(ChatColor.BLUE+((spellOrGroup2 instanceof Spell?((Spell)spellOrGroup2).getName():((SpellGroup)spellOrGroup2).getName())+" selected"));
					}
				}
			}
			else{
				sender.sendMessage("The console can't go into groups! That wouldn't make sense!");
			}
			return true;
		}
		else if(command.getName().equalsIgnoreCase("goout")){
			if(sender instanceof Player){
				final Player player=(Player)sender;
				if(args.length==0){
					final SpellBook spellBook=spellBooks.get(player);
					if(spellBook.hasParentGroup()){
						spellBook.goOutOfGroup();
						final Object spellOrGroup=spellBook.getCurrentSpellOrGroup();
						player.sendMessage(ChatColor.BLUE+((spellOrGroup instanceof Spell?((Spell)spellOrGroup).getName():((SpellGroup)spellOrGroup).getName())+" selected"));
					}
					else{
						player.sendMessage("You can't go out of your current group, because you are in the highest group in the hierarchy!");
					}
				}
			}
			else{
				sender.sendMessage("The console can't go out of groups! That wouldn't make sense!");
			}
			return true;
		}
		else if(command.getName().equalsIgnoreCase("nextspell")){
			if(sender instanceof Player){
				final Player player=(Player)sender;
				final SpellBook spellBook=spellBooks.get(player);
				spellBook.next();
				sendPlayerCurrentSpellOrGroup(player);
			}
			else{
				sender.sendMessage("The console can't go to the next spell! That wouldn't make sense!");
			}
			return true;
		}
		else if(command.getName().equalsIgnoreCase("currentlyselected")){
			if(sender instanceof Player){
				final Player player=(Player)sender;
				sendPlayerCurrentSpellOrGroup(player);
			}
			else{
				sender.sendMessage("The console can't have a spell or spell group selected! That wouldn't make sense!");
			}
			return true;
		}
		else if(command.getName().equalsIgnoreCase("selectspell")){
			if(sender instanceof Player){
				final Player player=(Player)sender;
				if(args.length>0){
					Spell spell=spells.getSpell(args[0]);
					if(spell==null){
						player.sendMessage("That is not a valid spell!");
					}
					else{
						final SpellBook spellBook=spellBooks.get(player);
						final SpellGroup group=spells.getGroup(spell);
						spellBook.setGroup(group, group.indexOf(spell));
						sendPlayerCurrentSpellOrGroup(player);
					}
				}
				else{
					player.sendMessage("You must provide a spell that you'd like to select.");
				}
			}
			else{
				sender.sendMessage("The console can't have a spell or spell group selected! That wouldn't make sense!");
			}
			return true;
		}
		return false;
	}
	public void onEnable() {
		final File spelldir=new File("plugins/spells/");
		if(!spelldir.exists()){
			try {
				spelldir.mkdir();
				log.warning("No spells folder exists, so it was created.");
			}
			catch (Exception e) { //TODO: Make this error more specific in different cases, if they exist
				log.warning("No spells folder exists and the plugin can't create it, because the directory is write protected.");
			}
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		loadSpells(spelldir);
		spells.print();//this is just for debugging
		if(spells==null||spells.size()==0){
			log.warning("No Spells Loaded!");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		for(Spell spell:spells){
			//log.info(spell.getName());
			getServer().getPluginManager().registerEvents(spell, this);
		}
		Scheduler.start(this);
		final Player[] players=Bukkit.getServer().getOnlinePlayers();
		for(int i=0;i<players.length;i++){
			spellBooks.put(players[i], new SpellBook(spells));
		}
		getServer().getPluginManager().registerEvents(this, this);
		log.info("Spells 2.0 enabled");
	}
	private void loadSpells(File spelldir){
		final ArrayList<Spell> loadedSpells=new ArrayList<Spell>();
		final PluginClassLoader loader=(PluginClassLoader) Spells.class.getClassLoader();
		try{
			loader.addURL(spelldir.toURI().toURL());
		} catch(Throwable t){}
		for(File f:Arrays.asList(spelldir.listFiles())){
			if(f.getName().endsWith(".class")){
				try {
					Class<?> clazz = loader.loadClass(f.getName().replace(".class", ""));
					if(Spell.class.isInstance(clazz.newInstance())){
						Class<? extends Spell> s=clazz.asSubclass(Spell.class);
						loadedSpells.add(s.newInstance());
					}
					else if(SpellSet.class.isInstance(clazz.newInstance())){
						Class<? extends SpellSet> s=clazz.asSubclass(SpellSet.class);
						loadedSpells.addAll(s.newInstance().getSpells());
					}
				} catch (Throwable t) {}
			}
			else if(f.getName().endsWith(".jar")){
				try{
					loader.addURL(new URL(new URL("jar:"+f.toURI().toURL()+"!/"),""));
					Class<?> clazz = loader.loadClass(f.getName().replace(".jar", ""));
					if(Spell.class.isInstance(clazz.newInstance())){
						Class<? extends Spell> s=clazz.asSubclass(Spell.class);
						loadedSpells.add(s.newInstance());
					}
					else if(SpellSet.class.isInstance(clazz.newInstance())){
						Class<? extends SpellSet> s=clazz.asSubclass(SpellSet.class);
						loadedSpells.addAll(s.newInstance().getSpells());
					}
				} catch(Throwable t){
					t.printStackTrace();
				}
			}
			else if(f.isDirectory())loadSpells(f);
		}
		for(Spell spell:loadedSpells){
			if(spell.getName().contains(" "))log.log(Level.WARNING, spell.getName()+" couldn't be loaded, because its name contains a space.");
			else if(spell.getName()==null)log.log(Level.WARNING, spell.getName()+" couldn't be loaded, because names cannot be null.");
			else if(spell.getName()==null)log.log(Level.WARNING, spell.getName()+" couldn't be loaded, because names cannot be empty.");
			else if(spells.getSpell(spell.getName())==null)spells.place(spell,spell.getGroup());
			else log.log(Level.WARNING, spell.getName()+" could not be loaded, because a spell with the same name was already loaded.");
		}
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void playerInteract(PlayerInteractEvent e){
		final Player player=e.getPlayer();
		if(player.getItemInHand().getType()==Material.GOLD_HOE){
			final Action action=e.getAction();
			final SpellBook book=spellBooks.get(player);
			Object spellOrGroup=book.getCurrentSpellOrGroup();
			if(spellOrGroup instanceof Spell){
				Spell spell=(Spell)spellOrGroup;
				if(action.equals(Action.LEFT_CLICK_AIR)||action.equals(Action.LEFT_CLICK_BLOCK)){
					e.setCancelled(true);
					Bukkit.getServer().getPluginManager().callEvent(new SpellCastEvent(spell,player));
				}
				else if(action.equals(Action.RIGHT_CLICK_AIR)||action.equals(Action.RIGHT_CLICK_BLOCK)){
					e.setCancelled(true);
					if(player.isSneaking()){
						book.goOutOfGroup();
						sendPlayerCurrentSpellOrGroup(player);
					}
					else{
						book.next();
						sendPlayerCurrentSpellOrGroup(player);
					}
				}
			}
			else if(spellOrGroup instanceof SpellGroup){
				if(action.equals(Action.LEFT_CLICK_AIR)||action.equals(Action.LEFT_CLICK_BLOCK)){
					e.setCancelled(true);
					book.goInGroup();
					sendPlayerCurrentSpellOrGroup(player);
				}
				else if(action.equals(Action.RIGHT_CLICK_AIR)||action.equals(Action.RIGHT_CLICK_BLOCK)){
					e.setCancelled(true);
					if(player.isSneaking()){
						book.goOutOfGroup();
						sendPlayerCurrentSpellOrGroup(player);
					}
					else{
						book.next();
						sendPlayerCurrentSpellOrGroup(player);
					}
				}
			}
		}
	}
	private void sendPlayerCurrentSpellOrGroup(Player player){
		final Object spellOrGroup=spellBooks.get(player).getCurrentSpellOrGroup();
		player.sendMessage(ChatColor.BLUE+((spellOrGroup instanceof Spell?((Spell)spellOrGroup).getName():((SpellGroup)spellOrGroup).getName())+" selected"));
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void spellCast(SpellCastEvent e){
		if(e.isCancelled())return;
		final Spell spell=e.getSpell();
		final Player player=e.getPlayer();
		final SpellBook spellBook=spellBooks.get(player);
		if(!spell.checkRequirements(e.getPlayer())){
			e.setCancelled(true);
			return;
		}
		else if(spellBook.hasCooldown(spell)){
			e.setCancelled(false);
			return;
		}
		spell.removeRequirements(player);
		spell.cast(player);
		spellBook.addCooldown(spell);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onLogin(PlayerJoinEvent e){
		spellBooks.put(e.getPlayer(), new SpellBook(spells));
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onLogoff(PlayerQuitEvent e){
		spellBooks.remove(e.getPlayer());
	}
}

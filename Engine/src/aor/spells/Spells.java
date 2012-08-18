package aor.spells;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
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
		changeGoldHoeCraftingRecipe(true);
		log.info("Spells 2.0 Disabled");
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,String[] args) {
		if(command.getName().equalsIgnoreCase("spells")||command.getName().equalsIgnoreCase("s")){
			String name="help";
			if(args.length>0){
				name=args[0];
				args=Arrays.copyOfRange(args, 1, args.length);
			}
			if(name.equalsIgnoreCase("info")){
				if(args.length!=0){
					Spell spell=spells.getSpell(args[0]);
					if(spell!=null){
						if(spell.getDescription()==null){
							sender.sendMessage(spell.getName()+" doesn't have a description!");
						}
						else sender.sendMessage(spell.getDescription());
					}
					else sender.sendMessage(args[0]+" isn't a valid spell! Please try again!");
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
				else sender.sendMessage("You cannot use spellinfo from the commandline without giving a spell as an argument!");
				return true;
			}
			else if(name.equalsIgnoreCase("cast")){
				if(sender instanceof Player){
					final Player player=(Player)sender;
					if(args.length==0){
						final SpellBook spellBook=spellBooks.get(player);
						Object spellOrGroup=spellBook.getCurrentSpellOrGroup();
						if(player.getItemInHand().getType().equals(Material.GOLD_HOE)){
							if(spellOrGroup instanceof Spell){
								final Spell spell=(Spell) spellOrGroup;
								if(!player.isPermissionSet("spells."+spell.getGroup()+"."+spell.getName())||player.hasPermission("spells."+spell.getGroup()+"."+spell.getName()))Bukkit.getServer().getPluginManager().callEvent(new SpellCastEvent(spell,player));
								else player.sendMessage("You do not have the required permissions to use this spell!");
							}
							else{
								player.sendMessage("You can't use cast unless you provide a spellname as an argument or you have a spell selected.");
							}
						}
						else{
							player.sendMessage("You must create a scepter to be able to cast spells!");
						}
					}
					else{
						Spell spell=spells.getSpell(args[0]);
						if(spell!=null){
							if(!player.isPermissionSet("spells."+spell.getGroup()+"."+spell.getName())||player.hasPermission("spells."+spell.getGroup()+"."+spell.getName()))Bukkit.getServer().getPluginManager().callEvent(new SpellCastEvent(spell,player));
							else player.sendMessage("You do not have the required permissions to use this spell!");
						}
						else player.sendMessage("That spell doesn't exist!");
					}
				}
				else sender.sendMessage("The console can't cast spells! That wouldn't make sense!");
				return true;
			}
			else if(name.equalsIgnoreCase("in")){
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
				else sender.sendMessage("The console can't go into groups! That wouldn't make sense!");
				return true;
			}
			else if(name.equalsIgnoreCase("out")){
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
				else sender.sendMessage("The console can't go out of groups! That wouldn't make sense!");
			}
			else if(name.equalsIgnoreCase("next")){
				if(sender instanceof Player){
					final Player player=(Player)sender;
					final SpellBook spellBook=spellBooks.get(player);
					spellBook.next();
					sendPlayerCurrentSpellOrGroup(player);
				}
				else sender.sendMessage("The console can't go to the next spell! That wouldn't make sense!");
			}
			else if(name.equalsIgnoreCase("prev")||command.getName().equalsIgnoreCase("previous")){
				if(sender instanceof Player){
					final Player player=(Player)sender;
					final SpellBook spellBook=spellBooks.get(player);
					spellBook.next();
					sendPlayerCurrentSpellOrGroup(player);
				}
				else sender.sendMessage("The console can't go to the previous spell! That wouldn't make sense!");
			}
			else if(name.equalsIgnoreCase("current")){
				if(sender instanceof Player){
					final Player player=(Player)sender;
					sendPlayerCurrentSpellOrGroup(player);
				}
				else sender.sendMessage("The console can't have a spell or spell group selected! That wouldn't make sense!");
			}
			else if(name.equalsIgnoreCase("selectspell")){
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
					else player.sendMessage("You must provide a spell that you'd like to select.");
				}
				else sender.sendMessage("The console can't have a spell or spell group selected! That wouldn't make sense!");
			}
			else if(name.equalsIgnoreCase("help")){
				sender.sendMessage("Spells Help - These commands are not case sensitive. You may use either /spells or /s in each example:");
				sender.sendMessage("/Spells cast [spellname] - Casts the currently selected spell or the spell provided as an argument. ");
				sender.sendMessage("/Spells current - Displays the name of the currently selected spell and its description.");
				sender.sendMessage("/Spells help - Displays this message");
				sender.sendMessage("/Spells in - Goes into the currently selected spellgroup.");
				sender.sendMessage("/Spells info [spellname] - Displays information about the currently selected spell or the spell provided as an argument.");
				sender.sendMessage("/Spells next - Selects the next spell. Equivalent to right clicking with a scepter.");
				sender.sendMessage("/Spells out - Selects the spellgroup you are currently within.");
				sender.sendMessage("/Spells prev[ious] - selects the previous spell.");
				sender.sendMessage("/Spells selectspell spellname - selects the spell provided as an argument");
				sender.sendMessage("For more information, see our wiki at https://github.com/Hypersonic/Spells-2/wiki");
			}
			else{
				sender.sendMessage(name+" isn't a known command! Type /spells help or /s help for a list of commands.");
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
		//spells.print(); this is just for debugging
		if(spells==null||spells.size()==0){
			log.warning("No Spells Loaded!");
			log.info("Spells is attempting to download the default spells. You can always delete ones you don't want and add news ones.");
			try {
				ReadableByteChannel c=Channels.newChannel(new URL("https://dl.dropbox.com/u/36992498/Spells.zip").openStream());
				FileOutputStream fos=new FileOutputStream("plugins/spells/Spells.zip");
				fos.getChannel().transferFrom(c, 0, Long.MAX_VALUE);
				fos.close();
				ZipInputStream zis=new ZipInputStream(new BufferedInputStream(new FileInputStream("plugins/spells/Spells.zip"),2048));
				ZipEntry entry;
				while((entry=zis.getNextEntry())!=null){
					byte[] data=new byte[2048];
					BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream("plugins/spells/"+entry.getName()),2048);
					int amount;
					while((amount=zis.read(data, 0, 2048))!=-1){
						bos.write(data,0,amount);
					}
					bos.flush();
					bos.close();
				}
				zis.close();
				new File("plugins/spells/Spells.zip").delete();
			} catch (Exception e) {
				log.warning("Spells was unable to download the spells, because of the following error:\n");
				e.printStackTrace();
				Bukkit.getServer().getPluginManager().disablePlugin(this);
			}
			loadSpells(spelldir);
			if(spells==null||spells.size()==0)Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		for(Spell spell:spells){
			getServer().getPluginManager().registerEvents(spell, this);
		}
		Scheduler.start(this);
		final Player[] players=Bukkit.getServer().getOnlinePlayers();
		for(int i=0;i<players.length;i++){
			spellBooks.put(players[i], new SpellBook(spells));
		}
		getServer().getPluginManager().registerEvents(this, this);
		changeGoldHoeCraftingRecipe(false);
		log.info("Spells 2.0 Enabled");
	}
	private static final void changeGoldHoeCraftingRecipe(boolean normal) {
		LinkedList<Recipe> recipes=new LinkedList<Recipe>();
		Iterator<Recipe> iterator=Bukkit.getServer().recipeIterator();
		while(iterator.hasNext()){
			Recipe recipe=iterator.next();
			if(recipe.getResult().getType()!=Material.GOLD_HOE)recipes.add(recipe);
		}
		Bukkit.getServer().clearRecipes();
		for(Recipe recipe:recipes){
			Bukkit.getServer().addRecipe(recipe);
		}
		ShapedRecipe goldHoe=new ShapedRecipe(new ItemStack(Material.GOLD_HOE));
		if(normal){
			goldHoe.shape("GGE","ESE","ESE");
			goldHoe.setIngredient('G', Material.GOLD_INGOT);
		}
		else{
			goldHoe.shape("ESG","ESS","SEE");
			goldHoe.setIngredient('G', Material.GOLD_BLOCK);
		}
		goldHoe.setIngredient('S', Material.STICK);
		Bukkit.getServer().addRecipe(goldHoe);
		ShapedRecipe goldHoe2=new ShapedRecipe(new ItemStack(Material.GOLD_HOE));
		if(normal){
			goldHoe2.shape("GSE","SSE","EES");
			goldHoe2.setIngredient('G', Material.GOLD_INGOT);
		}
		else{
			goldHoe2.shape("EGG","ESE","ESE");
			goldHoe2.setIngredient('G', Material.GOLD_BLOCK);
		}
		goldHoe2.setIngredient('S', Material.STICK);
		Bukkit.getServer().addRecipe(goldHoe2);
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
				} catch (Throwable t) {
					log.log(Level.WARNING, f.getName()+" not loaded, because of an error. "+t.getLocalizedMessage());
				}
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
					log.log(Level.WARNING, f.getName()+" not loaded, because of an error. "+t.getLocalizedMessage());
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
					if(!player.isPermissionSet("spells."+spell.getGroup()+"."+spell.getName())||player.hasPermission("spells."+spell.getGroup()+"."+spell.getName()))Bukkit.getServer().getPluginManager().callEvent(new SpellCastEvent(spell,player));
					else player.sendMessage("You do not have the required permissions to use this spell!");
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
			player.sendMessage(ChatColor.RED+"You cannot cast "+spell.getName().replaceAll("_"," ")+", because you do not meet the requirements.");
			player.sendMessage(ChatColor.RED+spell.getRequirements());
			e.setCancelled(true);
			return;
		}
		else if(spellBook.hasCooldown(spell)){
			player.sendMessage(ChatColor.RED+spell.getCooldownMessage());
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

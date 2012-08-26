package aor.spells;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
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
	private static int permissions=0;
	private static boolean spellGroups=true;
	private static boolean flavorText=true;
	private HashMap<String,String> forcedSpellGroups=new HashMap<String,String>();
	private int outputSetting=1;
	public void onDisable() {
		Bukkit.getServer().getScheduler().cancelTasks(this);
		Scheduler.stop(this);
		spells=null;
		changeGoldHoeCraftingRecipe(true);
		if(outputSetting>0)log.log(Level.INFO,"Spells 2.0 Disabled");
	}
	public void onEnable() {
		final File spelldir=new File("plugins/spells/");
		if(!spelldir.exists()){
			try {
				spelldir.mkdir();
				if(outputSetting>0)log.log(Level.WARNING,"No spells folder exists, so it was created.");
			}
			catch (Exception e) { //TODO: Make this error more specific in different cases, if they exist
				if(outputSetting>0)log.log(Level.WARNING,"No spells folder exists and the plugin can't create it, because the directory is write protected.");
			}
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		loadConfig();
		loadSpells(spelldir);
		if(outputSetting>1)spells.print();
		if(spells==null||spells.size()==0){
			log.log(Level.WARNING,"No Spells Loaded!");
			if(downloadSpells()){
				loadSpells(spelldir);
				if(spells==null||spells.size()==0){
					Bukkit.getServer().getPluginManager().disablePlugin(this);
					return;
				}
			}
			else{
				Bukkit.getServer().getPluginManager().disablePlugin(this);
				return;
			}
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
		if(outputSetting>0)log.log(Level.INFO,"Spells 2.0 Enabled");
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
					if(outputSetting>0)log.log(Level.WARNING, f.getName()+" not loaded, because of an error. "+t.getLocalizedMessage());
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
					if(outputSetting>0)log.log(Level.WARNING, f.getName()+" not loaded, because of an error. "+t.getLocalizedMessage());
				}
			}
			else if(f.isDirectory())loadSpells(f);
		}
		for(Spell spell:loadedSpells){
			if(spell.getName()==null)if(outputSetting>0){log.log(Level.WARNING, spell.getName()+" couldn't be loaded, because names cannot be null.");}
			else if(spell.getName()==null)if(outputSetting>0){log.log(Level.WARNING, spell.getName()+" couldn't be loaded, because names cannot be empty.");}
			else if(spells.getSpell(spell.getName())==null)spells.place(spell,spellGroups?forcedSpellGroups.containsKey(spell.getName())?forcedSpellGroups.get(spell.getName()):spell.getGroup():"");
			else if(outputSetting>0)log.log(Level.WARNING, spell.getName()+" could not be loaded, because a spell with the same name was already loaded.");
		}
	}
	private boolean downloadSpells(){
		if(outputSetting>0)log.log(Level.INFO,"Spells is attempting to download the default spells. You can always delete ones you don't want and add news ones.");
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
			return true;
		} catch (Exception e) {
			if(outputSetting>0)log.log(Level.WARNING,"Spells was unable to download the spells, because of the following error:\n");
			e.printStackTrace();
			return false;
		}
	}
	private void loadConfig(){
		final File spellsdir=new File("plugins/spells/");
		final File config=new File("plugins/spells/config.properties");
		if(!spellsdir.exists()||!config.exists()){
			try{
				if(!spellsdir.exists())spellsdir.createNewFile();
				config.createNewFile();
				FileWriter writer=new FileWriter(config,false);
				writer.write("#Everything in a line after a # is ignored and considered a comment.\n" +
						"\n" +
						"\n" +
						"permissions: everybody #(everybody, ops or permissions)\n" +
						"spell groups: enabled #(enabled or disabled) requires server reload to update\n" +
						"flavor text: enabled #(enabled or disabled)\n" +
						"console output: normal #(none, normal, verbose or debug)\n" +
						"\n" +
						"#you can customize the spell groups spells are placed in in this config file.\n" +
						"#simply type the spell name followed by a colon and a space and the spellgroup you'd like it to be in\n" +
						"#for example:\n" +
						"#example spell: examplegroup.examplesubgroup\n" +
						"#this only works when spell groups are enabled and the server must reload for this to take effect.\n");
				if(outputSetting>0)log.log(Level.INFO, "The Spells config file didn't exist, so it was created.");
				writer.flush();
				writer.close();
			}
			catch(Exception e){
				if(outputSetting>0)log.log(Level.WARNING, "The Spells config file does not exist and it could not be created, because of a "+e.getClass()+": "+e.getLocalizedMessage());
			}
		}
		else{
			try {
				HashSet<String> validCommands=new HashSet<String>(Arrays.asList(new String[]{"permissions","spell groups","flavor text","console output"}));
				Scanner filereader=new Scanner(config);
				filereader.useDelimiter("\n");
				int lineNumber=0;
				while(filereader.hasNext()){
					lineNumber++;
					try{
						String line=filereader.next();
						if(!line.startsWith("#")){
							if(line.contains("#"))line=line.substring(0, line.indexOf("#"));
							if(line.contains(":")){
								String name=line.substring(0, line.indexOf(":"));
								String setting=line.substring(line.indexOf(":")+1, line.length());
								while(setting.startsWith(" "))setting=setting.substring(1);
								while(setting.endsWith(" "))setting=setting.substring(0, setting.length()-1);
								if(validCommands.remove(name)){
									if(name.equals("permissions")){
										if(setting.equalsIgnoreCase("ops"))permissions=1;
										else if(setting.equalsIgnoreCase("permissions"))permissions=2;
										else permissions=0;
									}
									else if(name.equals("spell groups")){
										if(setting.equalsIgnoreCase("disabled"))spellGroups=false;
										else spellGroups=true;
									}
									else if(name.equals("flavor text")){
										if(setting.equalsIgnoreCase("disabled"))flavorText=false;
										else flavorText=true;
									}
									else if(name.equals("console output")){
										if(setting.equalsIgnoreCase("none"))outputSetting=0;
										else if(setting.equalsIgnoreCase("verbose"))outputSetting=2;
										else if(setting.equalsIgnoreCase("debug"))outputSetting=3;
										else outputSetting=1;
									}
								}
								else{
									forcedSpellGroups.put(name, setting);
								}
							}
						}
					}catch(Exception e){
						if(outputSetting>1){
							log.log(Level.WARNING, "line "+lineNumber+" in the config file caused an error when being parsed: ");
							e.printStackTrace();
						}
					}
				}
				if(validCommands.size()>0){
					FileWriter writer=new FileWriter(config,true);
					for(String notThere:validCommands){
						writer.write(notThere+"\n");
					}
					writer.flush();
					writer.close();
				}
				filereader.close();
			} catch (FileNotFoundException e) {
			} catch (IOException e) {}
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
					castSpell(player,spell);
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
	public static boolean flavorTextEnabled(){
		return flavorText;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,String[] args) {
		if(command.getName().equalsIgnoreCase("spells")||command.getName().equalsIgnoreCase("s")){
			if(sender instanceof Player&&!((Player)sender).hasPermission("spells.commands"))sender.sendMessage("You do not have the required permissions to use spells commands!");
			String name="help";
			if(args.length>0){
				name=args[0];
				args=Arrays.copyOfRange(args, 1, args.length);
			}
			if(name.equalsIgnoreCase("info")){
				if(args.length!=0){
					Spell spell=spells.getSpell(combine(args));
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
								castSpell(player,(Spell)spellOrGroup);
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
						Spell spell=spells.getSpell(combine(args));
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
						Spell spell=spells.getSpell(combine(args));
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
	private String combine(String... strings){
		String result="";
		for(int i=0;i<strings.length-1;i++){
			result+=strings[i]+" ";
		}
		result+=strings[strings.length-1];
		return result;
	}
	public void castSpell(Player player, Spell spell){
		if(permissions==0||(permissions==1&&player.isOp())||(permissions==2&&player.hasPermission("spells."+spell.getGroup()+"."+spell.getName())))
			Bukkit.getServer().getPluginManager().callEvent(new SpellCastEvent(spell,player));
		else player.sendMessage("You do not have the required permissions to use this spell!");
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void spellCast(SpellCastEvent e){
		if(e.isCancelled())return;
		final Spell spell=e.getSpell();
		final Player player=e.getPlayer();
		final SpellBook spellBook=spellBooks.get(player);
		if(!spell.checkRequirements(e.getPlayer())){
			player.sendMessage(ChatColor.RED+"You cannot cast "+spell.getName()+", because you do not meet the requirements.");
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
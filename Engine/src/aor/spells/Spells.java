package aor.spells;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.PluginClassLoader;

public final class Spells extends JavaPlugin implements Listener{
	public static final Logger log = Logger.getLogger("Minecraft");
	private HashMap<Player,SpellBook> spellBooks=new HashMap<Player,SpellBook>();
	private Spell[] spells;
	static final Runner runner=new Runner();
	static int numberOfSpells;
	public void onDisable() {
		Bukkit.getServer().getScheduler().cancelTasks(this);
		runner.stop();
		spells=null;
		log.info("Spells 2.0 Disabled");
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
		if(spells==null||spells.length==0){
			log.warning("No Spells loaded!");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		numberOfSpells=spells.length;
		for(Spell spell:spells){
			log.info(spell.getName());
			getServer().getPluginManager().registerEvents(spell, this);
		}
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, runner, 1, 1);
		final Player[] players=Bukkit.getServer().getOnlinePlayers();
		for(int i=0;i<players.length;i++){
			spellBooks.put(players[i], new SpellBook());
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
		spells=new Spell[loadedSpells.size()];
		for(int i=0;i<spells.length;i++){
			spells[i]=loadedSpells.get(i);
		}
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void playerInteract(PlayerInteractEvent e){
		final Player player=e.getPlayer();
		if(player.getItemInHand().getType()==Material.GOLD_HOE){
			final Action action=e.getAction();
			if(action.equals(Action.LEFT_CLICK_AIR)||action.equals(Action.LEFT_CLICK_BLOCK)){
				e.setCancelled(true);
				Bukkit.getServer().getPluginManager().callEvent(new SpellCastEvent(spells[spellBooks.get(player).getCurrentSpellID()],player));
			}
			else if(action.equals(Action.RIGHT_CLICK_AIR)||action.equals(Action.RIGHT_CLICK_BLOCK)){
				e.setCancelled(true);
				spellBooks.get(player).nextSpell();
				player.sendMessage(ChatColor.BLUE+spells[spellBooks.get(player).getCurrentSpellID()].getName()+" selected");
			}
		}
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void spellCast(SpellCastEvent e){
		if(e.isCancelled())return;
		final Spell spell=e.getSpell();
		final Player player=e.getPlayer();
		if(!spell.checkRequirements(e.getPlayer())){
			e.setCancelled(true);
			return;
		}
		spell.removeRequirements(player);
		spell.cast(player);
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onLogin(PlayerLoginEvent e){
		spellBooks.put(e.getPlayer(), new SpellBook());
	}
	@EventHandler(priority=EventPriority.MONITOR)
	public void onLogoff(PlayerLoginEvent e){
		spellBooks.remove(e.getPlayer());
	}
}

package aor.spells;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.PluginClassLoader;

public class Spells extends JavaPlugin{
	public static Logger log = Logger.getLogger("Minecraft");
	private ArrayList<Spell> spells=new ArrayList<Spell>();
	public void onDisable() {
		log.info("Spells 2.0 Disabled");
	}
	public void onEnable() {
		loadSpells();
		if(spells.size()==0){
			log.warning("No Spells Loaded!");
			onDisable();
			return;
		}
		for(Spell spell:spells){
			log.info(spell.getName());
		}
		log.info("Spells 2.0 Enabled");
	}
	private void loadSpells(){
		File spelldir=new File("plugins/spells/");
		if(!spelldir.exists()){
			try {
				spelldir.mkdir();
				log.warning("no spells folder exists, so it was created.");
			}
			catch (Exception e) {
				log.warning("No spells folder exists and the plugin can't create it, because the directory is write protected");
			}
			return;
		}
		PluginClassLoader loader=(PluginClassLoader) Spells.class.getClassLoader();
		try{
			loader.addURL(new File("plugins/spells/").toURI().toURL());
		} catch(Throwable t){}
		for(File f:Arrays.asList(new File("plugins/spells/").listFiles())){
			if(f.getName().endsWith(".class")){
				try {
					Class<?> clazz = loader.loadClass(f.getName().replace(".class", ""));
					if(Spell.class.isInstance(clazz.newInstance())){
						Class<? extends Spell> s=clazz.asSubclass(Spell.class);
						spells.add(s.newInstance());
					}
					else if(SpellSet.class.isInstance(clazz.newInstance())){
						Class<? extends SpellSet> s=clazz.asSubclass(SpellSet.class);
						spells.addAll(s.newInstance().getSpells());
					}
				} catch (Throwable t) {}
			}
			else if(f.getName().endsWith(".jar")){
				try{
					loader.addURL(new URL(new URL("jar:"+f.toURI().toURL()+"!/"),""));
					Class<?> clazz = loader.loadClass(f.getName().replace(".jar", ""));
					if(Spell.class.isInstance(clazz.newInstance())){
						Class<? extends Spell> s=clazz.asSubclass(Spell.class);
						spells.add(s.newInstance());
					}
					else if(SpellSet.class.isInstance(clazz.newInstance())){
						Class<? extends SpellSet> s=clazz.asSubclass(SpellSet.class);
						spells.addAll(s.newInstance().getSpells());
					}
				} catch(Throwable t){
					t.printStackTrace();
				}
			}
		}
	}
}
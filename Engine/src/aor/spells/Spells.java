package aor.spells;

import java.io.File;
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
		for(Spell spell:spells){
			log.info(spell.getName());
		}
		log.info("Spells 2.0 Enabled");
	}
	private void loadSpells(){
		for(File f:Arrays.asList(new File("plugins/spells/").listFiles())){
			if(f.getName().endsWith(".class")){
				try {
					PluginClassLoader loader=(PluginClassLoader) Spells.class.getClassLoader();
					loader.addURL(new File("plugins/spells/").toURI().toURL());
					Class<?> clazz = loader.loadClass(f.getName().replace(".class", ""));
					Class<? extends Spell> s=clazz.asSubclass(Spell.class);
					spells.add(s.newInstance());
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}
}
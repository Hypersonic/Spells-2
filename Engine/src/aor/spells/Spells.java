package aor.spells;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class Spells extends JavaPlugin{
	Logger log = Logger.getLogger("Minecraft");
	ArrayList<Spell> spells=new ArrayList<Spell>();
	public void onDisable() {
		log.info("Spells 2.0 Disabled");
	}
	public void onEnable() {
		log.info("Spells 2.0 Enabled");
	}
}
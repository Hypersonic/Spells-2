package aor.spells;

import org.bukkit.entity.Player;

public abstract class Spell {
	public abstract String getName();
	public abstract String getDescription();	
	public abstract void cast();
	public abstract boolean checkRequirements(Player player);
}
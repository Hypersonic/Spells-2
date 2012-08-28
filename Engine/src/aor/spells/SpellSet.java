package aor.spells;

import java.util.Collection;

/**
 * This class should be extended to allow multiple spells to be put in one jar file, with the class extending spellset not being in a package and having the same name as the jar.
 * @author Jay
 */
public abstract class SpellSet {
	/**
	 * This method must be overriden in any spell set and is used to get the list of spells contained in the spellset
	 * @return - the collection of spells in the set
	 */
	public abstract Collection<Spell> getSpells();
}

import java.util.ArrayList;

import org.bukkit.entity.Player;

import aor.spells.Spell;
import aor.spells.SpellSet;

public class ExampleSpellSet extends SpellSet{
	public class ExampleSpell extends Spell {
		public ExampleSpell(){
			
		}
		@Override
		public String getName() {
			return "Example Spell";
		}
		@Override
		public String getDescription() {
			return "";
		}
		@Override
		public void cast(Player player) {
			
		}
		@Override
		public boolean checkRequirements(Player player) {
			return false;
		}
		@Override
		public void removeRequirements(Player player) {
			
		}
	}

	@Override
	public ArrayList<Spell> getSpells() {
		ArrayList<Spell> result=new ArrayList<Spell>();
		result.add(new ExampleSpell());
		return result;
	}
}
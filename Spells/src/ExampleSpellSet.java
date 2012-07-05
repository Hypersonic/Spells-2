
import java.util.ArrayList;

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
	}

	@Override
	public ArrayList<Spell> getSpells() {
		ArrayList<Spell> result=new ArrayList<Spell>();
		result.add(new ExampleSpell());
		return result;
	}
}
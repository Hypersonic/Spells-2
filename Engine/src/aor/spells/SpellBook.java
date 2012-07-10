package aor.spells;

public class SpellBook {
	private int current;
	public int getCurrentSpellID() {
		return current;
	}
	public void nextSpell(){
		current=current+1<Spells.numberOfSpells?current+1:0;
	}
}
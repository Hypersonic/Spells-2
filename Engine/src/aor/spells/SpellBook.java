package aor.spells;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @author Jay
 */
final class SpellBook {
	private static Method getMethod(){try{return SpellBook.class.getMethod("removeCooldown", Spell.class);}catch(Throwable t){throw new RuntimeException();}}
	private static final Method removeCooldown=getMethod();
	private int current=0;
	private SpellGroup currentGroup;
	private ArrayList<Spell> spellsWithCooldowns=new ArrayList<Spell>();
	public SpellBook(SpellGroup mainGroup){
		this.currentGroup=mainGroup;
	}
	public void next(){
		current=current+1<currentGroup.groupAndSpellSize()?current+1:0;
	}
	public void previous(){
		current=current-1<0?currentGroup.groupAndSpellSize():current-1;
	}
	public Object getCurrentSpellOrGroup() {
		return currentGroup.get(current);
	}
	public void goOutOfGroup() {
		final SpellGroup temp=currentGroup;
		currentGroup=currentGroup.getParent()==null?currentGroup:currentGroup.getParent();
		current=currentGroup!=temp?currentGroup.indexOf(temp):0;
	}
	public void goInGroup() {
		currentGroup=currentGroup.getChildren().get(current-currentGroup.spellSize());
		current=0;
	}
	public boolean hasCooldown(Spell spell) {
		return spellsWithCooldowns.contains(spell);
	}
	public void addCooldown(Spell spell) {
		spellsWithCooldowns.add(spell);
		Scheduler.schedule(spell.getCooldown(), this, removeCooldown, spell);
	}
	public void removeCooldown(Spell spell){
		spellsWithCooldowns.remove(spell);
	}
	public boolean hasParentGroup() {
		return currentGroup.getParent()!=null;
	}
	public void setGroup(SpellGroup group,int index) {
		currentGroup=group;
		current=index;
	}
}
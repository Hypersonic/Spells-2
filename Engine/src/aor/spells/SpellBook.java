package aor.spells;

public final class SpellBook {
	private int current;
	private SpellGroup currentGroup;
	public SpellBook(SpellGroup mainGroup){
		this.currentGroup=mainGroup;
	}
	public void nextSpell(){
		current=current+1<currentGroup.groupAndSpellSize()?current+1:0;
	}
	public Object getCurrentSpellOrGroup() {
		return currentGroup.get(current);
	}
	public void goOutOfGroup() {
		currentGroup=currentGroup.getParent()==null?currentGroup:currentGroup.getParent();
	}
	public void goInGroup() {
		currentGroup=currentGroup.getChildren().get(current-currentGroup.spellSize());
	}
}
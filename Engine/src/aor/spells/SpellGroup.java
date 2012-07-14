package aor.spells;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Pattern;

public class SpellGroup implements Collection<Spell>, Iterable<Spell>{
	private ArrayList<SpellGroup> children=new ArrayList<SpellGroup>();
	private SpellGroup parent;
	private String name;
	private ArrayList<Spell> spells=new ArrayList<Spell>();
	public SpellGroup(String name){
		this.name=name;
	}
	private SpellGroup(SpellGroup parent,String name){
		this.name=name;
		this.parent=parent;
	}
	public void addChild(String name){
		for(SpellGroup child:children)if(child.getName().equals(name))return;
		children.add(new SpellGroup(this,name));
	}
	@Override
	public int size() {
		int result=0;
		if(children!=null)for(SpellGroup child:children){
			result+=child.size();
		}
		result+=spells.size();
		return result;
	}
	@Override
	public boolean isEmpty() {
		return children.isEmpty()&&spells.isEmpty();
	}
	@Override
	public boolean contains(Object o) {
		if(!(o instanceof Spell))return false;
		for(SpellGroup child:children)if(child.contains(o))return true;
		return spells.contains(o);
	}
	@Override
	public Iterator<Spell> iterator() {
		return getSpells().iterator();
	}
	@Override
	public Object[] toArray() {
		return getSpells().toArray();
	}
	@Override
	public Object[] toArray(Object[] a) {
		return getSpells().toArray(a);
	}
	@Override
	public boolean add(Spell s) {
		return spells.add(s);
	}
	@Override
	public boolean remove(Object o) {
		if(!(o instanceof Spell))return false;
		if(spells.remove(o))return true;
		for(SpellGroup child:children)if(child.remove(o))return true;
		return false;
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		for(Object o:c){
			if(contains(o))return false;
		}
		return true;
	}
	@Override
	public boolean addAll(Collection<? extends Spell> c) {
		return spells.addAll(c);
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean result = false;
		for(SpellGroup child:children)result|=child.removeAll(c);
		return spells.removeAll(spells)||result;
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		boolean result = false;
		for(SpellGroup child:children)result|=child.retainAll(c);
		return spells.retainAll(c)||result;
	}
	@Override
	public void clear() {
		spells.clear();
		children.clear();
	}
	public ArrayList<SpellGroup> getChildren(){
		return children;
	}
	public SpellGroup getParent(){
		return parent;
	}
	public String getName(){
		return name;
	}
	private ArrayList<Spell> getSpells(){
		ArrayList<Spell> result=new ArrayList<Spell>();
		if(spells!=null)result.addAll(spells);
		for(SpellGroup child: children)result.addAll(child.getSpells());
		return result;
	}
	public void place(Spell spell,String group) {
		if(group.startsWith("."))group.replaceFirst(Pattern.quote("."), "");
		if(group.startsWith(getName()))group=group.replaceFirst(getName(), "");
		Scanner s=new Scanner(group);
		s.useDelimiter(Pattern.quote("."));
		System.out.println(spell.getName()+" ("+group+")");
		if(s.hasNext()){
			String str=s.next();
			System.out.println("ABC"+str);
			if(str.equals("")){
				spells.add(spell);
			}
			else{
				addChild(str);
				for(SpellGroup child:children){
					if(child.getName().equals(str)){
						child.place(spell, group);
						break;
					}
				}
			}
		}
		else {
			System.out.println("??? "+group);
			spells.add(spell);
		}
	}
	public int groupAndSpellSize() {
		return spells.size()+children.size();
	}
	public Object get(int current) {
		if(spells.size()<=current)return children.get(current-spells.size());
		return spells.get(current);
	}
	public int spellSize(){
		return spells.size();
	}
}
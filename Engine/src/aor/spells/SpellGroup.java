package aor.spells;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Level;

import org.bukkit.Bukkit;

public class SpellGroup implements Iterable<Spell>{
	private ArrayList<SpellGroup> children=new ArrayList<SpellGroup>();
	private SpellGroup parent;
	private String name;
	private ArrayList<Spell> spells=new ArrayList<Spell>();
	public SpellGroup(String name){
		this.name=name;
		Bukkit.getLogger().log(Level.INFO, name);
	}
	private SpellGroup(SpellGroup parent,String name){
		this.name=name;
		this.parent=parent;
	}
	public void addChild(String name){
		for(SpellGroup child:children)if(child.getName().equals(name))return;
		children.add(new SpellGroup(this,name));
	}
	public int size() {
		int result=0;
		if(children!=null)for(SpellGroup child:children){
			result+=child.size();
		}
		result+=spells.size();
		return result;
	}
	@Override
	public Iterator<Spell> iterator() {
		return getSpells().iterator();
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
		spells.add(spell);
		/*if(group.startsWith(getName()+"."))group=group.replaceFirst(getName()+".", "");
		System.out.println(group+" ("+name+")");
		if(group.contains(".")){
			String subgroupName=group.substring(0, group.indexOf("."));
			group=group.substring(group.indexOf(".")+1);
			System.out.println(subgroupName+" -- "+group);
			if(subgroupName.equals("")){
				spells.add(spell);
			}
			else{
				addChild(subgroupName);
				for(SpellGroup child:children){
					if(child.getName().equals(subgroupName)){
						child.place(spell, group);
						break;
					}
				}
			}
		}
		else if (group.length()==0){
			spells.add(spell);
		}
		else{
			addChild(group);
			for(SpellGroup child:children){
				if(child.getName().equals(group)){
					child.place(spell, "");
					break;
				}
			}
		}*/
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
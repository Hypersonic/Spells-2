package aor.spells;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

public class SpellLoader extends URLClassLoader {
	private static URL[] getURL(){
		try {
			return new URL[]{new File("/plugins/spells").toURI().toURL()};
		} catch (MalformedURLException e){
			throw new RuntimeException("This should never appear!!!");
		}
	}
	public SpellLoader() {
		super(getURL());
	}
	public ArrayList<Spell> loadSpells(){
		ArrayList<Spell> result=new ArrayList<Spell>();
		for(File f:new File("/plugins/spells").listFiles()){
			if(f.getName().endsWith(".class")){
				try {
					Class<?> clazz=super.loadClass(f.getName());
					Class<? extends Spell> spell=clazz.asSubclass(Spell.class);
					result.add(spell.getConstructor().newInstance());
				} catch (Exception e) {}
			}
		}
		return result;
	}
}

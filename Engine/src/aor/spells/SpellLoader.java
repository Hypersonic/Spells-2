package aor.spells;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
@Deprecated
public class SpellLoader{
	URLClassLoader loader;
	/*private static URL[] getURL(){
		try {
			return new URL[]{new File("plugins/spells/").toURI().toURL()};
		} catch (MalformedURLException e){
			throw new RuntimeException("This should never appear!!!");
		}
	}*/
	
	public SpellLoader() {
		//super(getURL());
		try {
			loader=new URLClassLoader(new URL[]{new File("plugins/spells/").toURI().toURL()});
		} catch (MalformedURLException e) {
			throw new RuntimeException("This should never appear!!!");
		}
	}
	public ArrayList<Spell> loadSpells(){
		ArrayList<Spell> result=new ArrayList<Spell>();
		File[] files=new File("plugins/spells/").listFiles();
		for(int i=0;i<files.length;i++){
			File f=files[i];
			if(f.getName().endsWith(".class")){
				try {
					Class<?> clazz=loader.loadClass(f.getName().replace(".class", ""));
					Class<? extends Spell> spell=clazz.asSubclass(Spell.class);
					result.add(spell.getConstructor().newInstance());
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
		return result;
	}
}

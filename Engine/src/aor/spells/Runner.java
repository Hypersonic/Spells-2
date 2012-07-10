package aor.spells;

import java.util.ArrayList;
import java.lang.Runnable;

import org.bukkit.Bukkit;

public class Runner implements Runnable{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	/*public ArrayList<ArrayList<Object[]>> main=new ArrayList<ArrayList<Object[]>>(0);
	public ArrayList<ArrayList<Integer>> spellId=new ArrayList<ArrayList<Integer>>(0);
	private boolean stop=false;
	public Runner(){}
	public void run(){
		for(int i=0;i<plugin.playersWithCooldowns.size();i++){
			for(int i2=0;i2<plugin.spellList.size();i2++){
				plugin.cooldowns.get(plugin.playersWithCooldowns.get(i))[i2]--;
			}
		}
		if(main.size()==0){
			main.add(new ArrayList<Object[]>(0));
			spellId.add(new ArrayList<Integer>(0));
		}
		for(int i=0;i<main.get(0).size();i++){
			plugin.spellList.get(spellId.get(0).get(i)).run(main.get(0).get(i));
		}
		main.remove(0);
		spellId.remove(0);
		if(stop){
			while(main.size()>0){
				for(int i=0;i<main.get(0).size();i++){
					plugin.spellList.get(spellId.get(0).get(i)).run(main.get(0).get(i));
				}
				main.remove(0);
			}
		}
		else{
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin,this,1L);
		}
	}
	public void stop(){
		stop=true;
	}*/
}
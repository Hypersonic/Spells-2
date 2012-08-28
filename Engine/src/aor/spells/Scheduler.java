package aor.spells;

import java.util.ArrayList;
import java.util.LinkedList;
import java.lang.Runnable;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;

/**
 * This class controls all scheduling for Spells. Usually  you can just use the methods in Spell for scheduling, but you can schedule stuff directly though this class' schedule method, which allows ou to choose the object for the method to be invoked on.
 * @author Jay
 */
public final class Scheduler{
	private static final Runner runner=new Scheduler().new Runner();
	/**
	 * This method gives you more flexibility in scheduling tasks than the version in Spell. It allows you to have whichever method you like called on any object, instead of just a spell class. See schedule in spell for more information.
	 * @param ticks - the number of ticks to wait before calling the method.
	 * @param object - the object to have the method invoked upon.
	 * @param method - the method to be called after the number of ticks
	 * @param args - the arguments to be passed to the method.
	 */
	public static void schedule(int ticks,Object object,Method method, Object... args){
		runner.schedule(ticks, object, method, args);
	}
	static void start(Spells spells){
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(spells, runner, 1, 1);
	}
	static void stop(Spells spells){
		Bukkit.getServer().getScheduler().cancelTasks(spells);
		runner.stop();
	}
	private final class Runner implements Runnable{
		private LinkedList<ArrayList<RunData>> data=new LinkedList<ArrayList<RunData>>();
		public Runner(){}
		@Override
		public void run() {
			if(data.size()>0){
				final ArrayList<RunData> currentData=data.remove();
				if(currentData==null)return;
				for(RunData data:currentData){
					final Object spell=data.getSpell();
					final Method method=data.getMethod();
					final Object[] params=data.getParams();
					if(method==null){
						if(spell instanceof Spell)((Spell)spell).run(params);
					}
					else{
						try {
							method.invoke(spell, params);
						} catch (Exception e) {assert false:e;}
					}
				}
			}
		}
		public void stop(){
			while(data.size()>0)run();
		}
		private class RunData{
			private Object spell;
			private Method method;
			private Object[] params;
			public RunData(Object spell,Method method,Object[] params){
				this.spell=spell;
				this.method=method;
				this.params=params;
			}
			public Object getSpell(){
				return spell;
			}
			public Method getMethod(){
				return method;
			}
			public Object[] getParams(){
				return params;
			}
		}
		public void schedule(int ticks,Object spell,Method method, Object... args){
			if(data.size()-1<ticks){
				while(data.size()-1<ticks){
					data.add(null);
				}
			}
			if(data.get(ticks)==null)data.set(ticks, new ArrayList<RunData>(1));
			data.get(ticks).add(new RunData(spell, method, args));
		}
	}
}
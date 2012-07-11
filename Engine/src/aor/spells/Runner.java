package aor.spells;

import java.util.ArrayList;
import java.lang.Runnable;
import java.lang.reflect.Method;

public class Runner implements Runnable{
	private ArrayList<ArrayList<RunData>> data=new ArrayList<ArrayList<RunData>>();
	@Override
	public void run() {
		if(data.size()>0){
			final ArrayList<RunData> currentData=data.remove(0);
			if(currentData==null)return;
			for(RunData data:currentData){
				final Spell spell=data.getSpell();
				final Method method=data.getMethod();
				final Object[] params=data.getParams();
				if(method==null){
					spell.run(params);
				}
				else{
					try {
						method.invoke(spell, params);
					} catch (Exception e) {}
				}
			}
		}
	}
	public void stop(){
		while(data.size()>0)run();
	}
	public void schedule(int millis,Spell spell,Method method, Object... args){
		while(data.size()<millis){
			data.add(null);
		}
		if(data.get(millis)==null)data.set(millis, new ArrayList<RunData>(1));
		data.get(millis).add(new RunData(spell, method, args));
	}
	private class RunData{
		private Spell spell;
		private Method method;
		private Object[] params;
		public RunData(Spell spell,Method method,Object[] params){
			this.spell=spell;
			this.method=method;
			this.params=params;
		}
		public Spell getSpell(){
			return spell;
		}
		public Method getMethod(){
			return method;
		}
		public Object[] getParams(){
			return params;
		}
	}
}
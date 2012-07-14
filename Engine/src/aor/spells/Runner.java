package aor.spells;

import java.util.ArrayList;
import java.util.LinkedList;
import java.lang.Runnable;
import java.lang.reflect.Method;

public class Runner implements Runnable{
	private LinkedList<ArrayList<RunData>> data=new LinkedList<ArrayList<RunData>>();
	@Override
	public void run() {
		if(data.size()>0){
			final ArrayList<RunData> currentData=data.remove();
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
					} catch (Exception e) {assert false:"FAILLL";}
				}
			}
		}
	}
	public void stop(){
		while(data.size()>0)run();
	}
	public void schedule(int ticks,Spell spell,Method method, Object... args){
		if(data.size()-1<ticks){
			while(data.size()-1<ticks){
				data.add(null);
			}
		}
		if(data.get(ticks)==null)data.set(ticks, new ArrayList<RunData>(1));
		data.get(ticks).add(new RunData(spell, method, args));
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
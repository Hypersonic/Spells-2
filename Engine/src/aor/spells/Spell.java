package aor.spells;

import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class Spell implements Listener {
	/**
	 * Every Spell must override this method. It returns the Spell's name.
	 * @return String - the Spell's name. May not be null, the empty string ("") or the name of another spell.
	 */
	public abstract String getName();
	/**
	 * Every Spell must override this method. It returns the Spell's description.
	 * @return String - the Spell's description. While it may be null, it is recommended that spellmakers return something helpful.
	 */
	public abstract String getDescription();
	/**
	 * Every Spell must override this method, because it is where a spell actually gets cast. You should not check whether or not the player can cast the spell, because that should be done in checkRequirements and cast will only be called if checkRequiremnts returns true. You also shouldn't remove requirements, because that should be done in removeRequirements, which will already have been called.
	 * @param player - the player casting the spell.
	 */
	public abstract void cast(Player player);
	/**
	 * Every Spell must override this method. It returns a string to be sent to the player displaying all of the requirements of a spell.
	 * @return the requirements
	 */
	public abstract String getRequirements();
	/**
	 * This method does not need to be overriden, but it is probably a good idea to override this method. This method returns the group the spell will be placed in by Spells. The syntax for spell groups is groupname1.groupname2...groupnamen. For more information about spell groups, see the wiki at google.com.
	 * @return String - the name of the spellgroup you would like the spell to be placed in.
	 */
	public String getGroup(){
		return "";
	}
	public boolean checkRequirements(Player player){
		return true;
	}
	public void removeRequirements(Player player){}
	public void schedule(int ticks,Method m,Object... args){
		Scheduler.schedule(ticks, this, m, args);
	}
	public void schedule(int ticks,Object... args){
		Scheduler.schedule(ticks, this, null, args);
	}
	public void run(Object... args){}
	public int getCooldown(){
		return 0;
	}
	protected Method getMethod(String name,Class<?>... argClasses){
		try{
			return getClass().getMethod(name,argClasses);
		}
		catch(Exception e){
			throw new RuntimeException("Spell "+getName()+" doesn't have a method called "+name+"!");
		}
	}
}
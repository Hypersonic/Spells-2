package aor.spells;

import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 * This is the class that all spells must extend.
 * Some of the methods must be overriden, others are optional and others are just helpful methods, although you should take a look at SpellUtils for more useful stuff.
 * @author Jay
 */
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
	/**
	 * This method may be overriden to change the message players receive when a spell cannot be cast yet, because it still has a cooldown. By default it just returns a brief message with the cooldown time of the spell. 
	 * @return String - the message to be returned
	 */
	public String getCooldownMessage(){
		return "You cannot cast, because theis spell has a cooldown of "+getCooldown()/20.0+" seconds.";
	}
	/**
	 * It is recommended that you override this method, although not required. This method should check to make sure the player has everything required to cast the spell. This includes items, in addition to other thigns such as whether or not the player has a target.
	 * @param player - The player that tried to cast the spell.
	 * @return - whether or not the player meets the requirements to cast the spell.
	 */
	public boolean checkRequirements(Player player){
		return true;
	}
	/**
	 * You should override this method, if you override checkRequirements. It is rarely useful otherwise. This method will only be called after checkRequirements returns true. It should be used to remove items from the inventory of the player etc.
	 * @param player - the player to remove the requirements from the player
	 */
	public void removeRequirements(Player player){}
	/**
	 * Schedules a delayed task using the spells scheduler. The advantage of using the spells scheduler over the bukkit scheduler is that everything that is scheduled will be run early, if the plugin is being disabled. Note that if you use the scheduler directly, you can schedule methods to be run on objects other than your spell. See schedule in scheduler, if you want to choose which object the method is invoked upon.
	 * @param ticks - the number of ticks (20 ticks/second) to wait before calling the method passed
	 * @param method - the method to be called after the delay. For an easy way to get the method object, see getMethod.
	 * @param args - the arguments to be passed to the method
	 */
	public void schedule(int ticks,Method method,Object... args){
		Scheduler.schedule(ticks, this, method, args);
	}
	/**
	 * the same as the other schedule, except it calls the default run method, instead of a passed method.
	 * @param ticks - the number of ticks (20 ticks/second) to wait before calling the method passed
	 * @param args - the arguments to be passed to run
	 */
	public void schedule(int ticks,Object... args){
		Scheduler.schedule(ticks, this, null, args);
	}
	/**
	 * run when the schedule method that does not take a method for an argument is called. It can be used for whatever schduled tasks you want.
	 * @param args - the arguments passed to schedule
	 */
	public void run(Object... args){}
	/**
	 * An optional method that can be overriden to give spells a cooldown of a certain number of ticks.
	 * @return int - the number of ticks (20 ticks/second)
	 */
	public int getCooldown(){
		return 0;
	}
	/**
	 * This method provides an easy way for spells to get method objects (this only works with methods in the spell class)
	 * @param name -  the name of the method
	 * @param argClasses - the classes of the arguments (ex. getMethod("methodName",Player.class,ItemStack.class))
	 * @return Method - the method object
	 */
	protected Method getMethod(String name,Class<?>... argClasses){
		try{
			return getClass().getMethod(name,argClasses);
		}
		catch(Exception e){
			throw new RuntimeException("Spell "+getName()+" doesn't have a method called "+name+"!");
		}
	}
}
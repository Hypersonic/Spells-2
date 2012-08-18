package aor.spellstexturepatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
	public static boolean nogui;
	public static ArrayList<String> zips= new ArrayList<String>();
	public static void main(String[] arguments) {
		List<String> args=Arrays.asList(arguments);
		for(String arg:args){
			if(arg.equalsIgnoreCase("nogui"))nogui=true;
			if(arg.endsWith(".zip")||arg.endsWith("minecraft.jar")){
				
			}
		}
	}
}

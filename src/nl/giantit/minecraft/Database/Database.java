package nl.giantit.minecraft.Database;

import nl.giantit.minecraft.Database.drivers.MySQL;
import nl.giantit.minecraft.Database.drivers.SQLite;
import nl.giantit.minecraft.Database.drivers.hTwo;
import nl.giantit.minecraft.Database.drivers.iDriver;

import org.bukkit.plugin.Plugin;

import java.util.HashMap;

/**
 *
 * @author Giant
 */
public class Database {
	
	private enum dbType {
		MySQL("MySQL"),
		hTwo("h2"),
		SQLite("SQLite");
		
		String value;
		
		private dbType(String s) {
			this.value = s;
		}
		
		@Override
		public String toString() {
			return this.value;
		}
	}
	
	private static HashMap<String, Database> instance = new HashMap<String, Database>();
	private iDriver dbDriver;
	private dbType t;
	
	private Database(Plugin p, HashMap<String, String> conf, String instance) {
		if(instance == null)
			instance = "0";
		
		String d = conf.get("driver");
		
		if(d.equalsIgnoreCase("MySQL")) {
			t = dbType.MySQL;
			this.dbDriver = MySQL.Obtain(p, conf, instance);
		}else if(d.equalsIgnoreCase("h2")) {
			t = dbType.hTwo;
			this.dbDriver = hTwo.Obtain(p, conf, instance);
		}else{
			t = dbType.SQLite;
			this.dbDriver = SQLite.Obtain(p, conf, instance);
		}
	}
	
	public iDriver getEngine() {
		return this.dbDriver;
	}
	
	public String getType() {
		return t.toString();
	}
	
	public static Database Obtain() {
		String instance = "0";
		
		if(!Database.instance.containsKey(instance))
			return null;
		
		return Database.instance.get(instance);
	}
	
	public static Database Obtain(String instance) {
		if(instance == null)
			instance = "0";
		
		if(!Database.instance.containsKey(instance))
			return null;
		
		return Database.instance.get(instance);
	}
	
	public static Database Obtain(Plugin p, String instance, HashMap<String, String> conf) {
		if(instance == null)
			instance = "0";
		
		if(!Database.instance.containsKey(instance))
			Database.instance.put(instance, new Database(p, conf, instance));
		
		return Database.instance.get(instance);
	}
}

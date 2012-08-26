package nl.giantit.minecraft.dbExample;

import nl.giantit.minecraft.Database.Database;
import nl.giantit.minecraft.Database.drivers.iDriver;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class dbExample extends JavaPlugin {

	private Database dbDriver;
	
	
	@Override
	public void onEnable() {
		if(!getDataFolder().exists()) {
			getDataFolder().mkdir();
			getDataFolder().setWritable(true);
			getDataFolder().setExecutable(true);
		}
		
		HashMap<String, String> dbData = new HashMap<String, String>();
		dbData.put("driver", "SQLite");
		dbData.put("database", "dbExample");
		dbData.put("prefix", "basic_");
		dbData.put("debug", "true");
		
		this.dbDriver = Database.Obtain(this, null, dbData);
		
		new InitDatabase(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(args.length == 0) {
			Boolean isLucky;
			
			HashMap<String, String> where = new HashMap<String, String>();
			where.put("player", sender.getName());
			
			ArrayList<HashMap<String, String>> resSet = this.dbDriver.getEngine().select("lucky").from("#__playerData").where(where).execQuery();
			if(resSet.size() > 0) {
				HashMap<String, String> res = resSet.get(0);
				isLucky = res.get("lucky").equals("1");
			}else{
				Random rand = new Random();
				int x = rand.nextInt(10) > 5 ? 1 : 0;

				ArrayList<String> fields = new ArrayList<String>();
				fields.add("player");
				fields.add("lucky");
				
				HashMap<Integer, HashMap<String, String>> values = new HashMap<Integer, HashMap<String, String>>();
				
				for(int i = 0; i < fields.size(); i++) {
					HashMap<String, String> value = new HashMap<String, String>();
					String field = fields.get(i);
					if(field.equalsIgnoreCase("player")) {
						value.put("data", sender.getName());
					}else if(field.equalsIgnoreCase("lucky")) {
						value.put("kind", "INT");
						value.put("data", String.valueOf(x));
					}
					
					values.put(i, value);
				}
				
				this.dbDriver.getEngine().insert("#__playerData", fields, values).Finalize().updateQuery();
				
				isLucky = x == 1;
			}
			
			if(isLucky) {
				sender.sendMessage("You are lucky!");
			}else{
				sender.sendMessage("Sorry, you are not lucky! :(");
			}
			
			if(sender instanceof Player) {
				sender.sendMessage("type /dbExample reset to try again!");
			}else{
				sender.sendMessage("type dbExample reset to try again!");
			}
		}else{
			HashMap<String, String> where = new HashMap<String, String>();
			where.put("player", sender.getName());
			
			this.dbDriver.getEngine().delete("#__playerData").where(where).updateQuery();
			sender.sendMessage("Your luckyness has been reset!");
		}
					
		
		return true;
	}
	
	public iDriver getDb() {
		return this.dbDriver.getEngine();
	}
}

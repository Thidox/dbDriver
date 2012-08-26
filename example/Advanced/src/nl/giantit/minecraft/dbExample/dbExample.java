package nl.giantit.minecraft.dbExample;

import nl.giantit.minecraft.Database.Database;
import nl.giantit.minecraft.Database.drivers.iDriver;
import nl.giantit.minecraft.dbExample.Listeners.BlockListener;
import nl.giantit.minecraft.dbExample.Listeners.PlayerListener;

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
		
		config c = new config(this);
		
		HashMap<String, String> dbData = new HashMap<String, String>();
		dbData.put("driver", c.getString("Advanced.db.driver"));
		dbData.put("database", c.getString("Advanced.db.database"));
		dbData.put("prefix", c.getString("Advanced.db.prefix"));
		
		// MySQL stuff
		dbData.put("host", c.getString("Advanced.db.host"));
		dbData.put("port", c.getString("Advanced.db.port"));
		dbData.put("user", c.getString("Advanced.db.user"));
		dbData.put("password", c.getString("Advanced.db.password"));
		
		// Should be print out database errors?
		dbData.put("debug", c.getString("Advanced.db.debug"));
		
		// For this example we will use a different instance name for defining our instance.
		// This means however that we can no longer do Database.Obtain();
		// But are now instead bound to Database.Obtain(String s); or in this case: Database.Obtain("myLittleInstance");
		this.dbDriver = Database.Obtain(this, "myLittleInstance", dbData);
		
		new InitDatabase(this);
		
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		getServer().getPluginManager().registerEvents(new BlockListener(this), this);
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

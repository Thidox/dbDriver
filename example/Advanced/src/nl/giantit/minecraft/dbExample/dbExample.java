package nl.giantit.minecraft.dbExample;

import nl.giantit.minecraft.Database.Database;
import nl.giantit.minecraft.Database.iDriver;
import nl.giantit.minecraft.dbExample.Listeners.BlockListener;
import nl.giantit.minecraft.dbExample.Listeners.PlayerListener;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import nl.giantit.minecraft.Database.QueryResult;
import nl.giantit.minecraft.Database.QueryResult.QueryRow;

public class dbExample extends JavaPlugin {

	private Database dbDriver;
	
	private HashMap<String, Integer> getPlayerData(Player p) {
		iDriver db = this.dbDriver.getEngine();
		HashMap<String, Integer> data = new HashMap<String, Integer>();
		
		HashMap<String, String> where = new HashMap<String, String>();
		where.put("player", p.getName());
		
		QueryResult resSet = db.select("timesClicked", "blocksBroken", "movesMade").from("#__playerData").where(where).execQuery();
		
		if(resSet.size() > 0) {
			QueryRow res = resSet.getRow();
			data.put("timesClicked", res.getInteger("timesvlicked"));
			data.put("blocksBroken", res.getInteger("blocksbroken"));
			data.put("movesMade", res.getInteger("movesmade"));
			
			return data;
		}
		
		// Player not found!
		return null;
	}
	
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
	public void onDisable() {
		// Properly close current connection!
		this.dbDriver.getEngine().close();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Sorry, this command only works as a player! :(");
			return true;
		}
		
		Player p = (Player) sender;
		
		if(args.length == 0) {
			HashMap<String, Integer> data = this.getPlayerData(p);
			if(null != data) {
				sender.sendMessage("You have moved " + String.valueOf(data.get("movesMade")) + " times!");
				sender.sendMessage("You have clicked " + String.valueOf(data.get("timesClicked")) + " times!");
				sender.sendMessage("You have broken " + String.valueOf(data.get("blocksBroken")) + " blocks!");
				
				sender.sendMessage("Type /dbExample reset to reset your data!");
			}else{
				sender.sendMessage("Sorry, no data for you yet, try moving around!");
			}
		}else{
			HashMap<String, String> where = new HashMap<String, String>();
			where.put("player", sender.getName());
			
			this.dbDriver.getEngine().delete("#__playerData").where(where).updateQuery();
			sender.sendMessage("Your statistics have been reset!");
		}
					
		
		return true;
	}
	
	public iDriver getDb() {
		return this.dbDriver.getEngine();
	}
}

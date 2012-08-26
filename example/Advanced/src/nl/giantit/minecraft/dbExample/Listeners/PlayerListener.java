package nl.giantit.minecraft.dbExample.Listeners;

import nl.giantit.minecraft.Database.drivers.iDriver;
import nl.giantit.minecraft.dbExample.dbExample;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerListener implements Listener {

	private dbExample plugin;
	private iDriver db;
	
	public PlayerListener(dbExample plugin) {
		this.plugin = plugin;
		this.db = plugin.getDb();
	}
	
	private Boolean checkPlayerExists(Player p) {
		HashMap<String, String> where = new HashMap<String, String>();
		where.put("player", p.getName());
		
		ArrayList<HashMap<String, String>> resSet = db.select("id").from("#__playerData").where(where).execQuery();
		
		return resSet.size() == 1;
	}
	
	private void updateState(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		
		if(!this.checkPlayerExists(p)) {
			ArrayList<String> fields = new ArrayList<String>();
			fields.add("player");
			fields.add("timesClicked");
			
			HashMap<Integer, HashMap<String, String>> values = new HashMap<Integer, HashMap<String, String>>();
			for(int i = 0; i < fields.size(); i++) {
				HashMap<String, String> value = new HashMap<String, String>();
				String field = fields.get(i);
				if(field.equalsIgnoreCase("player")) {
					value.put("data", p.getName());
				}else if(field.equalsIgnoreCase("timesClicked")) {
					value.put("kind", "INT");
					value.put("data", "1");
				}
				
				values.put(i, value);
			}
			
			db.insert("#__playerData", fields, values);
			
			// We are going to have to insert!
		}else{
			HashMap<String, HashMap<String, String>> fields = new HashMap<String, HashMap<String, String>>();
			HashMap<String, String> data = new HashMap<String, String>();
			
			data.put("kind", "INT"); // Hacky way of making it raw
			data.put("data", "timesClicked + 1"); // Make the database increment the current value of column timesClicked by 1
			fields.put("timesClicked", data);
			
			db.update("#__playerData").set(fields, true);
			// We can update!
		}
	}
	
	private void updateState(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		
		if(!this.checkPlayerExists(p)) {
			ArrayList<String> fields = new ArrayList<String>();
			fields.add("player");
			fields.add("stepsMade");
			
			HashMap<Integer, HashMap<String, String>> values = new HashMap<Integer, HashMap<String, String>>();
			for(int i = 0; i < fields.size(); i++) {
				HashMap<String, String> value = new HashMap<String, String>();
				String field = fields.get(i);
				if(field.equalsIgnoreCase("player")) {
					value.put("data", p.getName());
				}else if(field.equalsIgnoreCase("stepsMade")) {
					value.put("kind", "INT");
					value.put("data", "1");
				}
				
				values.put(i, value);
			}
			
			db.insert("#__playerData", fields, values);
			
			// We are going to have to insert!
		}else{
			HashMap<String, HashMap<String, String>> fields = new HashMap<String, HashMap<String, String>>();
			HashMap<String, String> data = new HashMap<String, String>();
			
			data.put("kind", "INT"); // Hacky way of making it raw
			data.put("data", "stepsMade + 1"); // Make the database increment the current value of column stepsMade by 1
			fields.put("stepsMade", data);
			
			db.update("#__playerData").set(fields, true);
			// We can update!
		}
	}

	// MONITOR because we are only monitoring the event.
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(final PlayerInteractEvent e) {
		// We don't want to work with a cancelled event!
		if(e.isCancelled())
			return;
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				updateState(e);
			}
		});
	}
	
	// MONITOR because we are only monitoring the event.
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(final PlayerMoveEvent e) {
		// We don't want to work with a cancelled event!
		if(e.isCancelled())
			return;
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				updateState(e);
			}
		});
	}
}

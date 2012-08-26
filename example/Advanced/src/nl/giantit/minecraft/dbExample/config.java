package nl.giantit.minecraft.dbExample;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public class config {

	private Plugin p;
	FileConfiguration c;
	
	
	public config(Plugin p) {
		this.p = p;
		this.load();
	}
	
	public void load() {
		c = p.getConfig();
		if(!c.getBoolean("Advanced.installed", false)) {
			InputStream d = p.getResource("nl/giantit/minecraft/dbExample/config.yml");
			if(d != null) {
				c.setDefaults(YamlConfiguration.loadConfiguration(d));
			}
			
			try {
				c.save(new File(p.getDataFolder(), "config.yml"));
				c.options().copyDefaults(true);
				p.saveConfig();
				p.getLogger().log(Level.INFO, "Saved default config file!");
			}catch(IOException e) {
				p.getLogger().log(Level.SEVERE, "Failed to create config file!", e);
			}
		}
	}
	
	public String getString(String p) {
		return c.getString(p);
	}
	
	public String getString(String p, String d) {
		return c.getString(p, d);
	}
}

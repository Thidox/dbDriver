package nl.giantit.minecraft.dbExample;

import nl.giantit.minecraft.Database.drivers.iDriver;

import java.util.ArrayList;
import java.util.HashMap;

public class InitDatabase {

	private dbExample p;
	
	public InitDatabase(dbExample p) {
		this.p = p;
		
		this.init();
	}
	
	public void init() {
		iDriver db = this.p.getDb();
		
		if(!db.tableExists("#__versions")) {
			// Create table #__versions
			// (tableName VARCHAR(100) NOT NULL,
			// version DOUBLE NOT NULL DEFAULT '1.0');
			
			HashMap<String, HashMap<String, String>> fields = new HashMap<String, HashMap<String, String>>();
			
			HashMap<String, String> data = new HashMap<String, String>();
			
			data.put("TYPE", "VARCHAR");
			data.put("LENGTH", "100");
			data.put("NULL", "false");
			
			fields.put("tableName", data);
			
			data = new HashMap<String, String>();
			data.put("TYPE", "DOUBLE");
			data.put("LENGTH", null);
			data.put("NULL", "false");
			data.put("DEFAULT", "1.0");
			
			fields.put("version", data);
			
			// Finalize so no more changes possible.
			db.create("#__versions").fields(fields).Finalize().updateQuery();
			
			p.getLogger().info("Revisions table successfully created!");
		}
		
		if(!db.tableExists("#__playerData")) {
			ArrayList<String> field = new ArrayList<String>();
			field.add("tablename");
			field.add("version");
			
			HashMap<Integer, HashMap<String, String>> d = new HashMap<Integer, HashMap<String, String>>();
			HashMap<String, String> data = new HashMap<String, String>();
			
			data.put("data", "playerData");
			d.put(0, data);
			
			data = new HashMap<String, String>();
			data.put("data", "1.0");
			d.put(1, data);
			
			db.insert("#__versions", field, d).Finalize().updateQuery();
			
			HashMap<String, HashMap<String, String>> fields = new HashMap<String, HashMap<String, String>>();
			
			data = new HashMap<String, String>();
			data.put("TYPE", "INT"); // Internally converted to INTEGER for SQLite
			data.put("LENGTH", "3");
			data.put("A_INCR", "true"); // AUTO_INCREMENT
			data.put("P_KEY", "true"); // PRIMARY KEY
			data.put("NULL", "false");
			
			fields.put("id", data);
			
			data = new HashMap<String, String>();
			data.put("TYPE", "VARCHAR");
			data.put("LENGTH", "100");
			data.put("NULL", "false");
			
			fields.put("player", data);
			
			data = new HashMap<String, String>();
			data.put("TYPE", "INT"); // Internally converted to INTEGER for SQLite
			data.put("LENGTH", "5");			
			data.put("NULL", "false");
			data.put("DEFAULT", "0");
			
			fields.put("blocksBroken", data);
			
			data = new HashMap<String, String>();
			data.put("TYPE", "INT"); // Internally converted to INTEGER for SQLite
			data.put("LENGTH", "5");			
			data.put("NULL", "false");
			data.put("DEFAULT", "0");
			
			fields.put("stepsMade", data);
			
			data = new HashMap<String, String>();
			data.put("TYPE", "INT"); // Internally converted to INTEGER for SQLite
			data.put("LENGTH", "5");			
			data.put("NULL", "false");
			data.put("DEFAULT", "0");
			
			fields.put("timesClicked", data);
			
			db.create("#__playerData").fields(fields).Finalize().updateQuery();
			
			p.getLogger().info("Player data table successfully created!");
		}
	}
}

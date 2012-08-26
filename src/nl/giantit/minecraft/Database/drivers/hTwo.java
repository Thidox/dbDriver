package nl.giantit.minecraft.Database.drivers;

import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public class hTwo implements iDriver {
	
	private static HashMap<String,  hTwo> instance = new HashMap<String,  hTwo>();
	private Plugin plugin;
	
	private ArrayList<HashMap<String, String>> sql = new ArrayList<HashMap<String, String>>();
	private ArrayList<ResultSet> query = new ArrayList<ResultSet>();
	private int execs = 0;
	
	private String cur, db, host, port, user, pass, prefix;
	private Connection con = null;
	private Boolean dbg = false;
	
	private Boolean parseBool(String s, Boolean d) {
		if(s.equalsIgnoreCase("1") || s.equalsIgnoreCase("y") || s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("true"))
			return true;
		
		return d;
	}

	private hTwo(Plugin p, HashMap<String, String> c) {
		plugin = p;
		
		this.db = c.get("database");
		this.prefix = c.get("prefix");
		this.user = c.get("user");
		this.pass = c.get("password");
		this.dbg = (c.containsKey("debug")) ? this.parseBool(c.get("debug"), false) : false;

		String dbPath = "jdbc:h2:" + plugin.getDataFolder() + java.io.File.separator + this.db;
		try{
			Class.forName("org.h2.Driver");
			this.con = DriverManager.getConnection(dbPath, this.user, this.pass);
		}catch(SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to connect to database: SQL error!");
			if(this.dbg) {
				plugin.getLogger().log(Level.INFO, e.getMessage());
				e.printStackTrace();
			}
		}catch(ClassNotFoundException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to connect to database: SQLite library not found!");
			if(this.dbg) {
				plugin.getLogger().log(Level.INFO, e.getMessage());
				e.printStackTrace();
			}
		}
	}

	@Override
	public void close() {
		try {
			if(!con.isClosed() || !con.isValid(0))
				return;
			
			this.con.close();
		}catch(SQLException e) {
			//ignore
		}
	}

	@Override
	public boolean tableExists(String table) {
		ResultSet res = null;
		table = table.replace("#__", prefix);
		
		try {
			DatabaseMetaData data = this.con.getMetaData();
			res = data.getTables(null, null, table, null);

			return res.next();
		}catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, " Could not load table " + table);
			if(this.dbg) {
				plugin.getLogger().log(Level.INFO, e.getMessage());
				e.printStackTrace();
			}
            return false;
		} finally {
			try {
				if(res != null) {
					res.close();
				}
			}catch (Exception e) {
				plugin.getLogger().log(Level.SEVERE, " Could not close result connection to database");
				if(this.dbg) {
					plugin.getLogger().log(Level.INFO, e.getMessage());
					e.printStackTrace();
				}
				return false;
			}
		}
	}

	@Override
	public void buildQuery(String string) {
		this.buildQuery(string, false);
	}

	@Override
	public void buildQuery(String string, Boolean add) {
		this.buildQuery(string, add, false);
	}

	@Override
	public void buildQuery(String string, Boolean add, Boolean finalize) {
		this.buildQuery(string, add, finalize, false);
	}

	@Override
	public void buildQuery(String string, Boolean add, Boolean finalize, Boolean debug) {
		this.buildQuery(string, add, finalize, debug, false);
	}

	@Override
	public void buildQuery(String string, Boolean add, Boolean finalize, Boolean debug, Boolean table) {
		
	}

	@Override
	public void buildQuery(String string, Integer add) {
		this.buildQuery(string, add, false);
	}

	@Override
	public void buildQuery(String string, Integer add, Boolean finalize) {
		this.buildQuery(string, add, finalize, false);
	}

	@Override
	public void buildQuery(String string, Integer add, Boolean finalize, Boolean debug) {
		this.buildQuery(string, add, finalize, debug, false);
	}

	@Override
	public void buildQuery(String string, Integer add, Boolean finalize, Boolean debug, Boolean table) {
		
	}

	@Override
	public ArrayList<HashMap<String, String>> execQuery() {
		
		return null;
	}

	@Override
	public ArrayList<HashMap<String, String>> execQuery(Integer queryID) {
		
		return null;
	}

	@Override
	public void updateQuery() {
		
	}

	@Override
	public void updateQuery(Integer queryID) {
		
	}

	@Override
	public int countResult() {
		
		return 0;
	}

	@Override
	public int countResult(Integer queryID) {
		
		return 0;
	}

	@Override
	public ArrayList<HashMap<String, String>> getResult() {
		
		return null;
	}

	@Override
	public ArrayList<HashMap<String, String>> getResult(Integer queryID) {
		
		return null;
	}

	@Override
	public HashMap<String, String> getSingleResult() {
		
		return null;
	}

	@Override
	public HashMap<String, String> getSingleResult(Integer queryID) {
		
		return null;
	}

	@Override
	public iDriver select(String field) {
		
		return null;
	}

	@Override
	public iDriver select(String... fields) {
		
		return null;
	}

	@Override
	public iDriver select(ArrayList<String> fields) {
		
		return null;
	}

	@Override
	public iDriver select(HashMap<String, String> fields) {
		
		return null;
	}

	@Override
	public iDriver from(String table) {
		
		return null;
	}

	@Override
	public iDriver where(HashMap<String, String> fields) {
		
		return null;
	}

	@Override
	public iDriver where(HashMap<String, HashMap<String, String>> fields,
			Boolean shite) {
		
		return null;
	}

	@Override
	public iDriver orderBy(HashMap<String, String> fields) {
		
		return null;
	}

	@Override
	public iDriver limit(int limit) {
		
		return null;
	}

	@Override
	public iDriver limit(int limit, Integer start) {
		
		return null;
	}

	@Override
	public iDriver update(String table) {
		
		return null;
	}

	@Override
	public iDriver set(HashMap<String, String> fields) {
		
		return null;
	}

	@Override
	public iDriver set(HashMap<String, HashMap<String, String>> fields, Boolean shite) {
		
		return null;
	}

	@Override
	public iDriver insert(String table, ArrayList<String> fields, HashMap<Integer, HashMap<String, String>> values) {
		
		return null;
	}

	@Override
	public iDriver insert(String table, ArrayList<String> fields, ArrayList<HashMap<Integer, HashMap<String, String>>> values) {
		
		return null;
	}

	@Override
	public iDriver delete(String table) {
		
		return null;
	}

	@Override
	public iDriver Truncate(String table) {
		
		return null;
	}

	@Override
	public iDriver create(String table) {
		
		return null;
	}

	@Override
	public iDriver fields(HashMap<String, HashMap<String, String>> fields) {
		
		return null;
	}

	@Override
	public iDriver alter(String table) {
		
		return null;
	}

	@Override
	public iDriver add(HashMap<String, HashMap<String, String>> fields) {
		
		return null;
	}

	@Override
	public iDriver debug(Boolean dbg) {
		
		return null;
	}

	@Override
	public iDriver Finalize() {
		
		return null;
	}

	@Override
	public iDriver debugFinalize(Boolean dbg) {
		
		return null;
	}

}

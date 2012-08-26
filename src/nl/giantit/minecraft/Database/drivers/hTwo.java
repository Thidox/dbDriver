package nl.giantit.minecraft.Database.drivers;

import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
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
		if(!add) {
			if(table)
				string = string.replace("#__", prefix);
			
			HashMap<String, String> ad = new HashMap<String, String>();
			ad.put("sql", string);
			
			if(finalize)
				ad.put("finalize", "true");
			
			if(debug)
				ad.put("debug", "true");
			
			sql.add(ad);
		}else{
			int last = sql.size() - 1;
			
			this.buildQuery(string, last, finalize, debug, table);
		}
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
		if(table)
			string = string.replace("#__", prefix);
		
		try {
			HashMap<String, String> SQL = sql.get(add);
			if(SQL.containsKey("sql")) {
				if(SQL.containsKey("finalize")) {
					if(true == debug)
						plugin.getLogger().log(Level.SEVERE, " SQL syntax is finalized!");
					return;
				}else{
					SQL.put("sql", SQL.get("sql") + string);
					
					if(true == finalize)
						SQL.put("finalize", "true");

					sql.add(add, SQL);
				}
			}else
				if(true == debug)
					plugin.getLogger().log(Level.SEVERE, add.toString() + " is not a valid SQL query!");
		
			if(debug == true)
				plugin.getLogger().log(Level.INFO, sql.get(add).get("sql"));
		}catch(NullPointerException e) {
			if(true == debug)
				plugin.getLogger().log(Level.SEVERE, "Query " + add.toString() + " could not be found!");
		}
	}

	@Override
	public ArrayList<HashMap<String, String>> execQuery() {
		Integer queryID = ((sql.size() - 1 > 0) ? (sql.size() - 1) : 0);
		
		return this.execQuery(queryID);
	}
	
	@Override
	public ArrayList<HashMap<String, String>> execQuery(Integer queryID) {
		Statement st = null;
		
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		try {
			HashMap<String, String> SQL = sql.get(queryID);
			if(SQL.containsKey("sql")) {
				try {
					st = con.createStatement();
					//query.add(queryID, st.executeQuery(SQL.get("sql")));
					ResultSet res = st.executeQuery(SQL.get("sql"));
					while(res.next()) {
						HashMap<String, String> row = new HashMap<String, String>();

						ResultSetMetaData rsmd = res.getMetaData();
						int columns = rsmd.getColumnCount();
						for(int i = 1; i < columns + 1; i++) {
							row.put(rsmd.getColumnName(i), res.getString(i));
						}
						data.add(row);
					}
				}catch (SQLException e) {
					plugin.getLogger().log(Level.SEVERE, " Could not execute query!");
					if(this.dbg) {
						plugin.getLogger().log(Level.INFO, e.getMessage());
						e.printStackTrace();
					}
				} finally {
					try {
						if(st != null) {
							st.close();
						}
					}catch (Exception e) {
						plugin.getLogger().log(Level.SEVERE, " Could not close database connection");
						if(this.dbg) {
							plugin.getLogger().log(Level.INFO, e.getMessage());
							e.printStackTrace();
						}
					}
				}
			}
		}catch(NullPointerException e) {
			plugin.getLogger().log(Level.SEVERE, "Query " + queryID.toString() + " could not be found!");
		}
		
		return data;
	}

	@Override
	public void updateQuery() {
		Integer queryID = ((sql.size() - 1 > 0) ? (sql.size() - 1) : 0);
		
		this.updateQuery(queryID);
	}
	
	@Override
	public void updateQuery(Integer queryID) {
		Statement st = null;
		
		try {
			HashMap<String, String> SQL = sql.get(queryID);
			if(SQL.containsKey("sql")) {
				try {
					st = con.createStatement();
					st.executeUpdate(SQL.get("sql"));
				}catch (SQLException e) {
					plugin.getLogger().log(Level.SEVERE, " Could not execute query!");
					if(this.dbg) {
						plugin.getLogger().log(Level.INFO, e.getMessage());
						e.printStackTrace();
					}
				} finally {
					try {
						if(st != null) {
							st.close();
						}
					}catch (Exception e) {
						plugin.getLogger().log(Level.SEVERE, " Could not close database connection");
						if(this.dbg) {
							plugin.getLogger().log(Level.INFO, e.getMessage());
							e.printStackTrace();
						}
					}
				}
			}
		}catch(NullPointerException e) {
			plugin.getLogger().log(Level.SEVERE, "Query " + queryID.toString() + " could not be found!");
		}
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

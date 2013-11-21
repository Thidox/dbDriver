package nl.giantit.minecraft.database.drivers.h2;

import nl.giantit.minecraft.database.DatabaseType;
import nl.giantit.minecraft.database.Driver;
import nl.giantit.minecraft.database.QueryResult;
import nl.giantit.minecraft.database.query.CreateQuery;
import nl.giantit.minecraft.database.query.DeleteQuery;
import nl.giantit.minecraft.database.query.InsertQuery;
import nl.giantit.minecraft.database.query.Query;
import nl.giantit.minecraft.database.query.SelectQuery;
import nl.giantit.minecraft.database.query.TruncateQuery;
import nl.giantit.minecraft.database.query.UpdateQuery;

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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class H2Driver implements Driver {
	
	private DatabaseType type = DatabaseType.hTwo;
	
	private static HashMap<String, H2Driver> instance = new HashMap<String, H2Driver>();
	private Plugin plugin;
	
	private ArrayList<HashMap<String, String>> sql = new ArrayList<HashMap<String, String>>();
	
	private String db, user, pass, prefix;
	private Connection con = null;
	private boolean dbg = false;
	
	private boolean parseBool(String s, boolean d) {
		if(s.equalsIgnoreCase("1") || s.equalsIgnoreCase("y") || s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("true"))
			return true;
		
		return d;
	}

	private H2Driver(Plugin p, HashMap<String, String> c) {
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
				plugin.getLogger().log(Level.INFO, e.getMessage(), e);
				
			}
		}catch(ClassNotFoundException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to connect to database: h2 library not found!");
			if(this.dbg) {
				plugin.getLogger().log(Level.INFO, e.getMessage(), e);
				
			}
		}
	}

	@Override
	public void close() {
		try {
			if(con.isClosed())
				return;
			
			this.con.close();
		}catch(SQLException e) {
			//ignore
		}
	}
	
	@Override
	public boolean isConnected() {
		try {
			return con != null && !con.isClosed() && con.isValid(0);
		}catch(SQLException e) {
			return false;
		}
	}

	@Override
	public boolean tableExists(String table) {
		ResultSet res = null;
		table = table.replace("#__", prefix);
		
		try {
			DatabaseMetaData data = this.con.getMetaData();
			res = data.getTables(null, null, table.toUpperCase(), null);

			return res.next();
		}catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, " Could not load table " + table);
			if(this.dbg) {
				plugin.getLogger().log(Level.INFO, e.getMessage(), e);
				
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
					plugin.getLogger().log(Level.INFO, e.getMessage(), e);
					
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
	public void buildQuery(String string, boolean add) {
		this.buildQuery(string, add, false);
	}

	@Override
	public void buildQuery(String string, boolean add, boolean finalize) {
		this.buildQuery(string, add, finalize, false);
	}

	@Override
	public void buildQuery(String string, boolean add, boolean finalize, boolean debug) {
		this.buildQuery(string, add, finalize, debug, false);
	}

	@Override
	public void buildQuery(String string, boolean add, boolean finalize, boolean debug, boolean table) {
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
	public void buildQuery(String string, Integer add, boolean finalize) {
		this.buildQuery(string, add, finalize, false);
	}

	@Override
	public void buildQuery(String string, Integer add, boolean finalize, boolean debug) {
		this.buildQuery(string, add, finalize, debug, false);
	}

	@Override
	public void buildQuery(String string, Integer add, boolean finalize, boolean debug, boolean table) {
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
	public QueryResult execQuery() {
		Integer queryID = ((sql.size() - 1 > 0) ? (sql.size() - 1) : 0);
		
		return this.execQuery(queryID);
	}
	
	@Override
	public QueryResult execQuery(Query q) {
		
		return null;
	}
	
	@Override
	public QueryResult execQuery(Integer queryID) {
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
							row.put(rsmd.getColumnName(i).toLowerCase(), res.getString(i));
						}
						data.add(row);
					}
				}catch (SQLException e) {
					plugin.getLogger().log(Level.SEVERE, " Could not execute query!");
					if(this.dbg) {
						plugin.getLogger().log(Level.INFO, e.getMessage(), e);
					}
				} finally {
					try {
						if(st != null) {
							st.close();
						}
					}catch (Exception e) {
						plugin.getLogger().log(Level.SEVERE, " Could not close database connection");
						if(this.dbg) {
							plugin.getLogger().log(Level.INFO, e.getMessage(), e);
							
						}
					}
				}
			}
		}catch(NullPointerException e) {
			plugin.getLogger().log(Level.SEVERE, "Query " + queryID.toString() + " could not be found!");
		}
		
		return QueryResult.QR(data);
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
						plugin.getLogger().log(Level.INFO, e.getMessage(), e);
					}
				} finally {
					try {
						if(st != null) {
							st.close();
						}
					}catch (Exception e) {
						plugin.getLogger().log(Level.SEVERE, " Could not close database connection");
						if(this.dbg) {
							plugin.getLogger().log(Level.INFO, e.getMessage(), e);
						}
					}
				}
			}
		}catch(NullPointerException e) {
			plugin.getLogger().log(Level.SEVERE, "Query " + queryID.toString() + " could not be found!");
		}
	}
	
	@Override
	public void updateQuery(Query q) {
		
	}
	
	@Override
	public SelectQuery select(String f) {
		SelectQuery sQ = new H2SelectQuery(this);
		return sQ.select(f);
	}
	
	@Override
	public SelectQuery select(String... fields) {
		SelectQuery sQ = new H2SelectQuery(this);
		return sQ.select(fields);
	}
	
	@Override
	public SelectQuery select(List<String> fields) {
		SelectQuery sQ = new H2SelectQuery(this);
		return sQ.select(fields);
	}
	
	@Override
	public SelectQuery select(Map<String, String> fields) {
		SelectQuery sQ = new H2SelectQuery(this);
		return sQ.select(fields);
	}
	
	@Override
	public UpdateQuery update(String table) {
		return new H2UpdateQuery(this).setTable(table);
	}
	
	@Override
	public InsertQuery insert(String table) {
		return new H2InsertQuery(this).into(table);
	}
	
	@Override
	public DeleteQuery delete(String table) {
		return new H2DeleteQuery(this).from(table);
	}
	
	@Override
	public TruncateQuery Truncate(String table) {
		return new H2TruncateQuery(this).setTable(table);
	}
	

	@Override
	public CreateQuery create(String table) {
		return new H2CreateQuery(this).create(table);
	}
	
	@Override
	}

	@Override
	public Driver alter(String table) {
		table = table.replace("#__", prefix);
		this.buildQuery("ALTER TABLE " + table + "\n", false, false, false);
		
		return this;
	}
	
	@Override
	public Driver add(HashMap<String, HashMap<String, String>> fields) {
		int i = 0;
		for(Map.Entry<String, HashMap<String, String>> entry : fields.entrySet()) {
			i++;
			HashMap<String, String> data = entry.getValue();
			
			String field = entry.getKey();
			String t = "VARCHAR";
			Integer length = 100;
			boolean NULL = false;
			String def = "";
			
			if(data.containsKey("TYPE")) {
				t = data.get("TYPE");
			}
			
			if(data.containsKey("LENGTH")) {
				if(null != data.get("LENGTH")) {
					try{
						length = Integer.parseInt(data.get("LENGTH"));
						length = length < 0 ? 100 : length;
					}catch(NumberFormatException e) {}
				}else
					length = null;
			}
			
			if(data.containsKey("NULL")) {
				NULL = Boolean.parseBoolean(data.get("NULL"));
			}
			
			if(data.containsKey("DEFAULT")) {
				def = data.get("DEFAULT");
			}
			
			if(length != null)
				t += "(" + length + ")";
			
			String n = (!NULL) ? " NOT NULL" : " DEFAULT NULL";
			String d = (!def.equalsIgnoreCase("")) ? " DEFAULT " + def : "";
			String c = (i < fields.size()) ? ",\n" : ""; 
			
			this.buildQuery("ADD " + field + " " + t + n + d + c, true);
		}
		
		return this;
	}
	
	@Override
	public DatabaseType getType() {
		return this.type;
	}
	
	@Override
	public String getPrefix() {
		return this.prefix;
	}
	
	@Override
	public Query create(Type t) {
		switch(t) {
			case SELECT:
				return new H2SelectQuery(this);
			case UPDATE:
				return new H2UpdateQuery(this);
			case INSERT:
				return new H2InsertQuery(this);
			case DELETE:
				return new H2DeleteQuery(this);
			case TRUNCATE:
				return new H2TruncateQuery(this);
			case CREATE:
				break;
		}
		
		return null;
	}
	
	@Override
	public Plugin getPlugin() {
		return this.plugin;
	}
	
	public static H2Driver Obtain(Plugin p, HashMap<String, String> conf, String instance) {
		if(!H2Driver.instance.containsKey(instance))
			H2Driver.instance.put(instance, new H2Driver(p, conf));
		
		return H2Driver.instance.get(instance);
	}

}

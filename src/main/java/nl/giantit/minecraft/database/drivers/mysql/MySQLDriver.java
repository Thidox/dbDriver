package nl.giantit.minecraft.database.drivers.mysql;

import nl.giantit.minecraft.database.Driver;
import nl.giantit.minecraft.database.DatabaseType;
import nl.giantit.minecraft.database.query.Query;
import nl.giantit.minecraft.database.QueryResult;
import nl.giantit.minecraft.database.query.DeleteQuery;
import nl.giantit.minecraft.database.query.InsertQuery;
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

/**
 *
 * @author Giant
 */
public class MySQLDriver implements Driver {
	
	private final DatabaseType type = DatabaseType.MySQL;
	
	private final static HashMap<String, MySQLDriver> instance = new HashMap<String, MySQLDriver>();
	private final Plugin plugin;
	
	private final ArrayList<HashMap<String, String>> sql = new ArrayList<HashMap<String, String>>();
	
	private final String db, host, port, user, pass, prefix;
	private Connection con = null;
	private boolean dbg = false;
	
	private boolean parseBool(String s, boolean d) {
		if(s.equalsIgnoreCase("1") || s.equalsIgnoreCase("y") || s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("true"))
			return true;
		
		return d;
	}
	
	private void connect() {
		String dbPath = "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.db + "?user=" + this.user + "&password=" + this.pass;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			this.con = DriverManager.getConnection(dbPath);
		}catch(SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to connect to database: SQL error!");
			if(this.dbg) {
				plugin.getLogger().log(Level.INFO, e.getMessage(), e);
			}
		}catch(ClassNotFoundException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to connect to database: MySQL library not found!");
			if(this.dbg) {
				plugin.getLogger().log(Level.INFO, e.getMessage(), e);
			}
		}
	}
	
	private MySQLDriver(Plugin p, HashMap<String, String> c) {
		this.plugin = p;
		
		this.db = c.get("database");
		this.host = c.get("host");
		this.port = String.valueOf(c.get("port"));
		this.user = c.get("user");
		this.pass = c.get("password");
		this.prefix = c.get("prefix");
		this.dbg = (c.containsKey("debug")) ? this.parseBool(c.get("debug"), false) : false;
		this.connect();
	}
	
	@Override
	public void close() {
		try {
			if(null == con || !con.isClosed() || !con.isValid(0))
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
			res = data.getTables(null, null, table, null);

			return res.next();
		}catch (NullPointerException e) {
			plugin.getLogger().log(Level.SEVERE, "Could not load table " + table);
			if(this.dbg) {
				plugin.getLogger().log(Level.INFO, e.getMessage(), e);
			}
            return false;
		}catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Could not load table " + table);
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
				plugin.getLogger().log(Level.SEVERE, "Could not close result connection to database");
				if(this.dbg) {
					plugin.getLogger().log(Level.INFO, e.getMessage(), e);
				}
				return false;
			}
		}
	}
	
	@Override
	public void buildQuery(String string) {
		this.buildQuery(string, false, false, false);
	}
	
	@Override
	public void buildQuery(String string, boolean add) {
		this.buildQuery(string, add, false, false);
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
		this.buildQuery(string, add, false, false);
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
						plugin.getLogger().log(Level.SEVERE, "SQL syntax is finalized!");
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
				plugin.getLogger().log(Level.SEVERE, "Query " + add + " could not be found!");
		}
	}
	
	@Override
	public QueryResult execQuery() {
		Integer queryID = ((sql.size() - 1 > 0) ? (sql.size() - 1) : 0);
		return this.execQuery(queryID);
	}
	
	@Override
	public QueryResult execQuery(Integer queryID) {
		Statement st = null;
		
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		try {
			HashMap<String, String> SQL = sql.get(queryID);
			if(SQL.containsKey("sql")) {
				try {
					if(null == con || con.isClosed() || !con.isValid(0))
						this.connect();
					
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
					plugin.getLogger().log(Level.SEVERE, "Could not execute query!");
					if(this.dbg) {
						plugin.getLogger().log(Level.INFO, e.getMessage(), e);
					}
				} finally {
					try {
						if(st != null) {
							st.close();
						}
					}catch (Exception e) {
						plugin.getLogger().log(Level.SEVERE, "Could not close database connection");
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
	public QueryResult execQuery(Query q) {
		
		return null;
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
					if(null == con || con.isClosed() || !con.isValid(0))
						this.connect();
					
					st = con.createStatement();
					st.executeUpdate(SQL.get("sql"));
				}catch (SQLException e) {
					plugin.getLogger().log(Level.SEVERE, "Could not execute query!");
					if(this.dbg) {
						plugin.getLogger().log(Level.INFO, e.getMessage(), e);
					}
				} finally {
					try {
						if(st != null) {
							st.close();
						}
					}catch (Exception e) {
						plugin.getLogger().log(Level.SEVERE, "Could not close database connection");
						if(this.dbg) {
							plugin.getLogger().log(Level.INFO, e.getMessage(), e);
						}
					}
				}
			}
		}catch(NullPointerException e) {
			plugin.getLogger().log(Level.SEVERE, queryID.toString() + " is not a valid SQL query!");
		}
	}
	
	@Override
	public void updateQuery(Query q) {
		
	}
	
	@Override
	public SelectQuery select(String f) {
		SelectQuery sQ = new MySQLSelectQuery(this);
		return sQ.select(f);
	}
	
	@Override
	public SelectQuery select(String... fields) {
		SelectQuery sQ = new MySQLSelectQuery(this);
		return sQ.select(fields);
	}
	
	@Override
	public SelectQuery select(List<String> fields) {
		SelectQuery sQ = new MySQLSelectQuery(this);
		return sQ.select(fields);
	}
	
	@Override
	public SelectQuery select(Map<String, String> fields) {
		SelectQuery sQ = new MySQLSelectQuery(this);
		return sQ.select(fields);
	}
	
	@Override
	public UpdateQuery update(String table) {
		/*table = table.replace("#__", prefix);
		this.buildQuery("UPDATE " + table + " \n", false, false, false);*/
		UpdateQuery uQ = new MySQLUpdateQuery(this);
		uQ.setTable(table);
		
		return uQ;
	}
	
	@Override
	public InsertQuery insert(String table) {
		return new MySQLInsertQuery(this).into(table);
	}
	
	@Override
	public DeleteQuery delete(String table) {
		return new MySQLDeleteQuery(this).from(table);
	}
	
	@Override
	public TruncateQuery Truncate(String table) {
		return new MySQLTruncateQuery(this).setTable(table);
	}

	@Override
	public Driver create(String table) {
		table = table.replace("#__", prefix);
		this.buildQuery("CREATE TABLE " + table + "\n", false, false, false);
		
		return this;
	}
	
	@Override
	public Driver fields(HashMap<String, HashMap<String, String>> fields) {
		String P_KEY = "";
		this.buildQuery("(", true, false, false);
		
		int i = 0;
		for(Map.Entry<String, HashMap<String, String>> entry : fields.entrySet()) {
			i++;
			HashMap<String, String> data = entry.getValue();
			
			String field = entry.getKey();
			String t = "VARCHAR";
			Integer length = 100;
			boolean NULL = false;
			String def = "";
			boolean aincr = false;
			
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
			
			if(data.containsKey("A_INCR")) {
				aincr = Boolean.parseBoolean(data.get("A_INCR"));
			}
			
			if(data.containsKey("P_KEY")) {
				if(Boolean.parseBoolean(data.get("P_KEY"))) {
					P_KEY = field;
				}
			}
			
			if(length != null)
				t += "(" + length + ")";
			
			String n = (!NULL) ? " NOT NULL" : " DEFAULT NULL";
			String d = (!def.equalsIgnoreCase("")) ? " DEFAULT " + def : ""; 
			String a = (aincr) ? " AUTO_INCREMENT" : "";
			String c = (i < fields.size()) ? ",\n" : ""; 
			
			this.buildQuery(field + " " + t + n + d + a + c, true);
		}
		
		if(!P_KEY.equalsIgnoreCase(""))
			this.buildQuery("\n, PRIMARY KEY(" + P_KEY + ")", true, false, false);
		
		this.buildQuery(") ENGINE=InnoDB DEFAULT CHARSET=latin1;", true, false, false);
		
		return this;
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
				return new MySQLSelectQuery(this);
			case UPDATE:
				return new MySQLUpdateQuery(this);
			case INSERT:
				return new MySQLInsertQuery(this);
			case DELETE:
				return new MySQLDeleteQuery(this);
			case TRUNCATE:
				return new MySQLTruncateQuery(this);
			case CREATE:
				break;
		}
		
		return null;
	}
	
	@Override
	public Plugin getPlugin() {
		return this.plugin;
	}
	
	public static MySQLDriver Obtain(Plugin p, HashMap<String, String> conf, String instance) {
		if(!MySQLDriver.instance.containsKey(instance))
			MySQLDriver.instance.put(instance, new MySQLDriver(p, conf));
		
		return MySQLDriver.instance.get(instance);
	}
}

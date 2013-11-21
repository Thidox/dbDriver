package nl.giantit.minecraft.database;

import nl.giantit.minecraft.database.query.CreateQuery;
import nl.giantit.minecraft.database.query.DeleteQuery;
import nl.giantit.minecraft.database.query.IndexQuery;
import nl.giantit.minecraft.database.query.InsertQuery;
import nl.giantit.minecraft.database.query.Query;
import nl.giantit.minecraft.database.query.SelectQuery;
import nl.giantit.minecraft.database.query.TruncateQuery;
import nl.giantit.minecraft.database.query.UpdateQuery;

import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Giant
 */
public interface Driver {
	
	public enum Type {
		SELECT,
		UPDATE,
		INSERT,
		DELETE,
		TRUNCATE,
		CREATE,
		INDEX,
	}
	
	public void close();
	
	public boolean isConnected();
	
	public boolean tableExists(String table);
	
	@Deprecated
	public void buildQuery(String string);
	@Deprecated
	public void buildQuery(String string, boolean add);
	@Deprecated
	public void buildQuery(String string, boolean add, boolean finalize);
	@Deprecated
	public void buildQuery(String string, boolean add, boolean finalize, boolean debug);
	@Deprecated
	public void buildQuery(String string, boolean add, boolean finalize, boolean debug, boolean table);
	
	@Deprecated
	public void buildQuery(String string, Integer add);
	@Deprecated
	public void buildQuery(String string, Integer add, boolean finalize);
	@Deprecated
	public void buildQuery(String string, Integer add, boolean finalize, boolean debug);
	@Deprecated
	public void buildQuery(String string, Integer add, boolean finalize, boolean debug, boolean table);
	
	@Deprecated
	public QueryResult execQuery();
	@Deprecated
	public QueryResult execQuery(Integer queryID);
	public QueryResult execQuery(Query q);
	
	@Deprecated
	public void updateQuery();
	@Deprecated
	public void updateQuery(Integer queryID);
	public void updateQuery(Query q);
	
	public SelectQuery select(String f);
	public SelectQuery select(String... fields);
	public SelectQuery select(List<String> fields);
	public SelectQuery select(Map<String, String> fields);
	
	public UpdateQuery update(String table);
	
	public InsertQuery insert(String table);
	
	public DeleteQuery delete(String table);
	
	public TruncateQuery Truncate(String table);
	
	public CreateQuery create(String table);
	
	public IndexQuery createIndex(String index);

	@Deprecated
	public Driver alter(String table);
	@Deprecated
	public Driver add(HashMap<String, HashMap<String, String>> fields);
	
	public String getPrefix();
	
	public Query create(Type t);
	
	public DatabaseType getType();
	
	public Plugin getPlugin();
}

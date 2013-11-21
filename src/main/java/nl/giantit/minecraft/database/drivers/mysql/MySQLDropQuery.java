package nl.giantit.minecraft.database.drivers.mysql;

import nl.giantit.minecraft.database.Driver;
import nl.giantit.minecraft.database.QueryResult;
import nl.giantit.minecraft.database.query.DropQuery;
import nl.giantit.minecraft.database.query.Group;
import nl.giantit.minecraft.database.query.Query;

/**
 *
 * @author Giant
 */
public class MySQLDropQuery implements DropQuery {

	private final Driver db;
	
	private String t;
	
	private boolean prepared = false;
	private String query;
	
	public MySQLDropQuery(Driver db) {
		this.db = db;
	}
	
	@Override
	public DropQuery setTable(String table) {
		this.t = table;
		
		return this;
	}
	
	@Override
	public Query parse() {
		if(!this.prepared) {
			this.prepared = true;
			
			StringBuilder sB = new StringBuilder();
			sB.append("DROP TABLE ");
			sB.append(this.t.replace("#__", this.db.getPrefix()));
			sB.append(";");
			
			this.query = sB.toString();
		}
		
		return this;
	}
	
	@Override
	public boolean isParsed() {
		return this.prepared;
	}
	
	@Override
	public String getParsedQuery() {
		if(!this.prepared) {
			return "";
		}
		
		return this.query;
	}

	@Override
	public QueryResult exec() {
		return this.exec(false);
	}

	@Override
	public QueryResult exec(boolean debug) {
		if(debug) {
			// Send MySQL Query syntax to console for debugging purposes!
			this.db.getPlugin().getLogger().info(this.query);
		}
		
		return this.db.updateQuery(this);
	}
	
	@Override
	public Group createGroup() {
		return new MySQLGroup(this.db);
	}
}

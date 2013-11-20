package nl.giantit.minecraft.database.drivers.sqlite;

import nl.giantit.minecraft.database.Driver;
import nl.giantit.minecraft.database.query.Join;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Giant
 */
public class SQLiteJoin implements Join {

	private final Driver db;
	private final String table;
	private final String as;
	
	private Type t;
	
	private final Map<String, String> on = new LinkedHashMap<String, String>();
	
	private boolean prepared = false;
	private String join;
	
	public SQLiteJoin(Driver db, String table) {
		this(db, table, table);
	}
	
	public SQLiteJoin(Driver db, String table, String as) {
		this.db = db;
		this.table = table;
		this.as = as;
	}
	
	@Override
	public Join setType(Type t) {
		this.t = t;
		
		return this;
	}
	
	public Join on(String field, String compareTo) {
		this.on.put(field, compareTo);
		
		return this;
	}
	
	@Override
	public Join parse() {
		if(!this.prepared) {
			this.prepared = true;
			
			StringBuilder sB = new StringBuilder();
			sB.append(this.t.getTextual());
			sB.append(this.table.replace("#__", this.db.getPrefix()));
			sB.append(" AS ");
			sB.append(this.as.replace("#__", this.db.getPrefix()));
			
			if(on.size() > 0) {
				sB.append(" ON ");
				for(Map.Entry<String, String> entry : this.on.entrySet()) {
					sB.append(entry.getKey().replace("#__", this.db.getPrefix()));
					sB.append(" = ");
					sB.append(entry.getValue().replace("#__", this.db.getPrefix()));
				}
			}
		}
		
		return this;
	}
	
	@Override
	public boolean isParsed() {
		return this.prepared;
	}
	
	@Override
	public String getParsedJoin() {
		if(!this.prepared) {
			return "";
		}
		
		return this.join;
	}
	
	@Override
	public Type getType() {
		return this.t;
	}
	
}

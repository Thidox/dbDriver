package nl.giantit.minecraft.database.drivers.h2;

import nl.giantit.minecraft.database.Driver;
import nl.giantit.minecraft.database.QueryResult;
import nl.giantit.minecraft.database.query.Group;
import nl.giantit.minecraft.database.query.IndexQuery;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Giant
 */
public class H2IndexQuery implements IndexQuery {
	
	private final Driver db;

	private String n;
	private String table;
	private Type t;
	private final List<String> fields = new ArrayList<String>();
	
	private boolean prepared = false;
	private String index;
	
	public H2IndexQuery(Driver db) {
		this.db = db;
	}
	
	@Override
	public IndexQuery setName(String n) {
		this.n = n;
		return this;
	}
	
	@Override
	public IndexQuery setTable(String t) {
		this.table = t;
		return this;
	}
	
	@Override
	public IndexQuery setType(Type t) {
		this.t = t;
		return this;
	}
	
	@Override
	public IndexQuery addField(String f) {
		this.fields.add(f);
		return this;
	}

	@Override
	public IndexQuery parse() {
		if(!this.prepared) {
			this.prepared = true;
			
			StringBuilder sB = new StringBuilder();
			sB.append("CREATE ");
			sB.append(this.t.getTextual());
			
			sB.append(this.n);
			
			sB.append(" ON ");
			sB.append(this.table);
			
			sB.append(" (");
			int i = 0;
			for(String f : this.fields) {
				if(i > 0) {
					sB.append(", ");
				}else{
					++i;
				}
				
				sB.append(f);
			}
			
			sB.append(");");
			
			this.index = sB.toString();
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
		
		return this.index;
	}

	@Override
	public QueryResult exec() {
		return this.exec(false);
	}

	@Override
	public QueryResult exec(boolean debug) {
		if(debug) {
			// Send H2 Query syntax to console for debugging purposes!
			this.db.getPlugin().getLogger().info(this.index);
		}
		
		return this.db.updateQuery(this);
	}
	
	@Override
	public Group createGroup() {
		return new H2Group(this.db);
	}
}

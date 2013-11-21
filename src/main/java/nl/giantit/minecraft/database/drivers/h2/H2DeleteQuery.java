package nl.giantit.minecraft.database.drivers.h2;

import nl.giantit.minecraft.database.Driver;
import nl.giantit.minecraft.database.QueryResult;
import nl.giantit.minecraft.database.query.DeleteQuery;
import nl.giantit.minecraft.database.query.Group;

import nl.giantit.minecraft.database.query.Query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Giant
 */
public class H2DeleteQuery implements DeleteQuery {

	private final Driver db;
	
	private String from;
	private final List<Group> where = new ArrayList<Group>();
	
	private boolean prepared = false;
	private String query;
	
	public H2DeleteQuery(Driver db) {
		this.db = db;
	}
	
	@Override
	public DeleteQuery from(String table) {
		if(!this.isParsed()) {
			this.from = table;	
		}
		
		return this;
	}
	
	@Override
	public Group where(String field, Group.ValueType vT){
		return this.where(Group.Type.AND, field, null, vT);
	}
	
	@Override
	public Group where(Group.Type t, String field, Group.ValueType vT){
		return this.where(t, field, null, vT);
	}
	
	@Override
	public Group where(String field, String value) {
		return this.where(Group.Type.AND, field, value);
	}
	
	@Override
	public Group where(String field, String value, Group.ValueType vT) {
		return this.where(Group.Type.AND, field, value, vT);
	}
	
	@Override
	public Group where(Group.Type t, String field, String value) {
		return this.where(t, field, value, Group.ValueType.EQUALS);
	}
	
	@Override
	public Group where(Group.Type t, String field, String value, Group.ValueType vT) {
		Group g = new H2Group(this.db);
		g.add(t, field, value, vT);
		
		return this.where(g);
	}
	
	@Override
	public Group where(Group g) {
		if(!this.isParsed()) {
			if(where.isEmpty()) {
				g.setType(Group.Type.PRIMARY);
			}else if(g.getType() == Group.Type.PRIMARY) {
				g.setType(Group.Type.AND);
			}
		}
		
		where.add(g);
		return g;
	}
	
	@Override
	public Query parse() {
		if(!this.prepared) {
			this.prepared = true;
			
			StringBuilder sB = new StringBuilder();
			sB.append("DELETE FROM ");
			sB.append(this.from.replace("#__", this.db.getPrefix()));
			sB.append("\n");
			
			if(this.where.size() > 0) {
				sB.append(" WHERE ");
				Iterator<Group> whereIterator = this.where.iterator();
				while(whereIterator.hasNext()) {
					Group g = whereIterator.next();
					if(!g.isParsed()) {
						g.parse();
					}
					
					sB.append(g.getType().getTextual());
					sB.append(g.getParsedGroup());
				}
			
				sB.append("\n");
			}
			
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
			// Send H2 Query syntax to console for debugging purposes!
			this.db.getPlugin().getLogger().info(this.query);
		}
		
		return this.db.updateQuery(this);
	}
	
	@Override
	public Group createGroup() {
		return new H2Group(this.db);
	}
	
}

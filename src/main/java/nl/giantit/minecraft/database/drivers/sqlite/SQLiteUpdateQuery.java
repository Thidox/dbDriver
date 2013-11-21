package nl.giantit.minecraft.database.drivers.sqlite;

import nl.giantit.minecraft.database.Driver;
import nl.giantit.minecraft.database.QueryResult;
import nl.giantit.minecraft.database.query.Group;
import nl.giantit.minecraft.database.query.Query;
import nl.giantit.minecraft.database.query.UpdateQuery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Giant
 */
public class SQLiteUpdateQuery implements UpdateQuery {

	private final Driver db;
	
	private final List<Elem> set = new ArrayList<Elem>();
	private final List<Group> where = new ArrayList<Group>();
	
	private String table;
	
	private boolean prepared = false;
	private String query;
	
	public SQLiteUpdateQuery(Driver db) {
		this.db = db;
	}
	
	@Override
	public UpdateQuery setTable(String table) {
		this.table = table;
		return this;
	}
	
	@Override
	public UpdateQuery set(String field, String value) {
		return this.set(field, value, ValueType.SET);
	}
	
	@Override
	public UpdateQuery set(String field, String value, ValueType vT) {
		Elem e = new Elem(field, value, vT);
		this.set.add(e);
		
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
		Group g = new SQLiteGroup(this.db);
		g.add(t, field, value, vT);
		
		return this.where(g);
	}
	
	@Override
	public Group where(Group g) {
		if(where.isEmpty()) {
			g.setType(Group.Type.PRIMARY);
		}else if(g.getType() == Group.Type.PRIMARY) {
			g.setType(Group.Type.AND);
		}
		
		where.add(g);
		return g;
	}

	@Override
	public Query parse() {
		if(!this.prepared) {
			this.prepared = true;
			
			StringBuilder sB = new StringBuilder();
			sB.append("UPDATE ");
			sB.append(table.replace("#__", this.db.getPrefix()));
			sB.append("\n");
			
			sB.append(" SET ");
			int s = this.set.size();
			for(int i = 0; i < s; ++i) {
				if(i > 0) {
					sB.append(", ");
				}
				
				Elem e = this.set.get(i);
				String v = e.getValue() == null ? "" : e.getValue();
				ValueType vT = e.getValueType();
				String vTS = vT.getTextual().replace("%1", e.getField().replace("#__", this.db.getPrefix()));
				vTS = vTS.replace("%2", v.replace("#__", this.db.getPrefix()));
				
				sB.append(vTS);
			}
			
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
		return new SQLiteGroup(this.db);
	}
	
	
}

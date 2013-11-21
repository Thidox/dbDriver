package nl.giantit.minecraft.database.drivers.mysql;

import nl.giantit.minecraft.database.Driver;
import nl.giantit.minecraft.database.query.ForeignKey;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Giant
 */
public class MySQLForeignKey implements ForeignKey {
	
	private final Driver db;
	
	private String name;
	
	private List<String> lFL = new ArrayList<String>();
	private String remoteTable;
	private List<String> rFL = new ArrayList<String>();
			
	private Type onUpdate = null;
	private Type onDelete = null;
	
	private boolean prepared = false;
	private String fk;
	
	public MySQLForeignKey(Driver db) {
		this.db = db;
	}

	@Override
	public ForeignKey setName(String n) {
		this.name = n;
		return this;
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public ForeignKey addLocalField(String f) {
		this.lFL.add(f);
		return this;
	}
	
	@Override
	public ForeignKey remoteTable(String t) {
		this.remoteTable = t;
		return this;
	}
	
	@Override
	public ForeignKey addRemoteField(String f) {
		this.rFL.add(f);
		return this;
	}
	
	@Override
	public ForeignKey onUpdate(Type t) {
		this.onUpdate = t;
		return this;
	}
	
	@Override
	public ForeignKey onDelete(Type t) {
		this.onDelete = t;
		return this;
	}
	
	@Override
	public ForeignKey parse() {
		if(!this.prepared) {
			this.prepared = true;
			
			StringBuilder sB = new StringBuilder();
			sB.append("FOREIGN KEY (");
			int i = 0;
			for(String lF : lFL) {
				if(i > 0) {
					sB.append(", ");
				}else{
					++i;
				}
				
				sB.append(lF);
			}
			
			sB.append(") \n");
			
			sB.append(" REFERENCES ");
			sB.append(this.remoteTable.replace("#__", this.db.getPrefix()));
			sB.append(" (");
			i = 0;
			for(String rF : rFL) {
				if(i > 0) {
					sB.append(", ");
				}else{
					++i;
				}
				
				sB.append(rF);
			}
			sB.append(") \n");
			
			if(null != this.onUpdate) {
				sB.append(" ON UPDATE ");
				sB.append(this.onUpdate.getTextual());
				sB.append(" \n");
			}
			
			if(null != this.onDelete) {
				sB.append(" ON DELETE ");
				sB.append(this.onDelete.getTextual());
				sB.append(" \n");
			}
			
			this.fk = sB.toString();
		}
		
		return this;
	}
	
	@Override
	public boolean isParsed() {
		return this.prepared;
	}
	
	@Override
	public String getParsedFK() {
		if(!this.prepared) {
			return "";
		}
		
		return this.fk;
	}
	
}

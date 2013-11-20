package nl.giantit.minecraft.database;

/**
 *
 * @author Giant
 */
public enum DatabaseType {
	
	SQLite("SQLite"),
	MySQL("MySQL"),
	hTwo("h2");
	
	private String name;
	
	private DatabaseType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
}

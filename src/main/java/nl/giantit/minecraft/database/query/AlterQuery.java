package nl.giantit.minecraft.database.query;

/**
 *
 * @author Giant
 */
public interface AlterQuery extends Query {

	public AlterQuery setTable(String table);
	
	public AlterQuery rename(String table);
	
	public Column addColumn(String column);
	public Column addColumn(Column column);
	
}

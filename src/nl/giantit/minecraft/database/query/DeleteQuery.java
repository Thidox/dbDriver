package nl.giantit.minecraft.database.query;

/**
 *
 * @author Giant
 */
public interface DeleteQuery extends Query {

	public DeleteQuery from(String table);
	
	public Group where(String field, Group.ValueType vT);
	public Group where(Group.Type t, String field, Group.ValueType vT);
	public Group where(String field, String value);
	public Group where(String field, String value, Group.ValueType vT);
	public Group where(Group.Type t, String field, String value);
	public Group where(Group.Type t, String field, String value, Group.ValueType vT);
	public Group where(Group g);
}

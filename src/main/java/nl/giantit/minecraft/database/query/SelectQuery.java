package nl.giantit.minecraft.database.query;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Giant
 */
public interface SelectQuery extends Query {
	
	public enum Order {
		DESC("DESC", "Descending"),
		ASC("ASC", "Ascending");
		
		private final String value;
		private final String descr;
		
		private Order(String value, String descr) {
			this.value = value;
			this.descr = descr;
		}
		
		public String getValue() {
			return this.value;
		}
		
		public String getDescr() {
			return this.descr;
		}
	}

	public SelectQuery select(String f);
	public SelectQuery select(String... fields);
	public SelectQuery select(List<String> fields);
	public SelectQuery select(Map<String, String> fields);
	
	public SelectQuery from(String table);
	public SelectQuery from(String table, String as);
	
	public Join join(String table);
	public Join join(Join j);
	
	public Group where(String field, Group.ValueType vT);
	public Group where(Group.Type t, String field, Group.ValueType vT);
	public Group where(String field, String value);
	public Group where(String field, String value, Group.ValueType vT);
	public Group where(Group.Type t, String field, String value);
	public Group where(Group.Type t, String field, String value, Group.ValueType vT);
	public Group where(Group g);
	
	public SelectQuery orderBy(String field, Order order);
	public SelectQuery orderBy(Map<String, Order> fields);
	
	public SelectQuery limit(int limit);
	public SelectQuery limit(int limit, Integer start);
}

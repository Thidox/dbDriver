package nl.giantit.minecraft.database.query;

/**
 *
 * @author Giant
 */
public interface UpdateQuery extends Query {
	
	public class Elem {
		
		private final String f;
		private final String v;
		private final ValueType vT;

		public Elem(String field, String value, ValueType valueType) {
			this.f = field;
			this.v = value;
			this.vT = valueType;
		}
		
		public String getField() {
			return this.f;
		}
		
		public String getValue() {
			return this.v;
		}
		
		public ValueType getValueType() {
			return this.vT;
		}	
	}
	
	
	
	public enum ValueType {
		SET(" %1 = '%2' "),
		SETRAW(" %1 = %2 "),
		RAW(" %1 ");
		
		private final String textual;
		
		private ValueType(String textual) {
			this.textual = textual;
		}
		
		public String getTextual() {
			return this.textual;
		}
	}
	
	public UpdateQuery setTable(String table);
	
	public UpdateQuery set(String field, String value);
	public UpdateQuery set(String field, String value, ValueType vT);

	public Group where(String field, Group.ValueType vT);
	public Group where(Group.Type t, String field, Group.ValueType vT);
	public Group where(String field, String value);
	public Group where(String field, String value, Group.ValueType vT);
	public Group where(Group.Type t, String field, String value);
	public Group where(Group.Type t, String field, String value, Group.ValueType vT);
	public Group where(Group g);
	
}

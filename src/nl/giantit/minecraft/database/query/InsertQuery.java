package nl.giantit.minecraft.database.query;

import java.util.List;

/**
 *
 * @author Giant
 */
public interface InsertQuery extends Query {
	
	public class Elem {
		
		private final String v;
		private final ValueType vT;

		public Elem(String value, ValueType valueType) {
			this.v = value;
			this.vT = valueType;
		}
		
		public String getValue() {
			return this.v;
		}
		
		public ValueType getValueType() {
			return this.vT;
		}	
	}
	
	public enum ValueType {
		DEFAULT("'%1'"),
		RAW("%1");
		
		private final String textual;
		
		private ValueType(String textual) {
			this.textual = textual;
		}
		
		public String getTextual() {
			return this.textual;
		}
	}

	public InsertQuery into(String table);
	
	public InsertQuery addField(String field);
	public InsertQuery addFields(String... fields);
	public InsertQuery addFields(List<String> fields);
	
	public InsertQuery addRow();
	
	public InsertQuery assignValue(String field, String value);
	public InsertQuery assignValue(String field, String value, ValueType vT);
	
}

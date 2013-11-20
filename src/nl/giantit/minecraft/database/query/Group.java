package nl.giantit.minecraft.database.query;

/**
 *
 * @author Giant
 */
public interface Group {
	
	public class Elem {
		
		private final Type t;
		private final String f;
		private final String v;
		private final ValueType vT;

		public Elem(Type type, String field, String value, ValueType valueType) {
			this.t = type;
			this.f = field;
			this.v = value;
			this.vT = valueType;
		}
		
		public Type getType() {
			return this.t;
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

	public enum Type {
		PRIMARY(""),
		AND(" AND "),
		OR(" OR ");
		
		private final String textual;
		
		private Type(String textual) {
			this.textual = textual;
		}
		
		public String getTextual() {
			return this.textual;
		}
	}
	
	public enum ValueType {
		EQUALS(" %1 = '%2' "),
		NOTEQUALS(" %1 != '%2' "),
		EQUALSRAW(" %1 = %2 "),
		NOTEQUALSRAW(" %1 != %2 "),
		GREATERTHAN(" %1 > %2 "),
		GREATERTHANEQUALS(" %1 >= %2 "),
		SMALLERTHAN(" %1 < %2 "),
		SMALLERTHANEQUALS(" %1 <= %2 "),
		LIKE(" %1 LIKE '%2'"),
		LIKESTART(" %1 LIKE '%%2'"),
		LIKEEND(" %1 LIKE '%2%'"),
		LIKEPART(" %1 LIKE '%%2%'"),
		NULL(" %1 IS NULL "),
		NOTNULL(" %1 IS NOT NULL "),
		IN(" %1 IN (%2) "),
		RAW(" %1 ");
		
		private final String textual;
		
		private ValueType(String textual) {
			this.textual = textual;
		}
		
		public String getTextual() {
			return this.textual;
		}
	}
	
	public Group setType(Type t);
	
	public Group add(String field, ValueType vT);
	public Group add(Type t, String field, ValueType vT);
	public Group add(String field, String value);
	public Group add(String field, String value, ValueType vT);
	public Group add(Type t, String field, String value);
	public Group add(Type t, String field, String value, ValueType vT);
	public Group add(Group g);
	
	public Group parse();
	public boolean isParsed();
	public String getParsedGroup();
	
	public Type getType();
	
	public Group merge(Group g);
	
}

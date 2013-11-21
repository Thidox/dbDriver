package nl.giantit.minecraft.database.query;

/**
 *
 * @author Giant
 */
public interface Column {

	public enum DataType {
		INT(" INT(%1) "),
		SMALLINT(" SMALLINT "),
		BIGINT(" BIGINT "),
		DOUBLE(" DOUBLE "),
		VARCHAR(" VARCHAR(%1) "),
		TEXT(" TEXT "),
		DATE(" DATE "),
		TIME(" TIME "),
		DATETIME(" DATETIME "),
		TIMESTAMP(" TIMESTAMP "),
		RAW(" %1 ");
		
		private final String textual;
		
		private DataType(String textual) {
			this.textual = textual;
		}
		
		public String getTextual() {
			return this.textual;
		}
	}
	
	public Column setName(String name);
	public String getName();
	
	public Column setDataType(DataType t);
	public Column setRawDataType(String data);
	
	public Column setLength(int length);
	
	public boolean hasPrimaryKey();
	public Column setPrimaryKey();
	public Column setAutoIncr();
	
	public Column setNull();
	public Column setDefault(String def);
	public Column setRawDefault(String def);
	
	public Column parse();
	public boolean isParsed();
	public String getParsedColumn();
}
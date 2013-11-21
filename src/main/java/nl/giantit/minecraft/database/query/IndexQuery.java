package nl.giantit.minecraft.database.query;

/**
 *
 * @author Giant
 */
public interface IndexQuery extends Query {
	
	public enum Type {
		UNIQUE(" UNIQUE INDEX "),
		INDEX(" INDEX "),
		FULLTEXT(" FULLTEXT INDEX ");
		
		private final String textual;
		
		private Type(String textual) {
			this.textual = textual;
		}
		
		public String getTextual() {
			return this.textual;
		}
	}
	
	public IndexQuery setName(String n);
	
	public IndexQuery setTable(String t);
	
	public IndexQuery setType(Type t);
	
	public IndexQuery addField(String f);
}

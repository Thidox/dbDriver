package nl.giantit.minecraft.database.query;

/**
 *
 * @author Giant
 */
public interface Join {

	public enum Type {
		INNER(" INNER JOIN "),
		LEFT(" LEFT OUTER JOIN "),
		RIGHT(" RIGHT OUTER JOIN "),
		NATURAL(" NATURAL JOIN "),
		NATURALINNER(" NATURAL INNER JOIN "),
		NATURALLEFT(" NATURAL LEFT OUTER JOIN "),
		NATURALRIGHT(" NATURAL RIGHT OUTER JOIN ");
		
		private final String textual;
		
		private Type(String textual) {
			this.textual = textual;
		}
		
		public String getTextual() {
			return this.textual;
		}
	}
	
	public Join setType(Type t);
	
	public Join on(String field, String compareTo);
	
	public Join parse();
	public boolean isParsed();
	public String getParsedJoin();
	
	public Type getType();
	
}

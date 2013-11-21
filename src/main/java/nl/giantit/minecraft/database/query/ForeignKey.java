package nl.giantit.minecraft.database.query;

/**
 *
 * @author Giant
 */
public interface ForeignKey {
	
	public enum Type {
		CASCADE(" CASCADE "),
		RESTRICT(" RESTRICT "),
		NO_ACTION(" NO ACTION "),
		SET_DEFAULT(" SET DEFAULT "),
		SET_NULL(" SET NULL ");
		
		private final String textual;
		
		private Type(String textual) {
			this.textual = textual;
		}
		
		public String getTextual() {
			return this.textual;
		}
	}

	public ForeignKey setName(String n);
	public String getName();
	
	public ForeignKey addLocalField(String f);
	public ForeignKey remoteTable(String t);
	public ForeignKey addRemoteField(String f);
	
	public ForeignKey onUpdate(Type t);
	public ForeignKey onDelete(Type t);
	
	public ForeignKey parse();
	public boolean isParsed();
	public String getParsedFK();
}

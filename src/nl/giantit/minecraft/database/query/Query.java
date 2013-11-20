package nl.giantit.minecraft.database.query;

import nl.giantit.minecraft.database.QueryResult;

/**
 *
 * @author Giant
 */
public interface Query {
	
	public Query parse();
	public boolean isParsed();
	public String getParsedQuery();
	
	public QueryResult exec();
	public QueryResult exec(boolean debug);
	
	public Group createGroup();
}

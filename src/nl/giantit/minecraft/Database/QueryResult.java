package nl.giantit.minecraft.Database;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Giant
 */
public class QueryResult {
	
	private int pointer = 0;
	private int size;
	private ArrayList<HashMap<String, String>> data;
	
	private QueryResult(ArrayList<HashMap<String, String>> data) {
		this.pointer = 0;
		this.data = data;
		this.size = data.size();
	}
	
	public QueryRow getRow() {
		if(data.size() > pointer) {
			QueryRow qR = new QueryRow(data.get(pointer));
			pointer++;
			return qR;
		}
		
		pointer = 0;
		return null;
	}
	
	public QueryRow getRow(int point) {
		if(data.size() > point) {
			QueryRow qR = new QueryRow(data.get(point));
			return qR;
		}
		
		return null;
	}
	
	public void first() {
		this.pointer = 0;
	}
	
	public void last() {
		this.pointer = this.size - 1;
	}
	
	public int size() {
		return this.size;
	}
	
	public ArrayList<HashMap<String, String>> getRawData() {
		return this.data;
	}
	
	public class QueryRow {
		
		private HashMap<String, String> row;
		
		private QueryRow(HashMap<String, String> row) {
			this.row = row;
		}
		
		public String getString(String key) {
			if(row.containsKey(key.toLowerCase())) {
				return row.get(key.toLowerCase());
			}
			
			return null;
		}
		
		public boolean getBoolean(String key) {
			if(row.containsKey(key.toLowerCase())) {
				//return Boolean.parseBoolean(row.get(key));
				String v = row.get(key.toLowerCase());
				return v.equalsIgnoreCase("true") || v.equals("1") || v.equals("yes") || v.equals("y");
			}
			
			return false;
		}
		
		public int getInt(String key) {
			try{
				if(row.containsKey(key.toLowerCase())) {
					return Integer.parseInt(row.get(key.toLowerCase()));
				}
			}catch(NumberFormatException e) {
				return 0;
			}
			
			return 0;
		}
		
		public Integer getInteger(String key) {
			try{
				if(row.containsKey(key.toLowerCase())) {
					return Integer.valueOf(row.get(key.toLowerCase()));
				}
			}catch(NumberFormatException e) {
				return 0;
			}
			
			return 0;
		}
		
		public float getFloat(String key) {
			try{
				if(row.containsKey(key.toLowerCase())) {
					return Float.parseFloat(row.get(key.toLowerCase()));
				}
			}catch(NumberFormatException e) {
				return 0F;
			}
			
			return 0F;
		}
		
		public double getDouble(String key) {
			try{
				if(row.containsKey(key.toLowerCase())) {
					return Double.parseDouble(row.get(key.toLowerCase()));
				}
			}catch(NumberFormatException e) {
				return 0D;
			}
			
			return 0D;
		}
		
		public long getLong(String key) {
			try{
				if(row.containsKey(key.toLowerCase())) {
					return Long.parseLong(row.get(key.toLowerCase()));
				}
			}catch(NumberFormatException e) {
				return 0L;
			}
			
			return 0L;
		}
		
	}
	
	public static QueryResult QR(ArrayList<HashMap<String, String>> data) {
		return new QueryResult(data);
	}
	
}

package cm.aptoide.pt.util.databasecreator;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.database.sqlite.SQLiteDatabase;

public class TableCreator {
	
	public enum SQLiteType{
		INTEGER, TEXT, FLOAT, DATE, REAL
	}

	private class Column {
		
		private SQLiteType type;
		private String name;
		private String dfault;
		
		public Column(SQLiteType type, String name) {
			this.type = type;
			this.name = name;
		}
		
		public Column(SQLiteType type, String name, String dfault) {
			this(type, name);
			this.dfault = dfault;
		}
		
		@Override
		public String toString() {
			return name + " " + type + (dfault != null ? " DEFAULT " + dfault : "");
		}
		
	}
	
	private SQLiteDatabase database;
	
	public class TableBuilder{
		
		private ArrayList<Column> columns = new ArrayList<Column>();
		private String tableName;
		
		public TableBuilder(String tableName) {
			this.tableName = tableName;
		}
		
		public TableBuilder addColumn(SQLiteType type, String name) {
			columns.add(new Column(type, name));
			return this;
		}
		
		public TableBuilder addColumn(SQLiteType type, String name, String dfault) {
			columns.add(new Column(type, name, dfault));
			return this;
		}

		public void createTable() throws IllegalArgumentException {
			if(tableName==null){
				throw new IllegalArgumentException("No table name especified");
			}
			if(columns.size()==0){
				throw new IllegalArgumentException("No columns especified");
			}
			database.execSQL("CREATE TABLE " + tableName + " " + columnsToString(columns));
		}
		
	}
	
	public TableCreator(SQLiteDatabase database) {
		this.database = database;
	}

	public TableBuilder newTable(String tableName) {
		return new TableBuilder(tableName);
	}
	
	private String columnsToString(Iterable<Column> columns) {
		StringBuilder sb = new StringBuilder();
		Iterator<Column> it = columns.iterator();
		for(;;){
			Column column = it.next();
			sb.append(column);
			if(!it.hasNext()){
				return sb.toString();
			}
			sb.append(", ");
		}
	}
	
}

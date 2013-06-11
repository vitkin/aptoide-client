package cm.aptoide.pt.util.databasecreator;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;


public class TableCreator {

	private SQLiteDatabase database;

	public class TableBuilder{

		private ArrayList<Column> columns = new ArrayList<Column>();
		private String tableName;

		public TableBuilder(String tableName) {
			this.tableName = tableName;
		}

		public TableBuilder addColumn(Column column) {
			columns.add(column);
			return this;
		}

		public void createTable() throws IllegalArgumentException {
			if(tableName==null){
				throw new IllegalArgumentException("No table name especified");
			}
			if(columns.size()==0){
				throw new IllegalArgumentException("No columns especified");
			}
			String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + columnsToString(columns) + ")";

			Log.d("TableCreator","Executing SQL: " + sql);

			database.execSQL(sql);
		}

		private String columnsToString(ArrayList<Column> columns) {
			StringBuilder sb = new StringBuilder();
			Iterator<Column> it = columns.iterator();
			for(;;){
				Column column = it.next();
				sb.append(column);
				if(!it.hasNext()){
					return sb.toString() + columnsPrimaryKeys(columns);
				}
				sb.append(", ");
			}
		}

		private String columnsPrimaryKeys(ArrayList<Column> columns) {
			Iterator<Column> it = columns.iterator();
			ArrayList<Column> primaryKeys = new ArrayList<Column>();
			for(;;){
				Column column = it.next();
				if(column.isPrimaryKey()){
					primaryKeys.add(column);
				}

				if(!it.hasNext()){
					if(primaryKeys.isEmpty()){
						return "";
					}else{
						return ", PRIMARY KEY(" + columnsPrimaryKeysToString(primaryKeys) + ")";
					}

				}

			}
		}

		private String columnsPrimaryKeysToString(ArrayList<Column> primaryKeys){
			StringBuilder sb = new StringBuilder();
			Iterator<Column> it = primaryKeys.iterator();
			for(;;){
				Column column = it.next();
				sb.append(column.getName());
				if(!it.hasNext()){
					return sb.toString();
				}
				sb.append(", ");
			}

		}

	}

	public TableCreator(SQLiteDatabase database) {
		this.database = database;
	}

	public TableBuilder newTable(String tableName) {
		return new TableBuilder(tableName);
	}

}

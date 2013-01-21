package cm.aptoide.pt.util.databasecreator;



public class Column {
	
	public enum SQLiteType{
		INTEGER, TEXT, FLOAT, DATE, REAL
	}
	
	public enum OnConflict{
		REPLACE, IGNORE, ABORT
	}
	private SQLiteType type;
	private String name;
	private String dfault;
	private boolean unique;
	private OnConflict onConflict;
	private boolean primaryKey;
	
	public Column(String name) {
		this.name = name;
	}
	
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
		return name + (type != null ? " " +type : "") + (dfault != null ? " DEFAULT '" + dfault  + "'": "") 
				+ (unique?" UNIQUE ON CONFLICT " + onConflict.name():"");
	}

	public Column setUnique(OnConflict onConflict) {
		this.unique = true;
		this.onConflict = onConflict;
		return this;
	}

	/**
	 * @return the unique
	 */
	public boolean isUnique() {
		return unique;
	}

	public Column setPrimaryKey() {
		this.primaryKey = true;
		return this;
	}
	
	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public String getName() {
		return name;
	}

}

package com.phase3.genx.db;

import java.sql.*;
import java.util.*;

/**
 * Creator:    cgh
 * Created On: Aug 24, 2007 11:59:05 PM
 */
public class DbTable {
	boolean sequenceTable = false;
	String schema;
	String tableName;
	HashMap<String, DbColumn> columns = new HashMap<String, DbColumn>();

	ArrayList<DbColumn> referring = new ArrayList<DbColumn>();

	public String getClassName() {
		return Db2Java.getSafeClassName(tableName);
	}
	public String getFieldName() {
		return Db2Java.getSafeFieldName(tableName);
	}
	public Collection<DbColumn> getColumns() {
		return columns.values();
	}
	public String getPrimaryKeyFieldName() {
		for(DbColumn col : columns.values()) {
			if (col.isPk()) {
				return col.getFieldName();
			}
		}
		return null;
	}
	public DbColumn getColumn(String key) {
		return columns.get(key);
	}

	public boolean containsColumn(String key) {
		return columns.containsKey(key);
	}

	public String getTableName() {
		return tableName;
	}	
	DbTable(ResultSet row) throws SQLException {
		/*
   1.  TABLE_CAT String => table catalog (may be null)
   2. TABLE_SCHEM String => table schema (may be null)
   3. TABLE_NAME String => table name
   4. TABLE_TYPE String => table type. Typical types are "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
   5. REMARKS String => explanatory comment on the table
   6. TYPE_CAT String => the types catalog (may be null)
   7. TYPE_SCHEM String => the types schema (may be null)
   8. TYPE_NAME String => type name (may be null)
   9. SELF_REFERENCING_COL_NAME String => name of the designated "identifier" column of a typed table (may be null)
  10. REF_GENERATION String => specifies how values in SELF_REFERENCING_COL_NAME are created. Values are "SYSTEM", "USER", "DERIVED". (may be null)
		 */
		tableName = row.getString("TABLE_NAME");
		schema = row.getString("TABLE_SCHEM");
	}

	public void fetchColumns(DatabaseMetaData metadata) throws SQLException {
		HashMap<String,String> pkColNames = new HashMap<String,String>();
		ResultSet pkCols = metadata.getPrimaryKeys(null, null, tableName);
		/*
   1.  TABLE_CAT String => table catalog (may be null)
   2. TABLE_SCHEM String => table schema (may be null)
   3. TABLE_NAME String => table name
   4. COLUMN_NAME String => column name
   5. KEY_SEQ short => sequence number within primary key
   6. PK_NAME String => primary key name (may be null)
   		 */
		while(pkCols.next()) {
			pkColNames.put(pkCols.getString("COLUMN_NAME"), "");
		}
		pkCols.close();

		ResultSet cols = metadata.getColumns(null, null, tableName, null);
		while(cols.next()) {
			DbColumn col = new DbColumn(this, pkColNames, cols);
			columns.put(col.getName(), col);
			if (!sequenceTable) {
				sequenceTable = col.isSequencePk();
			}
		}
		cols.close();
	}

	public boolean isSequenceTable() {
		return sequenceTable;
	}

	public void addReferringColumn(DbColumn col) {
		referring.add(col);
	}
	public ArrayList<DbColumn> getReferringColumnList() {
		return referring;
	}
}

package com.phase3.genx.db;

import java.sql.*;
import java.util.*;

/**
 * Creator:    cgh
 * Created On: Aug 25, 2007 12:00:44 AM
 */
public class DbColumn {
	DbTable table;
	boolean isPk = false;
	boolean nullable = false;
	String name;
	String dbDataType;
	int datatype;
	int size;
	int decimals;
	DbTable fkTable = null;
	DbColumn fkColumn = null;


	public DbTable getTable() {
		return table;
	}

	public boolean isPk() {
		return isPk;
	}
	public boolean isVersionField() {
		return name.length() > 5 && name.substring(4).equalsIgnoreCase("update_date");
	}
	public boolean isSequencePk() {
		return name.toLowerCase().endsWith("uid");
	}
	public boolean isNullable() {
		return nullable;
	}

	public String getName() {
		return name;
	}

	public String getDbDataType() {
		return dbDataType;
	}

	public int getDatatype() {
		return datatype;
	}

	public int getSize() {
		return size;
	}

	public int getDecimals() {
		return decimals;
	}

	public DbTable getFkTable() {
		return fkTable;
	}

	public void setFkTable(DbTable fkTable) {
		this.fkTable = fkTable;
	}

	public DbColumn getFkColumn() {
		return fkColumn;
	}

	public void setFkColumn(DbColumn fkColumn) {
		this.fkColumn = fkColumn;
	}
	public boolean hasFkColumn() {
		return this.fkColumn != null;
	}
	public DbColumn(DbTable parent, HashMap<String,String> pkColNames, ResultSet resultSet) throws SQLException {
		table = parent;
		/*
       1.  TABLE_CAT String => table catalog (may be null)
       2. TABLE_SCHEM String => table schema (may be null)
       3. TABLE_NAME String => table name
       4. COLUMN_NAME String => column name
       5. DATA_TYPE int => SQL type from java.sql.Types
       6. TYPE_NAME String => Data source dependent type name, for a UDT the type name is fully qualified
       7. COLUMN_SIZE int => column size. For char or date types this is the maximum number of characters, for numeric or decimal types this is precision.
       8. BUFFER_LENGTH is not used.
       9. DECIMAL_DIGITS int => the number of fractional digits
      10. NUM_PREC_RADIX int => Radix (typically either 10 or 2)
      11. NULLABLE int => is NULL allowed.
              * columnNoNulls - might not allow NULL values
              * columnNullable - definitely allows NULL values
              * columnNullableUnknown - nullability unknown
      12. REMARKS String => comment describing column (may be null)
      13. COLUMN_DEF String => default value (may be null)
      14. SQL_DATA_TYPE int => unused
      15. SQL_DATETIME_SUB int => unused
      16. CHAR_OCTET_LENGTH int => for char types the maximum number of bytes in the column
      17. ORDINAL_POSITION int => index of column in table (starting at 1)
      18. IS_NULLABLE String => "NO" means column definitely does not allow NULL values; "YES" means the column might allow NULL values. An empty string means nobody knows.
      19. SCOPE_CATLOG String => catalog of table that is the scope of a reference attribute (null if DATA_TYPE isn't REF)
      20. SCOPE_SCHEMA String => schema of table that is the scope of a reference attribute (null if the DATA_TYPE isn't REF)
      21. SCOPE_TABLE String => table name that this the scope of a reference attribure (null if the DATA_TYPE isn't REF)
      22. SOURCE_DATA_TYPE short => source type of a distinct type or user-generated Ref type, SQL type from java.sql.Types (null if DATA_TYPE isn't DISTINCT or user-generated REF)
		 */
		name = resultSet.getString("COLUMN_NAME");
		dbDataType = resultSet.getString("TYPE_NAME");
		datatype = resultSet.getInt("DATA_TYPE");
		size  = resultSet.getInt("COLUMN_SIZE");
		decimals  = resultSet.getInt("DECIMAL_DIGITS");
		nullable = "YES".equalsIgnoreCase(resultSet.getString("IS_NULLABLE"));
		if (pkColNames.get(name) != null) {
			isPk = true;
		}
	}
	public String getFieldName() {
		return Db2Java.getSafeFieldName(name);
	}
	public String getMethodFieldName() {
		return Db2Java.getSafeClassName(name);
	}
	public String buildJdbcGetter(String resultSetName) throws Exception {
		String s = null;
		switch (datatype) {
			case Types.BOOLEAN:
				s =  "Boolean";
				break;
			case Types.DATE:
				s =  "Date";
				break;
			case Types.TIMESTAMP:
				s =  "Timestamp";
				break;
			case -2: // some other mssql "timestamp"
				s =  "Timestamp";
				break;
			case Types.DOUBLE:
			case Types.REAL:
				s =  "Double";
				break;
			case Types.FLOAT:
				s =  "Float";
				break;
			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.BIGINT:
			case Types.BIT:
			case Types.TINYINT:
				s =  "Integer";
				break;
			case Types.DECIMAL:
			case Types.NUMERIC:
				if (decimals == 0) {
					s =  "Long";
				} else {
					s =  "BigDecimal";
				}
				break;
			case Types.CHAR:
			case Types.LONGVARCHAR:
			case Types.VARCHAR:
				s =  "String";
				break;
			case 2005: //NVARCHAR, CLOB
				s =  "Clob";
				break;
			default:
				s =  "String";
				break;
		}
		if (s.equals("Timestamp")) {
			return "new java.util.Date(" + resultSetName + ".get" + s + " (\"" + this.name + "\").getTime())";
		} else {
			return resultSetName + ".get" + s + " (\"" + this.name + "\")";
		}
	}
	public String getClassType() throws Exception {
		switch(datatype) {
			case Types.BOOLEAN:
						return "Boolean";
			case Types.DATE:
						return "java.util.Date";
			case Types.TIMESTAMP:
						return "java.util.Date";
			case -2: // some other mssql "timestamp"
						return "Timestamp";
			case Types.DOUBLE:
			case Types.REAL:
						return "Double";
			case Types.FLOAT:
						return "Float";
			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.BIGINT:
			case Types.BIT:
			case Types.TINYINT:
						return "Integer";
			case Types.DECIMAL:
			case Types.NUMERIC:
				if (decimals == 0) {
					return "Long";
				} else {
						return "BigDecimal";
				}
			case Types.CHAR:
			case Types.LONGVARCHAR:
			case Types.VARCHAR:
				return "String";
			case 2005: //NVARCHAR, CLOB
					return "Clob";
			default:
					return "String";
		}
		//throw new Exception ("Unhandled column datatype:" + datatype);
	}
}

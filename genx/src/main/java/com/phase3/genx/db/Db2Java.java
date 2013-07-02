package com.phase3.genx.db;

import org.apache.commons.cli.*;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.*;
import org.apache.velocity.*;
import org.apache.velocity.app.*;

import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.Date;
import java.util.*;

/**
 * '%m/%d/%Y %h:%i:%s %p'
 * Creator:    cgh
 * Created On: Aug 24, 2007 11:53:45 PM
 */
public class Db2Java {
	private static HashMap<String, String> ignoreMap = new HashMap<String, String>();
	private static HashMap<String, String> illegalVarMap = new HashMap<String, String>();

	static {
		ignoreMap.put("sysdiagrams", "sysdiagrams");
		illegalVarMap.put("case", "caze");
		illegalVarMap.put("interface", "iface");
		illegalVarMap.put("Object", "Obj");
	}

    private String dburl;
    private String dbuser;
    private String dbpass;
    private String schema;
    private String table = "%";
    private String packageName;
    private String outputFolder;
    private String templateFilename;


	public static void main (String[] args) {
        Options options = new Options();
        options.addOption(OptionBuilder.withArgName("url").hasArg().withDescription("url to use to connect").create ("url"));
        options.addOption(OptionBuilder.withArgName("name").hasArg().withDescription("username to database").create ("user"));
        options.addOption(OptionBuilder.withArgName("password").hasArg().withDescription("password to database").create ("password"));
        options.addOption(OptionBuilder.withArgName("name").hasArg().withDescription("schema to process").create ("schema"));

        options.addOption(OptionBuilder.withArgName("package").hasArg().withDescription("java package to assign").create ("package"));
        options.addOption(OptionBuilder.withArgName("file").hasArg().withDescription("folder to put files in").create ("output"));
        options.addOption(OptionBuilder.withArgName("file").hasArg().withDescription("template file").create("template"));

        CommandLineParser parser = new BasicParser();
        Db2Java d = new Db2Java();
        try {
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("url")) {
                d.dburl = line.getOptionValue("url");
            } else {
                throw new ParseException("url required");
            }

            if (line.hasOption("user")) {
                d.dbuser= line.getOptionValue("user");
            } else {
                throw new ParseException("user required");
            }

            if (line.hasOption("password")) {
                d.dbpass= line.getOptionValue("password");
            } else {
                throw new ParseException("password required");
            }
            if (line.hasOption("schema")) {
                d.schema = line.getOptionValue("schema");
            } else {
                throw new ParseException("schema required");
            }

        } catch (ParseException e) {
            System.err.println(e.getMessage());
            HelpFormatter help = new HelpFormatter();
            help.printHelp(Db2Java.class.getName(), options);
            System.exit(-1);
        }
		try {
			d.build(args);
		} catch (Exception e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}
	HashMap<String, DbTable> tables;
	public void build(String[] args) throws Exception {
		String vendor = null;
		String url = args[0];

		if (url.contains("sqlserver")) {
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
			vendor = "sqlserver";
		} else if (url.contains("mysql")) {
			Class.forName("com.mysql.jdbc.Driver");
			vendor = "mysql";
		} else if (url.contains("oracle")) {
			Class.forName("oracle.jdbc.OracleDriver");
			vendor = "oracle";
		}
		Connection conn = null;
		try {
            String filename = "Class.java.vm";
			conn = DriverManager.getConnection(url, args[1], args[2]);
			processSchema(conn, conn.getMetaData(), args[3], args[4]); // use % for all
			//TODO produce(args[5], args[6], vendor);
			//TODO produceBusinessObject(args[5], args[6], vendor);

		} finally {
			if (conn != null) {
				conn.close();
			}
		}


	}

	private void produce(String filename, String pkg, String outputDir, String vendor) {
		outputDir = outputDir.replace('\\', '/');

		File webTemplateFile = new File(filename);
		String webTemplateString = null;
		try {
			Velocity.init();
			webTemplateString = FileUtils.readFileToString(webTemplateFile, null);
		} catch (Exception e) {
			throw new RuntimeException("Error reading template.", e);
		}

		VelocityContext velotex = new VelocityContext();

		velotex.put("vendor", vendor);
		velotex.put("package", pkg);
		velotex.put("currentDate", DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.FULL, Locale.getDefault()).format(new Date()));


		try {
			for(DbTable table : tables.values()) {
				//if (table.getTableName().equals("CCU_CAMPAIGN_CUSTOMER")) {
					velotex.put("className", getClassName(table.getTableName()));
					velotex.put("table", table);
					String fullFilePath = outputDir + '/' + getClassName(table.getTableName()) + ".java";
					writeFile(fullFilePath, velotex, webTemplateString, true);
				//}
			}
		} catch (Exception e) {
			throw new RuntimeException("Error writing model for schema.", e);
		}
	}

	private void produceBusinessObject(String filename, String pkg, String outputDir, String vendor) {
		outputDir = outputDir.replace('\\', '/');

		File webTemplateFile = new File(filename);
		String webTemplateString = null;
		try {
			Velocity.init();
			webTemplateString = FileUtils.readFileToString(webTemplateFile, null);
		} catch (Exception e) {
			throw new RuntimeException("Error reading template.", e);
		}

		VelocityContext velotex = new VelocityContext();

		velotex.put("vendor", vendor);
		velotex.put("modelPackage", pkg);

		pkg = pkg.replace(".model", ".bo");
		velotex.put("package", pkg);
		velotex.put("currentDate", DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.FULL, Locale.getDefault()).format(new Date()));


		try {
			for (DbTable table : tables.values()) {
				//if (table.getTableName().equals("CCU_CAMPAIGN_CUSTOMER")) {
				velotex.put("className", getClassName(table.getTableName()));
				velotex.put("table", table);
				outputDir = outputDir.replace("model", "bo");
				String fullFilePath = outputDir + '/' + getClassName(table.getTableName()) + "BusinessObject.java";
				writeFile(fullFilePath, velotex, webTemplateString, false);
				//}
			}
		} catch (Exception e) {
			throw new RuntimeException("Error writing model for schema.", e);
		}
	}

	private static void writeFile(String fileName,
			VelocityContext velotex, String templateString, boolean overwrite) throws Exception {

		File dir = new File(fileName.substring(0,fileName.lastIndexOf("/")));
		dir.mkdirs();
		File file = new File(fileName);
		if (!overwrite) {
			if(file.exists()) {
				return;
			}
		}
		FileWriter writer = new FileWriter(file);
		try {
			Velocity.evaluate(velotex, writer, "writer", templateString);
		} finally {
			writer.close();
		}

	}
	public static String getClassName (String dbName) {
		if (dbName.length() > 5) {
			dbName= dbName.substring(4);
		}

		StringBuffer sb = new StringBuffer(dbName.length());
		boolean wasSeparator = true;
		for(char c : dbName.toCharArray()) {
			if (Character.isLetterOrDigit(c)) {
				if (wasSeparator) {
					sb.append(Character.toUpperCase(c));
					wasSeparator = false;
				} else {
					sb.append(Character.toLowerCase(c));
				}
			} else {
				wasSeparator = true;
			}
		}
		return sb.toString();
	}
	public static String getSafeClassName(String dbName) {
		String s = getClassName(dbName);
		String replace = illegalVarMap.get(s);
		if (replace != null) {
			s = replace;
		}
		return s;
	}
	public static String getFieldName (String dbName) {
		if (dbName.length() > 5) {
			dbName= dbName.substring(4);
		}
		StringBuffer sb = new StringBuffer(dbName.length());
		boolean wasSeparator = false;
		boolean isFirst = true;
		for(char c : dbName.toCharArray()) {
			if (Character.isLetterOrDigit(c)) {
				if (wasSeparator) {
					sb.append(Character.toUpperCase(c));
					wasSeparator = false;
				} else {
					if(isFirst) {
						sb.append(Character.toLowerCase(c));
						isFirst = false;
					} else {
						sb.append(Character.toLowerCase(c));
					}
				}
			} else {
				wasSeparator = true;
			}
		}
		return sb.toString();
	}
	public static String getSafeFieldName(String dbName) {
		String s = getFieldName(dbName);
		String replace = illegalVarMap.get(s);
		if (replace != null) {
			s = replace;
		}
		return s;
	}

	private void processSchema(Connection conn, DatabaseMetaData metaData, String schema, String table) throws Exception {
		tables = new HashMap<String, DbTable>();

		// PASS #1
		ResultSet tableSet = metaData.getTables(null, null, table, null);
		while(tableSet.next()) {
			if ("TABLE".equalsIgnoreCase(tableSet.getString("TABLE_TYPE"))) {
				String tSchema = tableSet.getString("TABLE_SCHEM");
//				if (!tSchema.equalsIgnoreCase(schema)) {
//					continue;
//				}

				String name = tableSet.getString("TABLE_NAME");
				if (ignoreMap.containsKey(name)) {
					continue;
				}
				if (name.contains("$")) {
					continue;
				}
				if (name.length() < 5 || !(name.charAt(3) == '_')) {
					continue;
				}
//				if (name.startsWith("SDO_")) {
//					continue;
//				}
				DbTable t = new DbTable(tableSet);
				tables.put(t.getTableName(), t);
			}
		}
		tableSet.close();
		for(DbTable t : tables.values()) {
			t.fetchColumns(metaData);
		}

		// PASS #2
		String sql = "select * from information_schema.key_column_usage where CONSTRAINT_SCHEMA='"+ schema+"' ";
		ResultSet rs = null;//TODO JdbcUtil.execute(conn, sql);
		while (rs.next()) {
			String keyName = rs.getString("CONSTRAINT_NAME");
			String fkTableName = rs.getString("TABLE_NAME");
			String fkColumnName = rs.getString("COLUMN_NAME");
			String tableName = rs.getString("REFERENCED_TABLE_NAME");
			String columnName = rs.getString("REFERENCED_COLUMN_NAME");
			if (tableName != null) {
				DbTable t = tables.get(tableName);

				DbColumn column = t.getColumn(columnName);
				DbTable fTable = tables.get(fkTableName);
				if (fTable != null) {
					DbColumn fCol = fTable.getColumn(fkColumnName);
					fCol.setFkTable(t);
					fCol.setFkColumn(column);
					t.addReferringColumn(fCol);
				}
			}
		}

		/*
   1.  PKTABLE_CAT String => primary key table catalog (may be null)
   2. PKTABLE_SCHEM String => primary key table schema (may be null)
   3. PKTABLE_NAME String => primary key table name
   4. PKCOLUMN_NAME String => primary key column name
   5. FKTABLE_CAT String => foreign key table catalog (may be null) being exported (may be null)
   6. FKTABLE_SCHEM String => foreign key table schema (may be null) being exported (may be null)
   7. FKTABLE_NAME String => foreign key table name being exported
   8. FKCOLUMN_NAME String => foreign key column name being exported
   9. KEY_SEQ short => sequence number within foreign key
  10. UPDATE_RULE short => What happens to foreign key when primary is updated:
          * importedNoAction - do not allow update of primary key if it has been imported
          * importedKeyCascade - change imported key to agree with primary key update
          * importedKeySetNull - change imported key to NULL if its primary key has been updated
          * importedKeySetDefault - change imported key to default values if its primary key has been updated
          * importedKeyRestrict - same as importedKeyNoAction (for ODBC 2.x compatibility)
  11. DELETE_RULE short => What happens to the foreign key when primary is deleted.
          * importedKeyNoAction - do not allow delete of primary key if it has been imported
          * importedKeyCascade - delete rows that import a deleted key
          * importedKeySetNull - change imported key to NULL if its primary key has been deleted
          * importedKeyRestrict - same as importedKeyNoAction (for ODBC 2.x compatibility)
          * importedKeySetDefault - change imported key to default if its primary key has been deleted
  12. FK_NAME String => foreign key name (may be null)
  13. PK_NAME String => primary key name (may be null)
  14. DEFERRABILITY short => can the evaluation of foreign key constraints be deferred until commit
          * importedKeyInitiallyDeferred - see SQL92 for definition
          * importedKeyInitiallyImmediate - see SQL92 for definition
          * importedKeyNotDeferrable - see SQL92 for definition

		 */
//		for(DbTable t : tables.values()) {
//			ResultSet xrefSet = metaData.getExportedKeys(null, null, t.getTableName());
//			while(xrefSet.next()) {
//				String pColumnName = xrefSet.getString("PKCOLUMN_NAME");
//				String fTableName = xrefSet.getString("FKTABLE_NAME");
//				String fColumnName = xrefSet.getString("FKCOLUMN_NAME");
//				// ex: Customer.uid
//				DbColumn column = t.getColumn(pColumnName);
//				// is linked by CustomerIpAddress.customer
//				DbTable fTable = tables.get(fTableName);
//				if (fTable != null) {
//
//					DbColumn fCol = fTable.getColumn(fColumnName);
//
//
//					fCol.setFkTable(t);
//					fCol.setFkColumn(column);
//
//					t.addReferringColumn(fCol);
//				}
//			}
//			xrefSet.close();

//		}
	}
}

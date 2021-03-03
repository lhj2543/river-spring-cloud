package com.river.site.dataSource.dbtool.provider.db.table;

import com.river.site.dataSource.dbtool.GeneratorConstants;
import com.river.site.dataSource.dbtool.GeneratorProperties;
import com.river.site.dataSource.dbtool.provider.db.DataSourceProvider;
import com.river.site.dataSource.dbtool.util.BeanHelper;
import com.river.site.dataSource.dbtool.util.BuilderLogger;
import com.river.site.dataSource.dbtool.util.DBHelper;
import com.river.site.dataSource.dbtool.util.FileHelper;
import com.river.site.dataSource.dbtool.util.StringHelper;
import com.river.site.dataSource.dbtool.util.XMLHelper;
import com.river.site.dataSource.dbtool.util.XMLHelper.NodeData;
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TableFactory
{
  private static TableFactory instance = null;
  private String schema;
  private String catalog;
  private List<TableFactoryListener> tableFactoryListeners = new ArrayList();

  private TableFactory(String schema, String catalog) {
    this.schema = schema;
    this.catalog = catalog;
  }

  public static synchronized TableFactory getInstance() {
    instance = new TableFactory(GeneratorProperties.getNullIfBlank(GeneratorConstants.JDBC_SCHEMA), GeneratorProperties.getNullIfBlank(GeneratorConstants.JDBC_CATALOG));
    return instance;
  }

  public List<TableFactoryListener> getTableFactoryListeners() {
    return this.tableFactoryListeners;
  }

  public void setTableFactoryListeners(List<TableFactoryListener> tableFactoryListeners)
  {
    this.tableFactoryListeners = tableFactoryListeners;
  }

  public boolean addTableFactoryListener(TableFactoryListener o) {
    return this.tableFactoryListeners.add(o);
  }

  public void clearTableFactoryListener() {
    this.tableFactoryListeners.clear();
  }

  public boolean removeTableFactoryListener(TableFactoryListener o) {
    return this.tableFactoryListeners.remove(o);
  }

  public String getCatalog() {
    return this.catalog;
  }

  public String getSchema() {
    return this.schema;
  }

  public List getAllTables() {
    Connection conn = DataSourceProvider.getConnection();
    try {
      List tables = new TableCreateProcessor(conn, getSchema(), getCatalog()).getAllTables();
      for (Object localObject1 = tables.iterator(); ((Iterator)localObject1).hasNext(); ) { Table t = (Table)((Iterator)localObject1).next();
        dispatchOnTableCreatedEvent(t);
      }
      return tables;
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      GeneratorProperties.clear();
      DBHelper.close(conn);
    }
  }

  private void dispatchOnTableCreatedEvent(Table t) {
    for (TableFactoryListener listener : this.tableFactoryListeners) {
      listener.onTableCreated(t);
    }
  }

  public Table getTable(String tableName)
  {
    return getTable(getSchema(), tableName);
  }

  private Table getTable(String schema, String tableName) {
    return getTable(getCatalog(), schema, tableName);
  }

  private Table getTable(String catalog, String schema, String tableName) {
    Table t = null;
    try {
      t = _getTable(catalog, schema, tableName);
      if ((t == null) && (!tableName.equals(tableName.toUpperCase()))) {
        t = _getTable(catalog, schema, tableName.toUpperCase());
      }
      if ((t == null) && (!tableName.equals(tableName.toLowerCase()))) {
        t = _getTable(catalog, schema, tableName.toLowerCase());
      }
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (t == null) {
      Connection conn = DataSourceProvider.getConnection();
      try {
        throw new NotFoundTableException(new StringBuilder().append("not found table with give name:").append(tableName).append(DatabaseMetaDataUtils.isOracleDataBase(DatabaseMetaDataUtils.getMetaData(conn)) ? new StringBuilder().append(" \n databaseStructureInfo:").append(DatabaseMetaDataUtils.getDatabaseStructureInfo(DatabaseMetaDataUtils.getMetaData(conn), schema, catalog)).toString() : "").append("\n current ").append(DataSourceProvider.getDataSource()).append(" current schema:").append(getSchema()).append(" current catalog:").append(getCatalog()).toString());
      } finally {
        DBHelper.close(conn);
      }
    }
    dispatchOnTableCreatedEvent(t);
    return t;
  }

  private Table _getTable(String catalog, String schema, String tableName)
    throws SQLException
  {
    if ((tableName == null) || (tableName.trim().length() == 0)) {
      throw new IllegalArgumentException("tableName must be not empty");
    }
    catalog = StringHelper.defaultIfEmpty(catalog, null);
    schema = StringHelper.defaultIfEmpty(schema, null);

    Connection conn = DataSourceProvider.getConnection();
    DatabaseMetaData dbMetaData = conn.getMetaData();
    ResultSet rs = dbMetaData.getTables(catalog, schema, tableName, null);
    try {
      if (rs.next()) {
        Table table = new TableCreateProcessor(conn, getSchema(), getCatalog()).createTable(rs, true);
        return table;
      }
    } finally {
      DBHelper.close(conn, rs);
    }
    return null;
  }

  public static class DatabaseMetaDataUtils
  {
    public static boolean isOracleDataBase(DatabaseMetaData metadata)
    {
      try
      {
        boolean ret = false;

        return metadata.getDatabaseProductName().toLowerCase()
          .indexOf("oracle") != 
          -1;
      } catch (SQLException s) {
      }
      return false;
    }

    public static boolean isHsqlDataBase(DatabaseMetaData metadata)
    {
      try {
        boolean ret = false;

        return metadata.getDatabaseProductName().toLowerCase()
          .indexOf("hsql") != 
          -1;
      } catch (SQLException s) {
      }
      return false;
    }

    public static boolean isMysqlDataBase(DatabaseMetaData metadata)
    {
      try {
        boolean ret = false;

        return metadata.getDatabaseProductName().toLowerCase()
          .indexOf("mysql") != 
          -1;
      } catch (SQLException s) {
      }
      return false;
    }

    public static DatabaseMetaData getMetaData(Connection connection)
    {
      try
      {
        return connection.getMetaData();
      } catch (SQLException e) {
        throw new RuntimeException("cannot get DatabaseMetaData", e);
      }
    }

    public static String getDatabaseStructureInfo(DatabaseMetaData metadata, String schema, String catalog) {
      ResultSet schemaRs = null;
      ResultSet catalogRs = null;
      String nl = System.getProperty("line.separator");
      StringBuffer sb = new StringBuffer(nl);

      sb.append("Configured schema:").append(schema).append(nl);
      sb.append("Configured catalog:").append(catalog).append(nl);
      try
      {
        schemaRs = metadata.getSchemas();
        sb.append("Available schemas:").append(nl);
        while (schemaRs.next()) {
          sb.append("  ").append(schemaRs.getString("TABLE_SCHEM")).append(nl);
        }
      }
      catch (SQLException e2) {
        BuilderLogger.warn("Couldn't get schemas", e2);
        sb.append("  ?? Couldn't get schemas ??").append(nl);
      } finally {
        DBHelper.close(schemaRs);
      }
      try
      {
        catalogRs = metadata.getCatalogs();
        sb.append("Available catalogs:").append(nl);
        while (catalogRs.next()) {
          sb.append("  ").append(catalogRs.getString("TABLE_CAT")).append(nl);
        }
      }
      catch (SQLException e2) {
        BuilderLogger.warn("Couldn't get catalogs", e2);
        sb.append("  ?? Couldn't get catalogs ??").append(nl);
      } finally {
        DBHelper.close(catalogRs);
      }
      return sb.toString();
    }
  }

  static class ExecuteSqlHelper
  {
    public static String queryForString(Connection conn, String sql)
    {
      Statement s = null;
      ResultSet rs = null;
      try {
        s = conn.createStatement();
        rs = s.executeQuery(sql);
        String str1;
        if (rs.next()) {
          return rs.getString(1);
        }
        return null;
      } catch (SQLException e) {
        e.printStackTrace();
        return null;
      } finally {
        DBHelper.close(null, s, rs);
      }
    }
  }

  public static class TableOverrideValuesProvider
  {
    private static Map getTableConfigValues(String tableSqlName)
    {
      XMLHelper.NodeData nd = getTableConfigXmlNodeData(tableSqlName);
      if (nd == null) {
        return new HashMap();
      }
      return nd == null ? new HashMap() : nd.attributes;
    }

    private static Map getColumnConfigValues(Table table, Column column) {
      XMLHelper.NodeData root = getTableConfigXmlNodeData(table.getSqlName());
      if (root != null) {
        for (XMLHelper.NodeData item : root.childs) {
          if ((item.nodeName.equals("column")) && 
            (column.getSqlName().equalsIgnoreCase((String)item.attributes.get("sqlName")))) {
            return item.attributes;
          }
        }
      }

      return new HashMap();
    }

    private static XMLHelper.NodeData getTableConfigXmlNodeData(String tableSqlName) {
      XMLHelper.NodeData nd = getTableConfigXmlNodeData0(tableSqlName);
      if (nd == null) {
        nd = getTableConfigXmlNodeData0(tableSqlName.toLowerCase());
        if (nd == null) {
          nd = getTableConfigXmlNodeData0(tableSqlName.toUpperCase());
        }
      }
      return nd;
    }

    private static XMLHelper.NodeData getTableConfigXmlNodeData0(String tableSqlName) {
      try {
        File file = FileHelper.getFileByClassLoader("generator_config/table/" + tableSqlName + ".xml");
        BuilderLogger.trace("getTableConfigXml() load nodeData by tableSqlName:" + tableSqlName + ".xml");
        return new XMLHelper().parseXML(file);
      } catch (Exception e) {
        BuilderLogger.trace("not found config xml for table:" + tableSqlName + ", exception:" + e);
      }return null;
    }
  }

  public static class TableCreateProcessor
  {
    private Connection connection;
    private String catalog;
    private String schema;

    public String getCatalog()
    {
      return this.catalog;
    }

    public String getSchema() {
      return this.schema;
    }

    public TableCreateProcessor(Connection connection, String schema, String catalog)
    {
      this.connection = connection;
      this.schema = schema;
      this.catalog = catalog;
    }

    public Table createTable(ResultSet rs, boolean flg) throws SQLException {
      long start = System.currentTimeMillis();
      String tableName = null;
      try {
        ResultSetMetaData rsMetaData = rs.getMetaData();
        String schemaName = rs.getString("TABLE_SCHEM") == null ? "" : rs.getString("TABLE_SCHEM");
        tableName = rs.getString("TABLE_NAME");
        String tableType = rs.getString("TABLE_TYPE");
        String remarks = rs.getString("REMARKS");

        if ((remarks == null) && (DatabaseMetaDataUtils.isOracleDataBase(this.connection.getMetaData()))) {
          remarks = getOracleTableComments(tableName);
        }

        Table table = new Table();
        table.setTableType(tableType);
        table.setSchema(this.schema);
        table.setCatalog(this.catalog);
        table.setSqlName(tableName);
        table.setRemarks(remarks);
        String[] ownerAndTableName;
        if (("SYNONYM".equals(tableType)) && (DatabaseMetaDataUtils.isOracleDataBase(this.connection.getMetaData()))) {
          ownerAndTableName = getSynonymOwnerAndTableName(tableName);
          table.setOwnerSynonymName(ownerAndTableName[0]);
          table.setTableSynonymName(ownerAndTableName[1]);
        }
        if (flg) {
          retriveTableColumns(table);
        }

        BeanHelper.copyProperties(table, TableOverrideValuesProvider.getTableConfigValues(table.getSqlName()));
        return table;
      } catch (SQLException e) {
        throw new RuntimeException("create table object error,tableName:" + tableName, e);
      } finally {
        BuilderLogger.perf("createTable() cost:" + (System.currentTimeMillis() - start) + " tableName:" + tableName);
      }
    }

    private List<Table> getAllTables() throws SQLException {
      DatabaseMetaData dbMetaData = this.connection.getMetaData();
      ResultSet rs = dbMetaData.getTables(getCatalog(), getSchema(), null, new String[] { "TABLE", "VIEW", "SYNONYM" });
      try {
        List tables = new ArrayList();
        while (rs.next()) {
          tables.add(createTable(rs, false));
        }
        return tables;
      } finally {
        DBHelper.close(rs);
      }
    }

    private String[] getSynonymOwnerAndTableName(String synonymName) {
      PreparedStatement ps = null;
      ResultSet rs = null;
      String[] ret = new String[2];
      try {
        ps = this.connection.prepareStatement("select table_owner,table_name from sys.all_synonyms where synonym_name=? and owner=?");
        ps.setString(1, synonymName);
        ps.setString(2, getSchema());
        rs = ps.executeQuery();
        if (rs.next()) {
          ret[0] = rs.getString(1);
          ret[1] = rs.getString(2);
        }
        else {
          String databaseStructure = DatabaseMetaDataUtils.getDatabaseStructureInfo(getMetaData(), this.schema, this.catalog);
          throw new RuntimeException("Wow! Synonym " + synonymName + " not found. How can it happen? " + databaseStructure);
        }
      } catch (SQLException e) {
        String databaseStructure = DatabaseMetaDataUtils.getDatabaseStructureInfo(getMetaData(), this.schema, this.catalog);
        BuilderLogger.error(e.getMessage(), e);
        throw new RuntimeException("Exception in getting synonym owner " + databaseStructure);
      } finally {
        DBHelper.close(null, ps, rs);
      }
      return ret;
    }

    private DatabaseMetaData getMetaData() {
      return DatabaseMetaDataUtils.getMetaData(this.connection);
    }

    private void retriveTableColumns(Table table) throws SQLException {
      BuilderLogger.trace("-------setColumns(" + table.getSqlName() + ")");

      List primaryKeys = getTablePrimaryKeys(table);
      table.setPrimaryKeyColumns(primaryKeys);

      List indices = new LinkedList();

      Map uniqueIndices = new HashMap();

      Map uniqueColumns = new HashMap();
      ResultSet indexRs = null;
      try
      {
        if (table.getOwnerSynonymName() != null) {
          indexRs = getMetaData().getIndexInfo(getCatalog(), table.getOwnerSynonymName(), table.getTableSynonymName(), false, true);
        }
        else {
          indexRs = getMetaData().getIndexInfo(getCatalog(), getSchema(), table.getSqlName(), false, true);
        }
        while (indexRs.next()) {
          String columnName = indexRs.getString("COLUMN_NAME");
          if (columnName != null) {
            BuilderLogger.trace("index:" + columnName);
            indices.add(columnName);
          }

          String indexName = indexRs.getString("INDEX_NAME");
          boolean nonUnique = indexRs.getBoolean("NON_UNIQUE");

          if ((!nonUnique) && (columnName != null) && (indexName != null)) {
            List l = (List)uniqueColumns.get(indexName);
            if (l == null) {
              l = new ArrayList();
              uniqueColumns.put(indexName, l);
            }
            l.add(columnName);
            uniqueIndices.put(columnName, indexName);
            BuilderLogger.trace("unique:" + columnName + " (" + indexName + ")");
          }
        }
      }
      catch (Throwable t) {
      }
      finally {
        DBHelper.close(indexRs);
      }

      List columns = getTableColumns(table, primaryKeys, indices, uniqueIndices, uniqueColumns);

      for (Iterator i = columns.iterator(); i.hasNext(); ) {
        Column column = (Column)i.next();
        table.addColumn(column);
      }

      if (primaryKeys.size() == 0) {
        BuilderLogger.warn("WARNING: The JDBC driver didn't report any primary key columns in " + table.getSqlName());
      }
    }

    private List getTableColumns(Table table, List primaryKeys, List indices, Map uniqueIndices, Map uniqueColumns)
      throws SQLException
    {
      List columns = new LinkedList();
      ResultSet columnRs = getColumnsResultSet(table);
      try {
        while (columnRs.next()) {
          int sqlType = columnRs.getInt("DATA_TYPE");
          String sqlTypeName = columnRs.getString("TYPE_NAME");
          String columnName = columnRs.getString("COLUMN_NAME");
          String columnDefaultValue = columnRs.getString("COLUMN_DEF");

          String remarks = columnRs.getString("REMARKS");
          if ((remarks == null) && (DatabaseMetaDataUtils.isOracleDataBase(this.connection.getMetaData()))) {
            remarks = getOracleColumnComments(table.getSqlName(), columnName);
          }

          boolean isNullable = 1 == columnRs.getInt("NULLABLE");
          int size = columnRs.getInt("COLUMN_SIZE");
          int decimalDigits = columnRs.getInt("DECIMAL_DIGITS");

          boolean isPk = primaryKeys.contains(columnName);
          boolean isIndexed = indices.contains(columnName);
          String uniqueIndex = (String)uniqueIndices.get(columnName);
          List columnsInUniqueIndex = null;
          if (uniqueIndex != null) {
            columnsInUniqueIndex = (List)uniqueColumns.get(uniqueIndex);
          }

          boolean isUnique = (columnsInUniqueIndex != null) && (columnsInUniqueIndex.size() == 1);
          if (isUnique) {
            BuilderLogger.trace("unique column:" + columnName);
          }
          Column column = new Column(table, sqlType, sqlTypeName, columnName, size, decimalDigits, isPk, isNullable, isIndexed, isUnique, columnDefaultValue, remarks);

          BeanHelper.copyProperties(column, TableOverrideValuesProvider.getColumnConfigValues(table, column));
          columns.add(column);
        }
      } finally {
        DBHelper.close(columnRs);
      }
      return columns;
    }

    private ResultSet getColumnsResultSet(Table table) throws SQLException {
      ResultSet columnRs = null;
      if (table.getOwnerSynonymName() != null) {
        columnRs = getMetaData().getColumns(getCatalog(), table.getOwnerSynonymName(), table.getTableSynonymName(), null);
      } else {
        columnRs = getMetaData().getColumns(getCatalog(), getSchema(), table.getSqlName(), null);
      }
      return columnRs;
    }

    private List<String> getTablePrimaryKeys(Table table) throws SQLException
    {
      List primaryKeys = new LinkedList();
      ResultSet primaryKeyRs = null;
      try {
        if (table.getOwnerSynonymName() != null) {
          primaryKeyRs = getMetaData().getPrimaryKeys(getCatalog(), table.getOwnerSynonymName(), table.getTableSynonymName());
        }
        else {
          primaryKeyRs = getMetaData().getPrimaryKeys(getCatalog(), getSchema(), table.getSqlName());
        }
        while (primaryKeyRs.next()) {
          String columnName = primaryKeyRs.getString("COLUMN_NAME");
          BuilderLogger.trace("primary key:" + columnName);
          primaryKeys.add(columnName);
        }
      } finally {
        DBHelper.close(primaryKeyRs);
      }
      return primaryKeys;
    }

    private String getOracleTableComments(String table)
    {
      String sql = "SELECT comments FROM user_tab_comments WHERE table_name='" + table + "'";
      return ExecuteSqlHelper.queryForString(this.connection, sql);
    }

    private String getOracleColumnComments(String table, String column) {
      String sql = "SELECT comments FROM user_col_comments WHERE table_name='" + table + "' AND column_name = '" + column + "'";
      return ExecuteSqlHelper.queryForString(this.connection, sql);
    }
  }

  public static class NotFoundTableException extends RuntimeException
  {
    private static final long serialVersionUID = 5976869128012158628L;

    public NotFoundTableException(String message)
    {
      super();
    }
  }
}


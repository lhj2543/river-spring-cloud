package com.river.site.dataSource.dbtool.provider.db.table;

import com.river.site.dataSource.dbtool.util.StringHelper;
import java.io.Serializable;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class Table
  implements Serializable, Cloneable
{
  String sqlName;
  String remarks;
  String className;
  String tableType;
  private String ownerSynonymName = null;

  private String tableSynonymName = null;

  ColumnSet columns = new ColumnSet();
  List<Column> primaryKeyColumns = new ArrayList();

  String catalog = null;
  String schema = null;
  private String tableAlias;
  private ForeignKeys exportedKeys;
  private ForeignKeys importedKeys;
  public static final String PKTABLE_NAME = "PKTABLE_NAME";
  public static final String PKCOLUMN_NAME = "PKCOLUMN_NAME";
  public static final String FKTABLE_NAME = "FKTABLE_NAME";
  public static final String FKCOLUMN_NAME = "FKCOLUMN_NAME";
  public static final String KEY_SEQ = "KEY_SEQ";

  public Table()
  {
  }

  public Table(Table t)
  {
    setSqlName(t.getSqlName());
    this.remarks = t.getRemarks();
    this.className = t.getClassName();
    this.ownerSynonymName = t.getOwnerSynonymName();
    setColumns(t.getColumns());
    this.primaryKeyColumns = t.getPrimaryKeyColumns();
    this.tableAlias = t.getTableAlias();
    this.exportedKeys = t.exportedKeys;
    this.importedKeys = t.importedKeys;
  }

  public LinkedHashSet<Column> getColumns()
  {
    return this.columns.getColumns();
  }

  public String getTableType()
  {
    return this.tableType;
  }

  public void setTableType(String tableType) {
    this.tableType = tableType;
  }

  public void setColumns(LinkedHashSet<Column> columns) {
    this.columns.setColumns(columns);
  }
  public String getOwnerSynonymName() {
    return this.ownerSynonymName;
  }
  public void setOwnerSynonymName(String ownerSynonymName) {
    this.ownerSynonymName = ownerSynonymName;
  }
  public String getTableSynonymName() {
    return this.tableSynonymName;
  }
  public void setTableSynonymName(String tableSynonymName) {
    this.tableSynonymName = tableSynonymName;
  }

  @Deprecated
  public List<Column> getPrimaryKeyColumns()
  {
    return this.primaryKeyColumns;
  }

  @Deprecated
  public void setPrimaryKeyColumns(List<Column> primaryKeyColumns) {
    this.primaryKeyColumns = primaryKeyColumns;
  }

  public String getSqlName() {
    return this.sqlName;
  }
  public void setSqlName(String sqlName) {
    this.sqlName = sqlName;
  }

  public String getRemarks()
  {
    return this.remarks;
  }
  public void setRemarks(String remarks) {
    this.remarks = remarks;
  }
  public void addColumn(Column column) {
    this.columns.addColumn(column);
  }

  public void setClassName(String customClassName) {
    this.className = customClassName;
  }

  public String getClassName()
  {
    if (StringHelper.isBlank(this.className)) {
      return StringHelper.toJavaClassName(this.sqlName);
    }
    return this.className;
  }

  public String getTableAlias()
  {
    if (StringHelper.isNotBlank(this.tableAlias)) {
      return this.tableAlias;
    }
    return StringHelper.removeCrlf(StringHelper.defaultIfEmpty(getRemarks(), getClassName()));
  }
  public void setTableAlias(String v) {
    this.tableAlias = v;
  }

  public String getClassNameLowerCase()
  {
    return getClassName().toLowerCase();
  }

  public String getUnderscoreName()
  {
    return StringHelper.toUnderscoreName(getClassName()).toLowerCase();
  }

  public String getClassNameFirstLower()
  {
    return StringHelper.uncapitalize(getClassName());
  }

  public String getConstantName()
  {
    return StringHelper.toUnderscoreName(getClassName()).toUpperCase();
  }

  @Deprecated
  public boolean isSingleId()
  {
    return getPkCount() == 1;
  }

  @Deprecated
  public boolean isCompositeId()
  {
    return getPkCount() > 1;
  }

  @Deprecated
  public boolean isNotCompositeId()
  {
    return !isCompositeId();
  }

  public int getPkCount()
  {
    return this.columns.getPkCount();
  }

  /** @deprecated */
  public List getCompositeIdColumns()
  {
    return getPkColumns();
  }

  public List<Column> getPkColumns()
  {
    return this.columns.getPkColumns();
  }

  public List<Column> getNotPkColumns()
  {
    return this.columns.getNotPkColumns();
  }

  public Column getPkColumn()
  {
    Column c = this.columns.getPkColumn();
    if (c == null) {
      throw new IllegalStateException("not found primary key on table:" + getSqlName());
    }
    return c;
  }

  @Deprecated
  public Column getIdColumn()
  {
    return getPkColumn();
  }

  public List<Column> getEnumColumns() {
    return this.columns.getEnumColumns();
  }

  public Column getColumnByName(String name) {
    return this.columns.getByName(name);
  }

  public Column getColumnBySqlName(String sqlName) {
    return this.columns.getBySqlName(sqlName);
  }

  public Column getRequiredColumnBySqlName(String sqlName) {
    if (getColumnBySqlName(sqlName) == null) {
      throw new IllegalArgumentException("not found column with sqlName:" + sqlName + " on table:" + getSqlName());
    }
    return getColumnBySqlName(sqlName);
  }

  public List<Column> getIgnoreKeywordsColumns(String ignoreKeywords)
  {
    List results = new ArrayList();
    for (Column c : getColumns()) {
      String sqlname = c.getSqlName().toLowerCase();
      if (!StringHelper.contains(sqlname, ignoreKeywords.split(",")))
      {
        results.add(c);
      }
    }
    return results;
  }

  public void initImportedKeys(DatabaseMetaData dbmd)
    throws SQLException
  {
    ResultSet fkeys = dbmd.getImportedKeys(this.catalog, this.schema, this.sqlName);

    while (fkeys.next()) {
      String pktable = fkeys.getString("PKTABLE_NAME");
      String pkcol = fkeys.getString("PKCOLUMN_NAME");
      String fktable = fkeys.getString("FKTABLE_NAME");
      String fkcol = fkeys.getString("FKCOLUMN_NAME");
      String seq = fkeys.getString("KEY_SEQ");
      Integer iseq = new Integer(seq);
      getImportedKeys().addForeignKey(pktable, pkcol, fkcol, iseq);
    }
    fkeys.close();
  }

  public void initExportedKeys(DatabaseMetaData dbmd)
    throws SQLException
  {
    ResultSet fkeys = dbmd.getExportedKeys(this.catalog, this.schema, this.sqlName);

    while (fkeys.next()) {
      String pktable = fkeys.getString("PKTABLE_NAME");
      String pkcol = fkeys.getString("PKCOLUMN_NAME");
      String fktable = fkeys.getString("FKTABLE_NAME");
      String fkcol = fkeys.getString("FKCOLUMN_NAME");
      String seq = fkeys.getString("KEY_SEQ");
      Integer iseq = new Integer(seq);
      getExportedKeys().addForeignKey(fktable, fkcol, pkcol, iseq);
    }
    fkeys.close();
  }

  public ForeignKeys getExportedKeys()
  {
    if (this.exportedKeys == null) {
      this.exportedKeys = new ForeignKeys(this);
    }
    return this.exportedKeys;
  }

  public ForeignKeys getImportedKeys()
  {
    if (this.importedKeys == null) {
      this.importedKeys = new ForeignKeys(this);
    }
    return this.importedKeys;
  }

  @Override
  public String toString() {
    return "Database Table:" + getSqlName() + " to ClassName:" + getClassName();
  }

  @Override
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
    }
    return null;
  }

  public void setSchema(String schema)
  {
    this.schema = schema;
  }

  public void setCatalog(String catalog) {
    this.catalog = catalog;
  }
}


package com.river.site.dataSource.dbtool.provider.db.table;

import com.river.site.dataSource.dbtool.util.ListHashtable;
import java.io.Serializable;

public class ForeignKeys
  implements Serializable
{
  protected Table parentTable;
  protected ListHashtable associatedTables;

  public ForeignKeys(Table aTable)
  {
    this.parentTable = aTable;
    this.associatedTables = new ListHashtable();
  }

  public void addForeignKey(String tableName, String columnName, String parentColumn, Integer seq)
  {
    ForeignKey tbl = null;
    if (this.associatedTables.containsKey(tableName)) {
      tbl = (ForeignKey)this.associatedTables.get(tableName);
    }
    else {
      tbl = new ForeignKey(this.parentTable, tableName);
      this.associatedTables.put(tableName, tbl);
    }

    tbl.addColumn(columnName, parentColumn, seq);
  }

  public ListHashtable getAssociatedTables()
  {
    return this.associatedTables;
  }
  public int getSize() {
    return getAssociatedTables().size();
  }
  public boolean getHasImportedKeyColumn(String aColumn) {
    boolean isFound = false;
    int numKeys = getAssociatedTables().size();
    for (int i = 0; i < numKeys; i++) {
      ForeignKey aKey = (ForeignKey)getAssociatedTables().getOrderedValue(i);
      if (aKey.getHasImportedKeyColumn(aColumn)) {
        isFound = true;
        break;
      }
    }
    return isFound;
  }
  public ForeignKey getAssociatedTable(String name) {
    Object fkey = getAssociatedTables().get(name);
    if (fkey != null) {
      return (ForeignKey)fkey;
    }
    return null;
  }

  public Table getParentTable()
  {
    return this.parentTable;
  }
  public boolean getHasImportedKeyParentColumn(String aColumn) {
    boolean isFound = false;
    int numKeys = getAssociatedTables().size();
    for (int i = 0; i < numKeys; i++) {
      ForeignKey aKey = (ForeignKey)getAssociatedTables().getOrderedValue(i);
      if (aKey.getHasImportedKeyParentColumn(aColumn)) {
        isFound = true;
        break;
      }
    }
    return isFound;
  }
  public ForeignKey getImportedKeyParentColumn(String aColumn) {
    ForeignKey aKey = null;
    int numKeys = getAssociatedTables().size();
    for (int i = 0; i < numKeys; i++) {
      aKey = (ForeignKey)getAssociatedTables().getOrderedValue(i);
      if (aKey.getHasImportedKeyParentColumn(aColumn)) {
        break;
      }
    }
    return aKey;
  }
}


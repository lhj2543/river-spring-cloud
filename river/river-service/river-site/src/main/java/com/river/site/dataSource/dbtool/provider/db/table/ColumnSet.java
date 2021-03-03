package com.river.site.dataSource.dbtool.provider.db.table;

import com.river.site.dataSource.dbtool.util.StringHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public class ColumnSet
  implements Serializable
{
  private static final long serialVersionUID = -6500047411657968878L;
  private LinkedHashSet<Column> columns = new LinkedHashSet();

  public ColumnSet()
  {
  }

  public ColumnSet(Collection<? extends Column> columns) {
    this.columns = new LinkedHashSet(columns);
  }

  public LinkedHashSet<Column> getColumns() {
    return this.columns;
  }

  public void setColumns(LinkedHashSet<Column> columns) {
    this.columns = columns;
  }

  public void addColumn(Column c) {
    this.columns.add(c);
  }

  public Column getBySqlName(String name, int sqlType) {
    for (Column c : this.columns) {
      if ((name.equalsIgnoreCase(c.getSqlName())) && (c.getSqlType() == sqlType)) {
        return c;
      }
    }
    return null;
  }

  public Column getBySqlName(String name) {
    if (name == null) {
      return null;
    }

    for (Column c : this.columns) {
      if (name.equalsIgnoreCase(c.getSqlName())) {
        return c;
      }
    }
    return null;
  }

  public Column getByName(String name) {
    if (name == null) {
      return null;
    }

    Column c = getBySqlName(name);
    if (c == null) {
      c = getBySqlName(StringHelper.toUnderscoreName(name));
    }
    return c;
  }

  public Column getByName(String name, int sqlType) {
    Column c = getBySqlName(name, sqlType);
    if (c == null) {
      c = getBySqlName(StringHelper.toUnderscoreName(name), sqlType);
    }
    return c;
  }

  public Column getByColumnName(String name) {
    if (name == null) {
      return null;
    }

    for (Column c : this.columns) {
      if (name.equals(c.getColumnName())) {
        return c;
      }
    }
    return null;
  }

  public List<Column> getPkColumns()
  {
    List results = new ArrayList();
    for (Column c : getColumns()) {
      if (c.isPk()) {
        results.add(c);
      }
    }
    return results;
  }

  public List<Column> getNotPkColumns()
  {
    List results = new ArrayList();
    for (Column c : getColumns()) {
      if (!c.isPk()) {
        results.add(c);
      }
    }
    return results;
  }

  public int getPkCount()
  {
    int pkCount = 0;
    for (Column c : this.columns) {
      if (c.isPk()) {
        pkCount++;
      }
    }
    return pkCount;
  }

  public Column getPkColumn()
  {
    if (getPkColumns().isEmpty()) {
      return null;
    }
    return (Column)getPkColumns().get(0);
  }

  public List<Column> getEnumColumns() {
    List results = new ArrayList();
    for (Column c : getColumns()) {
      if (!c.isEnumColumn()) {
        results.add(c);
      }
    }
    return results;
  }
}


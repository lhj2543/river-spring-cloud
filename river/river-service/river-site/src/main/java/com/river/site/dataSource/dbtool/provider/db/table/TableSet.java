package com.river.site.dataSource.dbtool.provider.db.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TableSet
  implements Serializable
{
  private static final long serialVersionUID = -6500047411657968878L;
  private List<Table> tables = new ArrayList();

  public List<Table> getTables() {
    return this.tables;
  }

  public void setTables(List<Table> tables) {
    this.tables = tables;
  }

  public void addTable(Table c) {
    this.tables.add(c);
  }

  public Table getBySqlName(String name) {
    for (Table c : this.tables) {
      if (name.equalsIgnoreCase(c.getSqlName())) {
        return c;
      }
    }
    return null;
  }

  public Table getByClassName(String name) {
    for (Table c : this.tables) {
      if (name.equals(c.getClassName())) {
        return c;
      }
    }
    return null;
  }
}


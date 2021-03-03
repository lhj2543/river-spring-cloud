package com.river.site.dataSource.dbtool.provider.db.sql;

import com.river.site.dataSource.dbtool.util.StringHelper;
import com.river.site.dataSource.dbtool.util.sqlparse.NamedParameterUtils;
import com.river.site.dataSource.dbtool.util.sqlparse.ParsedSql;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SqlSegment
{
  public String id;
  public String rawIncludeSql;
  public String parsedIncludeSql;
  public Set<SqlParameter> params;

  public SqlSegment()
  {
  }

  public SqlSegment(String id, String rawIncludeSql, String parsedIncludeSql)
  {
    setId(id);
    this.rawIncludeSql = rawIncludeSql;
    this.parsedIncludeSql = parsedIncludeSql;
  }

  public Set<SqlParameter> getParams(Sql sql)
  {
    Set result = new LinkedHashSet();
    for (String paramName : getParamNames()) {
      SqlParameter p = sql.getParam(paramName);
      if (p == null) {
        throw new IllegalArgumentException("not found param on sql:" + this.parsedIncludeSql + " with name:" + paramName + " for sqlSegment:" + this.id);
      }
      result.add(p);
    }
    return result;
  }
  public List<String> getParamNames() {
    ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(this.parsedIncludeSql);
    return parsedSql.getParameterNames();
  }
  public String getClassName() {
    return StringHelper.toJavaClassName(this.id.replace(".", "_").replace("-", "_"));
  }
  public String getId() {
    return this.id;
  }
  public void setId(String id) {
    if (StringHelper.isBlank(id)) {
      throw new IllegalArgumentException("id must be not blank");
    }
    this.id = id;
  }
  public String getRawIncludeSql() {
    return this.rawIncludeSql;
  }
  public void setRawIncludeSql(String rawIncludeSql) {
    this.rawIncludeSql = rawIncludeSql;
  }
  public String getParsedIncludeSql() {
    return this.parsedIncludeSql;
  }
  public void setParsedIncludeSql(String parsedIncludeSql) {
    this.parsedIncludeSql = parsedIncludeSql;
  }
  public Set<SqlParameter> getParams() {
    return this.params;
  }
  public void setParams(Set<SqlParameter> params) {
    this.params = params;
  }

  @Override
  public int hashCode()
  {
    return this.id.hashCode();
  }

  public boolean isGenerateParameterObject() {
    if (getParamNames().size() > 1) {
      return true;
    }
    return false;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    SqlSegment other = (SqlSegment)obj;
    if (this.id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!this.id.equals(other.id)) {
      return false;
    }
    return true;
  }
}


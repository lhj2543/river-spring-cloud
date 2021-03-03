package com.river.site.dataSource.dbtool.provider.db.sql;

import com.river.site.dataSource.dbtool.GeneratorConstants;
import com.river.site.dataSource.dbtool.GeneratorProperties;
import com.river.site.dataSource.dbtool.provider.db.table.Column;
import com.river.site.dataSource.dbtool.provider.db.table.ColumnSet;
import com.river.site.dataSource.dbtool.provider.db.table.Table;
import com.river.site.dataSource.dbtool.util.StringHelper;
import com.river.site.dataSource.dbtool.util.sqlparse.SqlParseHelper;
import com.river.site.dataSource.dbtool.util.sqlparse.SqlParseHelper.NameWithAlias;
import com.river.site.dataSource.dbtool.util.sqlparse.SqlTypeChecker;
import com.river.site.dataSource.dbtool.util.typemapping.JavaPrimitiveTypeMapping;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class Sql
{
  public static String MULTIPLICITY_ONE = "one";
  public static String MULTIPLICITY_MANY = "many";
  public static String MULTIPLICITY_PAGING = "paging";

  public static String PARAMTYPE_PRIMITIVE = "primitive";
  public static String PARAMTYPE_OBJECT = "object";

  String operation = null;
  String resultClass;
  String parameterClass;
  String remarks;
  String multiplicity = MULTIPLICITY_ONE;
  boolean paging = false;
  String sqlmap;
  String resultMap = null;

  LinkedHashSet<Column> columns = new LinkedHashSet();

  LinkedHashSet<SqlParameter> params = new LinkedHashSet();
  String sourceSql;
  String executeSql;
  private String paramType = PARAMTYPE_PRIMITIVE;

  private List<SqlSegment> sqlSegments = new ArrayList();
  private String ibatisSql;
  private String mybatisSql;

  public boolean isColumnsInSameTable()
  {
    if ((this.columns == null) || (this.columns.isEmpty())) {
      return false;
    }

    Collection tableNames = SqlParseHelper.getTableNamesByQuery(this.executeSql);
    if (tableNames.size() > 1) {
      return false;
    }
    Table t = SqlFactory.getTableFromCache(((SqlParseHelper.NameWithAlias)tableNames.iterator().next()).getName());
    for (Column c : this.columns) {
      Column fromTableColumn = new ColumnSet(t.getColumns()).getBySqlName(c.getSqlName());
      if (fromTableColumn == null) {
        return false;
      }

    }

    return true;
  }

  public String getResultClass()
  {
    String resultClass = _getResultClass();
    if ((isPaging()) || (MULTIPLICITY_MANY.equals(this.multiplicity))) {
      return JavaPrimitiveTypeMapping.getWrapperType(resultClass);
    }
    return resultClass;
  }

  private String _getResultClass()
  {
    if (StringHelper.isNotBlank(this.resultClass)) {
      return this.resultClass;
    }
    if (this.columns.size() == 1) {
      return ((Column)this.columns.iterator().next()).getSimpleJavaType();
    }
    if (isColumnsInSameTable()) {
      Collection tableNames = SqlParseHelper.getTableNamesByQuery(this.executeSql);
      Table t = SqlFactory.getTableFromCache(((SqlParseHelper.NameWithAlias)tableNames.iterator().next()).getName());
      return t.getClassName();
    }
    if (this.operation == null) {
      return null;
    }
    return StringHelper.makeAllWordFirstLetterUpperCase(StringHelper.toUnderscoreName(this.operation)) + GeneratorProperties.getProperty(GeneratorConstants.GENERATOR_SQL_RESULTCLASS_SUFFIX);
  }

  public void setResultClass(String queryResultClass)
  {
    this.resultClass = queryResultClass;
  }

  public boolean isHasCustomResultClass() {
    return StringHelper.isNotBlank(this.resultClass);
  }

  public boolean isHasResultMap() {
    return StringHelper.isNotBlank(this.resultMap);
  }

  public String getResultClassName()
  {
    int lastIndexOf = getResultClass().lastIndexOf(".");
    return lastIndexOf >= 0 ? getResultClass().substring(lastIndexOf + 1) : getResultClass();
  }

  public String getParameterClass()
  {
    if (StringHelper.isNotBlank(this.parameterClass)) {
      return this.parameterClass;
    }
    if (StringHelper.isBlank(this.operation)) {
      return null;
    }
    if (isSelectSql()) {
      return StringHelper.makeAllWordFirstLetterUpperCase(StringHelper.toUnderscoreName(this.operation)) + "Query";
    }
    return StringHelper.makeAllWordFirstLetterUpperCase(StringHelper.toUnderscoreName(this.operation)) + "Parameter";
  }

  public void setParameterClass(String parameterClass)
  {
    this.parameterClass = parameterClass;
  }

  public String getParameterClassName()
  {
    int lastIndexOf = getParameterClass().lastIndexOf(".");
    return lastIndexOf >= 0 ? getParameterClass().substring(lastIndexOf + 1) : getParameterClass();
  }

  public int getColumnsCount()
  {
    return this.columns.size();
  }
  public void addColumn(Column c) {
    this.columns.add(c);
  }

  public String getOperation()
  {
    return this.operation;
  }
  public void setOperation(String operation) {
    this.operation = operation;
  }
  public String getOperationFirstUpper() {
    return StringHelper.capitalize(getOperation());
  }

  public String getMultiplicity()
  {
    return this.multiplicity;
  }

  public void setMultiplicity(String multiplicity) {
    this.multiplicity = multiplicity;
  }

  public LinkedHashSet<Column> getColumns()
  {
    return this.columns;
  }
  public void setColumns(LinkedHashSet<Column> columns) {
    this.columns = columns;
  }

  public LinkedHashSet<SqlParameter> getParams()
  {
    return this.params;
  }
  public void setParams(LinkedHashSet<SqlParameter> params) {
    this.params = params;
  }
  public SqlParameter getParam(String paramName) {
    for (SqlParameter p : getParams()) {
      if (p.getParamName().equals(paramName)) {
        return p;
      }
    }
    return null;
  }

  public String getSourceSql()
  {
    return this.sourceSql;
  }
  public void setSourceSql(String sourceSql) {
    this.sourceSql = sourceSql;
  }

  public String getSqlmap() {
    return getSqlmap(getParamNames());
  }

  public void setSqlmap(String sqlmap) {
    if (StringHelper.isNotBlank(sqlmap)) {
      sqlmap = StringHelper.replace(sqlmap, "${cdata-start}", "<![CDATA[");
      sqlmap = StringHelper.replace(sqlmap, "${cdata-end}", "]]>");
    }
    this.sqlmap = sqlmap;
  }

  private List<String> getParamNames() {
    List paramNames = new ArrayList();
    for (SqlParameter p : this.params) {
      paramNames.add(p.getParamName());
    }
    return paramNames;
  }

  private String getSqlmap(List<String> params) {
    if ((params == null) || (params.size() == 0)) {
      return this.sqlmap;
    }

    String result = this.sqlmap;

    if (params.size() == 1)
    {
      return StringHelper.replace(result, "${param1}", "value");
    }
    for (int i = 0; i < params.size(); i++) {
      result = StringHelper.replace(result, "${param" + (i + 1) + "}", (String)params.get(i));
    }

    return result;
  }

  public boolean isHasSqlMap() {
    return StringHelper.isNotBlank(this.sqlmap);
  }

  public String getResultMap() {
    return this.resultMap;
  }

  public void setResultMap(String resultMap) {
    this.resultMap = resultMap;
  }

  public String getExecuteSql()
  {
    return this.executeSql;
  }

  public void setExecuteSql(String executeSql) {
    this.executeSql = executeSql;
  }

  public String getCountHql() {
    return toCountSqlForPaging(getHql());
  }

  public String getCountSql() {
    return toCountSqlForPaging(getSql());
  }

  public String getIbatisCountSql() {
    return toCountSqlForPaging(getIbatisSql());
  }

  public String getMybatisCountSql() {
    return toCountSqlForPaging(getMybatisSql());
  }

  public String getSqlmapCountSql() {
    return toCountSqlForPaging(getSqlmap());
  }

  public String getSql() {
    return replaceWildcardWithColumnsSqlName(this.sourceSql);
  }

  public static String toCountSqlForPaging(String sql) {
    if (sql == null) {
      return null;
    }
    if (SqlTypeChecker.isSelectSql(sql)) {
      return SqlParseHelper.toCountSqlForPaging(sql, "select count(*) ");
    }
    return sql;
  }

  public String getSpringJdbcSql() {
    return SqlParseHelper.convert2NamedParametersSql(getSql(), ":", "");
  }

  public String getHql() {
    return SqlParseHelper.convert2NamedParametersSql(getSql(), ":", "");
  }

  public String getIbatisSql() {
    return StringHelper.isBlank(this.ibatisSql) ? SqlParseHelper.convert2NamedParametersSql(getSql(), "#", "#") : this.ibatisSql;
  }

  public String getMybatisSql() {
    return StringHelper.isBlank(this.mybatisSql) ? SqlParseHelper.convert2NamedParametersSql(getSql(), "#{", "}") : this.mybatisSql;
  }

  public void setIbatisSql(String ibatisSql) {
    this.ibatisSql = ibatisSql;
  }

  public void setMybatisSql(String mybatisSql) {
    this.mybatisSql = mybatisSql;
  }

  private String joinColumnsSqlName()
  {
    StringBuffer sb = new StringBuffer();
    for (Iterator it = this.columns.iterator(); it.hasNext(); ) {
      Column c = (Column)it.next();
      sb.append(c.getSqlName());
      if (it.hasNext()) {
        sb.append(",");
      }
    }
    return sb.toString();
  }

  public String replaceWildcardWithColumnsSqlName(String sql) {
    if ((SqlTypeChecker.isSelectSql(sql)) && (SqlParseHelper.getSelect(SqlParseHelper.removeSqlComments(sql)).indexOf("*") >= 0) && (SqlParseHelper.getSelect(SqlParseHelper.removeSqlComments(sql)).indexOf("count(") < 0)) {
      return SqlParseHelper.getPrettySql("select " + joinColumnsSqlName() + " " + SqlParseHelper.removeSelect(sql));
    }
    return sql;
  }

  public List<SqlSegment> getSqlSegments()
  {
    return this.sqlSegments;
  }

  public void setSqlSegments(List<SqlSegment> includeSqls) {
    this.sqlSegments = includeSqls;
  }

  public SqlSegment getSqlSegment(String id) {
    for (SqlSegment seg : this.sqlSegments) {
      if (seg.getId().equals(id)) {
        return seg;
      }
    }
    return null;
  }

  public List<SqlParameter> getFilterdWithSqlSegmentParams() {
    List result = new ArrayList();
    for (SqlParameter p : getParams()) {
      if (!isSqlSegementContainsParam(p.getParamName()))
      {
        result.add(p);
      }
    }
    return result;
  }

  private boolean isSqlSegementContainsParam(String paramName) {
    for (SqlSegment seg : getSqlSegments())
    {
      if (seg.getParamNames().contains(paramName)) {
        return true;
      }
    }
    return false;
  }

  public boolean isSelectSql()
  {
    return SqlTypeChecker.isSelectSql(this.sourceSql);
  }

  public boolean isUpdateSql()
  {
    return SqlTypeChecker.isUpdateSql(this.sourceSql);
  }

  public boolean isDeleteSql()
  {
    return SqlTypeChecker.isDeleteSql(this.sourceSql);
  }

  public boolean isInsertSql()
  {
    return SqlTypeChecker.isInsertSql(this.sourceSql);
  }

  public String getRemarks()
  {
    return this.remarks;
  }

  public String getParamType() {
    return this.paramType;
  }

  public void setParamType(String paramType) {
    this.paramType = paramType;
  }

  public void setRemarks(String comments) {
    this.remarks = comments;
  }

  public boolean isPaging() {
    if (MULTIPLICITY_PAGING.equalsIgnoreCase(this.multiplicity)) {
      return true;
    }
    return this.paging;
  }

  public void setPaging(boolean paging) {
    this.paging = paging;
  }

  public Column getColumnBySqlName(String sqlName) {
    for (Column c : getColumns()) {
      if (c.getSqlName().equalsIgnoreCase(sqlName)) {
        return c;
      }
    }
    return null;
  }

  public Column getColumnByName(String name) {
    Column c = getColumnBySqlName(name);
    if (c == null) {
      c = getColumnBySqlName(StringHelper.toUnderscoreName(name));
    }
    return c;
  }

  public void afterPropertiesSet() {
    for (SqlSegment seg : this.sqlSegments) {
      seg.setParams(seg.getParams(this));
    }
  }

  @Override
  public String toString()
  {
    return "sourceSql:\n" + this.sourceSql + "\nsql:" + getSql();
  }
}


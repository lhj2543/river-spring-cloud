package com.river.site.dataSource.dbtool.provider.db.sql;

import com.river.site.dataSource.dbtool.provider.db.DataSourceProvider;
import com.river.site.dataSource.dbtool.provider.db.table.Column;
import com.river.site.dataSource.dbtool.provider.db.table.Table;
import com.river.site.dataSource.dbtool.provider.db.table.TableFactory;
import com.river.site.dataSource.dbtool.provider.db.table.TableFactory.NotFoundTableException;
import com.river.site.dataSource.dbtool.util.BeanHelper;
import com.river.site.dataSource.dbtool.util.BuilderLogger;
import com.river.site.dataSource.dbtool.util.DBHelper;
import com.river.site.dataSource.dbtool.util.StringHelper;
import com.river.site.dataSource.dbtool.util.sqlerrorcode.SQLErrorCodeSQLExceptionTranslator;
import com.river.site.dataSource.dbtool.util.sqlerrorcode.SQLErrorCodes;
import com.river.site.dataSource.dbtool.util.sqlparse.BasicSqlFormatter;
import com.river.site.dataSource.dbtool.util.sqlparse.NamedParameterUtils;
import com.river.site.dataSource.dbtool.util.sqlparse.ParsedSql;
import com.river.site.dataSource.dbtool.util.sqlparse.ResultSetMetaDataHolder;
import com.river.site.dataSource.dbtool.util.sqlparse.SqlParseHelper;
import com.river.site.dataSource.dbtool.util.sqlparse.SqlParseHelper.NameWithAlias;
import com.river.site.dataSource.dbtool.util.sqlparse.StatementCreatorUtils;
import com.river.site.dataSource.dbtool.util.typemapping.JdbcType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SqlFactory
{
  public static Map<String, Table> cache = new HashMap();

  public Sql parseSql(String sourceSql)
  {
    if (StringHelper.isBlank(sourceSql)) {
      throw new IllegalArgumentException("sourceSql must be not empty");
    }
    String beforeProcessedSql = beforeParseSql(sourceSql);

    String namedSql = SqlParseHelper.convert2NamedParametersSql(beforeProcessedSql, ":", "");
    ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(namedSql);
    String executeSql = new BasicSqlFormatter().format(NamedParameterUtils.substituteNamedParameters(parsedSql));

    Sql sql = new Sql();
    sql.setSourceSql(sourceSql);
    sql.setExecuteSql(executeSql);
    BuilderLogger.debug("\n*******************************");
    BuilderLogger.debug("sourceSql  :" + sql.getSourceSql());
    BuilderLogger.debug("namedSql  :" + namedSql);
    BuilderLogger.debug("executeSql :" + sql.getExecuteSql());
    BuilderLogger.debug("*********************************");

    Connection conn = null;
    PreparedStatement ps = null;
    try {
      conn = DataSourceProvider.getNewConnection();
      conn.setAutoCommit(false);

      ps = conn.prepareStatement(SqlParseHelper.removeOrders(executeSql));

      SqlParametersParser sqlParametersParser = new SqlParametersParser();
      sqlParametersParser.execute(parsedSql, sql);

      ResultSetMetaData resultSetMetaData = executeSqlForResultSetMetaData(executeSql, ps, sqlParametersParser.allParams);
      sql.setColumns(new SelectColumnsParser().convert2Columns(sql, resultSetMetaData));
      sql.setParams(sqlParametersParser.params);

      return afterProcessedSql(sql);
    } catch (SQLException e) {
      throw new RuntimeException("execute sql occer error,\nexecutedSql:" + SqlParseHelper.removeOrders(executeSql), e);
    } catch (Exception e) {
      throw new RuntimeException("sql parse error,\nexecutedSql:" + SqlParseHelper.removeOrders(executeSql), e);
    } finally {
      try {
        DBHelper.rollback(conn);
      } finally {
        DBHelper.close(conn, ps, null);
      }
    }
  }

  protected Sql afterProcessedSql(Sql sql) {
    return sql;
  }

  protected String beforeParseSql(String sourceSql) {
    return sourceSql;
  }

  private ResultSetMetaData executeSqlForResultSetMetaData(String sql, PreparedStatement ps, List<SqlParameter> params) throws SQLException
  {
    StatementCreatorUtils.setRandomParamsValueForPreparedStatement(sql, ps, params);
    try
    {
      ps.setMaxRows(3);
      ps.setFetchSize(3);
      ps.setQueryTimeout(20);
      ResultSet rs = null;
      if (ps.execute()) {
        rs = ps.getResultSet();
        return rs.getMetaData();
      }
      return null;
    } catch (SQLException e) {
      if (isDataIntegrityViolationException(e)) {
        BuilderLogger.warn("ignore executeSqlForResultSetMetaData() SQLException,errorCode:" + e.getErrorCode() + " sqlState:" + e.getSQLState() + " message:" + e.getMessage() + "\n executedSql:" + sql);
        return null;
      }
      String message = "errorCode:" + e.getErrorCode() + " SQLState:" + e.getSQLState() + " errorCodeTranslatorDataBaaseName:" + getErrorCodeTranslatorDataBaaseName() + " " + e.getMessage();
      throw new SQLException(message, e.getSQLState(), e.getErrorCode());
    }
  }

  private String getErrorCodeTranslatorDataBaaseName() {
    SQLErrorCodeSQLExceptionTranslator transaltor = SQLErrorCodeSQLExceptionTranslator.getSQLErrorCodeSQLExceptionTranslator(DataSourceProvider.getDataSource());
    if (transaltor.getSqlErrorCodes() == null) {
      return "null";
    }
    return Arrays.toString(transaltor.getSqlErrorCodes().getDatabaseProductNames());
  }

  protected boolean isDataIntegrityViolationException(SQLException sqlEx)
  {
    SQLErrorCodeSQLExceptionTranslator transaltor = SQLErrorCodeSQLExceptionTranslator.getSQLErrorCodeSQLExceptionTranslator(DataSourceProvider.getDataSource());
    return transaltor.isDataIntegrityViolation(sqlEx);
  }

  public static Table getTableFromCache(String tableSqlName)
  {
    if (tableSqlName == null) {
      throw new IllegalArgumentException("tableSqlName must be not null");
    }

    Table table = (Table)cache.get(tableSqlName.toLowerCase());
    if (table == null) {
      table = TableFactory.getInstance().getTable(tableSqlName);
      cache.put(tableSqlName.toLowerCase(), table);
    }
    return table;
  }

  public static class SqlParametersParser
  {
    private static Map<String, Column> specialParametersMapping = new HashMap();
    public LinkedHashSet<SqlParameter> params;
    public List<SqlParameter> allParams;

    public SqlParametersParser()
    {
      specialParametersMapping.put("offset", new Column(null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "offset", 0, 0, false, false, false, false, null, null));
      specialParametersMapping.put("limit", new Column(null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "limit", 0, 0, false, false, false, false, null, null));
      specialParametersMapping.put("pageSize", new Column(null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "pageSize", 0, 0, false, false, false, false, null, null));
      specialParametersMapping.put("pageNo", new Column(null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "pageNo", 0, 0, false, false, false, false, null, null));
      specialParametersMapping.put("pageNumber", new Column(null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "pageNumber", 0, 0, false, false, false, false, null, null));
      specialParametersMapping.put("pageNum", new Column(null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "pageNumber", 0, 0, false, false, false, false, null, null));
      specialParametersMapping.put("page", new Column(null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "page", 0, 0, false, false, false, false, null, null));

      specialParametersMapping.put("beginRow", new Column(null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "beginRow", 0, 0, false, false, false, false, null, null));
      specialParametersMapping.put("beginRows", new Column(null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "beginRows", 0, 0, false, false, false, false, null, null));
      specialParametersMapping.put("startRow", new Column(null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "startRow", 0, 0, false, false, false, false, null, null));
      specialParametersMapping.put("startRows", new Column(null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "startRows", 0, 0, false, false, false, false, null, null));
      specialParametersMapping.put("endRow", new Column(null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "endRow", 0, 0, false, false, false, false, null, null));
      specialParametersMapping.put("endRows", new Column(null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "endRows", 0, 0, false, false, false, false, null, null));
      specialParametersMapping.put("lastRow", new Column(null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "lastRow", 0, 0, false, false, false, false, null, null));
      specialParametersMapping.put("lastRows", new Column(null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "lastRows", 0, 0, false, false, false, false, null, null));

      specialParametersMapping.put("orderBy", new Column(null, JdbcType.VARCHAR.TYPE_CODE, "VARCHAR", "orderBy", 0, 0, false, false, false, false, null, null));
      specialParametersMapping.put("orderby", new Column(null, JdbcType.VARCHAR.TYPE_CODE, "VARCHAR", "orderby", 0, 0, false, false, false, false, null, null));
      specialParametersMapping.put("sortColumns", new Column(null, JdbcType.VARCHAR.TYPE_CODE, "VARCHAR", "sortColumns", 0, 0, false, false, false, false, null, null));

      this.params = new LinkedHashSet();
      this.allParams = new ArrayList();
    }
    private void execute(ParsedSql parsedSql, Sql sql) throws Exception {
      long start = System.currentTimeMillis();
      for (int i = 0; i < parsedSql.getParameterNames().size(); i++) {
        String paramName = (String)parsedSql.getParameterNames().get(i);
        Column column = findColumnByParamName(parsedSql, sql, paramName);
        if (column == null) {
          column = (Column)specialParametersMapping.get(paramName);
          if (column == null)
          {
            column = new Column(null, JdbcType.UNDEFINED.TYPE_CODE, "UNDEFINED", paramName, 0, 0, false, false, false, false, null, null);
          }
        }
        SqlParameter param = new SqlParameter(column);

        param.setParamName(paramName);
        if (isMatchListParam(sql.getSourceSql(), paramName)) {
          param.setListParam(true);
        }
        this.params.add(param);
        this.allParams.add(param);
      }
      BuilderLogger.perf("parseForSqlParameters() cost:" + (System.currentTimeMillis() - start));
    }

    public boolean isMatchListParam(String sql, String paramName)
    {
      return (sql
        .matches("(?s).*\\sin\\s*\\([:#\\$&]\\{?" + paramName + "\\}?[$#}]?\\).*")) || 
        (sql
        .matches("(?s).*[#$]" + paramName + "\\[]\\.?\\w*[#$].*")) || 
        (sql
        .matches("(?s).*[#$]\\{" + paramName + "\\[[$\\{\\}\\w]+]\\}*.*"));
    }

    private Column findColumnByParamName(ParsedSql parsedSql, Sql sql, String paramName) throws Exception
    {
      Column column = sql.getColumnByName(paramName);
      if (column == null)
      {
        String leftColumn = SqlParseHelper.getColumnNameByRightCondition(parsedSql.toString(), paramName);
        if (leftColumn != null) {
          column = findColumnByParseSql(parsedSql, leftColumn);
        }
      }
      if (column == null) {
        column = findColumnByParseSql(parsedSql, paramName);
      }
      return column;
    }

    private Column findColumnByParseSql(ParsedSql sql, String paramName) throws Exception {
      if (paramName == null) {
        throw new NullPointerException("'paramName' must be not null");
      }
      try
      {
        Collection<NameWithAlias> tableNames = SqlParseHelper.getTableNamesByQuery(sql.toString());
        for (SqlParseHelper.NameWithAlias tableName : tableNames) {
          Table t = SqlFactory.getTableFromCache(tableName.getName());
          if (t != null) {
            Column column = t.getColumnByName(paramName);
            if (column != null) {
              return column;
            }
          }
        }
      }
      catch (TableFactory.NotFoundTableException e) {
        throw new IllegalArgumentException("get tableNamesByQuery occer error:" + sql.toString(), e);
      }
      return null;
    }
  }

  public static class SelectColumnsParser
  {
    private LinkedHashSet<Column> convert2Columns(Sql sql, ResultSetMetaData metadata)
      throws SQLException, Exception
    {
      if (metadata == null) {
        return new LinkedHashSet();
      }
      LinkedHashSet columns = new LinkedHashSet();
      for (int i = 1; i <= metadata.getColumnCount(); i++) {
        Column c = convert2Column(sql, metadata, i);
        if (c == null) {
          throw new IllegalStateException("column must be not null");
        }
        columns.add(c);
      }
      return columns;
    }

    private Column convert2Column(Sql sql, ResultSetMetaData metadata, int i) throws SQLException, Exception {
      ResultSetMetaDataHolder m = new ResultSetMetaDataHolder(metadata, i);
      if (StringHelper.isNotBlank(m.getTableName()))
      {
        Table table = foundTableByTableNameOrTableAlias(sql, m.getTableName());
        if (table == null) {
          return newColumn(null, m);
        }
        Column column = table.getColumnBySqlName(m.getColumnLabelOrName());
        if ((column == null) || (column.getSqlType() != m.getColumnType()))
        {
          column = newColumn(table, m);
          BuilderLogger.trace("not found column:" + m.getColumnLabelOrName() + " on table:" + table.getSqlName() + " " + BeanHelper.describe(column, new String[0]));
        }
        else {
          BuilderLogger.trace("found column:" + m.getColumnLabelOrName() + " on table:" + table.getSqlName() + " " + BeanHelper.describe(column, new String[0]));
        }
        return column;
      }
      return newColumn(null, m);
    }

    private Column newColumn(Table table, ResultSetMetaDataHolder m)
    {
      Column column = new Column(null, m.getColumnType(), m.getColumnTypeName(), m.getColumnLabelOrName(), m.getColumnDisplaySize(), m.getScale(), false, false, false, false, null, null);
      BuilderLogger.trace("not found on table by table emtpty:" + BeanHelper.describe(column, new String[0]));
      return column;
    }

    private Table foundTableByTableNameOrTableAlias(Sql sql, String tableNameId) throws Exception {
      try {
        return SqlFactory.getTableFromCache(tableNameId);
      } catch (TableFactory.NotFoundTableException e) {
        Set<NameWithAlias> tableNames = SqlParseHelper.getTableNamesByQuery(sql.getExecuteSql());
        for (SqlParseHelper.NameWithAlias tableName : tableNames) {
          if (tableName.getAlias().equalsIgnoreCase(tableNameId)) {
            return SqlFactory.getTableFromCache(tableName.getName());
          }
        }
      }
      return null;
    }
  }
}


package com.river.site.dataSource.dbtool.util.sqlparse;

import com.river.site.dataSource.dbtool.provider.db.table.TableFactory;
import com.river.site.dataSource.dbtool.util.BuilderLogger;
import com.river.site.dataSource.dbtool.util.IOHelper;
import com.river.site.dataSource.dbtool.util.StringHelper;

import java.io.StringReader;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlParseHelper
{
  static Pattern fromRegex = Pattern.compile("(\\sfrom\\s+)([,\\w]+)", 2);

  static Pattern join = Pattern.compile("(join\\s+)(\\w+)(as)?(\\w*)", 2);

  static Pattern update = Pattern.compile("(\\s*update\\s+)(\\w+)", 2);

  static Pattern insert = Pattern.compile("(\\s*insert\\s+into\\s+)(\\w+)", 2);

  public static long startTimes = System.currentTimeMillis();

  public static Set<NameWithAlias> getTableNamesByQuery(String sql)
  {
    sql = removeSqlComments(StringHelper.removeXMLCdataTag(sql)).trim();
    Set result = new LinkedHashSet();
    Matcher m = fromRegex.matcher(sql);
    if (m.find()) {
      String from = getFromClauses(sql);
      if (from.matches("(?ims).*\\sfrom\\s.*")) {
        return getTableNamesByQuery(from);
      }

      if (from.indexOf(44) >= 0)
      {
        String[] array = StringHelper.tokenizeToStringArray(from, ",");
        String[] arrayOfString1 = array;

        for (String s:arrayOfString1) {
          result.add(parseTableSqlAlias(s)); }
      }
      else if (StringHelper.indexOfByRegex(from.toLowerCase(), "\\sjoin\\s") >= 0)
      {
        String removedFrom = StringHelper.removeMany(from.toLowerCase(), new String[] { "inner", "full", "left", "right", "outer" });
        String[] joins = removedFrom.split("join");
        String[] arrayOfString2 = joins;
        for (String s:arrayOfString2) {
          result.add(parseTableSqlAlias(s));
        }
      }
      else
      {
        result.add(parseTableSqlAlias(from));
      }
    }
    if (SqlTypeChecker.isUpdateSql(sql)) {
      m = update.matcher(sql);
      if (m.find()) {
        result.add(new NameWithAlias(m.group(2), null));
      }
    }

    if (SqlTypeChecker.isInsertSql(sql)) {
      m = insert.matcher(sql);
      if (m.find()) {
        result.add(new NameWithAlias(m.group(2), null));
      }
    }
    return result;
  }

  public static NameWithAlias parseTableSqlAlias(String str)
  {
    try {
      str = str.trim();
      String[] array = str.split("\\sas\\s");
      if ((array.length >= 2) && (str.matches("^[\\w_]+\\s+as\\s+[_\\w]+.*"))) {
        return new NameWithAlias(array[0], StringHelper.tokenizeToStringArray(array[1], " \n\t")[0]);
      }
      array = StringHelper.tokenizeToStringArray(str, " \n\t");
      if ((array.length >= 2) && (str.matches("^[\\w_]+\\s+[_\\w]+.*"))) {
        return new NameWithAlias(array[0], array[1]);
      }
      return new NameWithAlias(StringHelper.getByRegex(str.trim(), "^[\\w_]+"), StringHelper.getByRegex(str.trim(), "^[\\w_]+\\s+([\\w_]+)", 1));
    } catch (Exception e) {
      throw new IllegalArgumentException("parseTableSqlAlias error,str:" + str, e);
    }
  }

  public static String getParameterClassName(String sql, String paramName)
  {
    Pattern p = Pattern.compile("(:)(" + paramName + ")(\\|?)([\\w.]+)");
    Matcher m = p.matcher(sql);
    if (m.find()) {
      return m.group(4);
    }
    return null;
  }

  public static String getColumnNameByRightCondition(String sql, String column) {
    String operator = "[=<>!]{1,2}";
    String result = getColumnNameByRightCondition(sql, column, operator);
    if (result == null) {
      result = getColumnNameByRightCondition(sql, column, "\\s+like\\s+");
    }
    if (result == null) {
      result = getColumnNameByRightCondition(sql, column, "\\s+between\\s+");
    }
    if (result == null) {
      result = getColumnNameByRightCondition(sql, column, "\\s+between\\s.+\\sand\\s+");
    }
    if (result == null) {
      result = getColumnNameByRightCondition(sql, column, "\\s+not\\s+in\\s*\\(");
    }
    if (result == null) {
      result = getColumnNameByRightCondition(sql, column, "\\s+in\\s*\\(");
    }

    if (result == null) {
      result = getColumnNameByRightConditionWithFunction(sql, column, operator);
    }
    return result;
  }

  private static String getColumnNameByRightConditionWithFunction(String sql, String column, String operator) {
    Pattern p = Pattern.compile("(\\w+)\\s*" + operator + "\\s*\\w+\\([,\\w]*[:#$&]\\{?" + column + "[\\}#$]?[,\\w]*\\)", 34);
    Matcher m = p.matcher(sql);
    if (m.find()) {
      return m.group(1);
    }
    return null;
  }

  private static String getColumnNameByRightCondition(String sql, String column, String operator)
  {
    Pattern p = Pattern.compile("(\\w+)\\s*" + operator + "\\s*[:#$&]\\{?" + column + "[\\}#$]?", 34);
    Matcher m = p.matcher(sql);
    if (m.find()) {
      return m.group(1);
    }
    return null;
  }

  public static String convert2NamedParametersSql(String sql, String prefix, String suffix) {
    return new NamedSqlConverter(prefix, suffix).convert2NamedParametersSql(sql);
  }

  public static String getPrettySql(String sql)
  {
    try
    {
      if (IOHelper.readLines(new StringReader(sql)).size() > 1) {
        return sql;
      }
      return StringHelper.replace(StringHelper.replace(sql, "from", "\n\tfrom"), "where", "\n\twhere");
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  public static String removeSelect(String sql)
  {
    if (StringHelper.isBlank(sql)) {
      throw new IllegalArgumentException("sql must be not empty");
    }
    int beginPos = StringHelper.indexOfByRegex(sql.toLowerCase(), "\\sfrom\\s");
    if (beginPos == -1) {
      throw new IllegalArgumentException(" sql : " + sql + " must has a keyword 'from'");
    }
    return sql.substring(beginPos);
  }

  public static String toCountSqlForPaging(String sql, String countQueryPrefix) {
    if (StringHelper.isBlank(sql)) {
      throw new IllegalArgumentException("sql must be not empty");
    }
    if (StringHelper.indexOfByRegex(sql.toLowerCase(), "\\sgroup\\s+by\\s") >= 0) {
      return countQueryPrefix + " from (" + sql + " ) forGroupByCountTable";
    }
    int selectBeginOps = StringHelper.indexOfByRegex(sql.toLowerCase(), "select\\s");
    int fromBeingOps = StringHelper.indexOfByRegex(sql.toLowerCase(), "\\sfrom\\s");
    if (fromBeingOps == -1) {
      throw new IllegalArgumentException(" sql : " + sql + " must has a keyword 'from'");
    }
    return sql.substring(0, selectBeginOps) + countQueryPrefix + sql.substring(fromBeingOps);
  }

  public static String getSelect(String sql)
  {
    if (StringHelper.isBlank(sql)) {
      throw new IllegalArgumentException("sql must be not empty");
    }
    int beginPos = StringHelper.indexOfByRegex(sql.toLowerCase(), "\\sfrom\\s");
    if (beginPos == -1) {
      throw new IllegalArgumentException(" sql : " + sql + " must has a keyword 'from'");
    }
    return sql.substring(0, beginPos);
  }

  public static String getFromClauses(String sql)
  {
    String lowerSql = sql.toLowerCase();
    int fromBegin = StringHelper.indexOfByRegex(lowerSql, "\\sfrom\\s");
    if (fromBegin <= 0) {
      throw new IllegalArgumentException("error from begin:" + fromBegin);
    }

    int fromEnd = lowerSql.indexOf("where");
    if (fromEnd == -1) {
      fromEnd = StringHelper.indexOfByRegex(lowerSql, "\\sgroup\\s+by\\s");
    }
    if (fromEnd == -1) {
      fromEnd = StringHelper.indexOfByRegex(lowerSql, "\\shaving\\s");
    }
    if (fromEnd == -1) {
      fromEnd = StringHelper.indexOfByRegex(lowerSql, "\\sorder\\s+by\\s");
    }
    if (fromEnd == -1) {
      fromEnd = StringHelper.indexOfByRegex(lowerSql, "\\sunion\\s");
    }
    if (fromEnd == -1)
    {
      fromEnd = StringHelper.indexOfByRegex(lowerSql, "\\sintersect\\s");
    }
    if (fromEnd == -1)
    {
      fromEnd = StringHelper.indexOfByRegex(lowerSql, "\\sminus\\s");
    }
    if (fromEnd == -1)
    {
      fromEnd = StringHelper.indexOfByRegex(lowerSql, "\\sexcept\\s");
    }
    if (fromEnd == -1) {
      fromEnd = sql.length();
    }
    return sql.substring(fromBegin + " from ".length(), fromEnd);
  }

  public static String removeSqlComments(String sql) {
    if (sql == null) {
      return null;
    }
    return sql.replaceAll("(?s)/\\*.*?\\*/", "").replaceAll("--.*", "");
  }

  public static String removeOrders(String sql)
  {
    return sql.replaceAll("(?is)order\\s+by[\\w|\\W|\\s|\\S]*", "");
  }

  public static String replaceWhere(String sql) {
    return sql.toString().replaceAll("(?i)\\swhere\\s+(and|or)\\s", " WHERE ");
  }

  public static void setRandomParamsValueForPreparedStatement(String sql, PreparedStatement ps)
    throws SQLException
  {
    int count = StringHelper.containsCount(sql, "?");
    if ((TableFactory.DatabaseMetaDataUtils.isOracleDataBase(ps.getConnection().getMetaData())) &&
      (SqlTypeChecker.isSelectSql(sql))) {
      for (int i = 1; i <= count; i++) {
        ps.setObject(i, null);
      }
      return;
    }

    for (int i = 1; i <= count; i++) {
      long random = new Random(System.currentTimeMillis() + startTimes++).nextInt() * 30 + System.currentTimeMillis() + startTimes;
      try {
        ps.setLong(i, random);
      } catch (SQLException e) {
        try {
          ps.setInt(i, (int)random % 2147483647);
        } catch (SQLException e1) {
          try {
            ps.setString(i, "" + random);
          } catch (SQLException e2) {
            try {
              ps.setTimestamp(i, new Timestamp(random));
            } catch (SQLException e3) {
              try {
                ps.setDate(i, new Date(random));
              } catch (SQLException e6) {
                try {
                  ps.setString(i, "" + (int)random);
                } catch (SQLException e4) {
                  try {
                    ps.setString(i, "" + (short)(int)random);
                  } catch (SQLException e82) {
                    try {
                      ps.setString(i, "" + (byte)(int)random);
                    } catch (SQLException e32) {
                      try {
                        ps.setNull(i, 1111);
                      } catch (SQLException error) {
                        warn(sql, i, error);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private static void warn(String sql, int i, SQLException error)
  {
    BuilderLogger.warn("error on set parametet index:" + i + " cause:" + error + " sql:" + sql);
  }

  public static class NamedSqlConverter
  {
    private String prefix;
    private String suffix;

    public NamedSqlConverter(String prefix, String suffix)
    {
      if (prefix == null) {
        throw new NullPointerException("'prefix' must be not null");
      }
      if (suffix == null) {
        throw new NullPointerException("'suffix' must be not null");
      }
      this.prefix = prefix;
      this.suffix = suffix;
    }

    public String convert2NamedParametersSql(String sql) {
      if (sql.trim().toLowerCase().matches("(?is)\\s*insert\\s+into\\s+.*")) {
        return replace2NamedParameters(replaceInsertSql2NamedParameters(sql));
      }
      return replace2NamedParameters(sql);
    }

    private String replace2NamedParameters(String sql)
    {
      String replacedSql = replace2NamedParametersByOperator(sql, "[=<>!]{1,2}");
      replacedSql = replace2NamedParametersByOperator(replacedSql, "\\s+like\\s+");

      return replacedSql;
    }

    private String replaceInsertSql2NamedParameters(String sql) {
      if (sql.matches("(?is)\\s*insert\\s+into\\s+\\w+\\s+values\\s*\\(.*\\).*")) {
        if (sql.indexOf("?") >= 0) {
          throw new IllegalArgumentException("无法解析的insert sql:" + sql + ",values()段没有包含疑问号?");
        }
        return sql;
      }

      String selectKeyPattern = "<selectKey.*";
      String insertPattern = "\\s*insert\\s+into.*\\((.*?)\\).*values.*?\\((.*)\\).*";
      Matcher m = matcher(sql, 34, new String[] { insertPattern + selectKeyPattern, insertPattern });
      if (m != null) {
        String[] columns = StringHelper.tokenizeToStringArray(m.group(1), ", \t\n\r\f");
        String[] values = StringHelper.tokenizeToStringArray(m.group(2), ", \t\n\r\f");
        if (columns.length != values.length) {
          throw new IllegalArgumentException("insert 语句的插入列与值列数目不相等,sql:" + sql + " \ncolumns:" + StringHelper.join(columns, ",") + " \nvalues:" + StringHelper.join(values, ","));
        }
        for (int i = 0; i < columns.length; i++) {
          String column = columns[i];
          String paranName = StringHelper.uncapitalize(StringHelper.makeAllWordFirstLetterUpperCase(column));
          values[i] = values[i].replace("?", this.prefix + paranName + this.suffix);
        }
        return StringHelper.replace(m.start(2), m.end(2), sql, StringHelper.join(values, ","));
      }
      throw new IllegalArgumentException("无法解析的sql:" + sql + ",不匹配正则表达式:" + insertPattern);
    }

    private static Matcher matcher(String input, int flags, String[] regexArray) {
      for (String regex : regexArray) {
        Pattern p = Pattern.compile(regex, flags);
        Matcher m = p.matcher(input);
        if (m.find()) {
          return m;
        }
      }
      return null;
    }

    private String replace2NamedParametersByOperator(String sql, String operator) {
      Pattern p = Pattern.compile("(\\w+)\\s*" + operator + "\\s*\\?", 34);
      Matcher m = p.matcher(sql);
      StringBuffer sb = new StringBuffer();
      while (m.find()) {
        String segment = m.group(0);
        String columnSqlName = m.group(1);
        String paramName = StringHelper.uncapitalize(StringHelper.makeAllWordFirstLetterUpperCase(columnSqlName));
        String replacedSegment = segment.replace("?", this.prefix + paramName + this.suffix);
        m.appendReplacement(sb, replacedSegment);
      }
      m.appendTail(sb);
      return sb.toString();
    }
  }

  public static class NameWithAlias
  {
    private String name;
    private String alias;

    public NameWithAlias(String name, String alias)
    {
      if (name == null) {
        throw new IllegalArgumentException("name must be not null");
      }
      if (name.trim().indexOf(' ') >= 0) {
        throw new IllegalArgumentException("error name:" + name);
      }
      if ((alias != null) && (alias.trim().indexOf(' ') >= 0)) {
        throw new IllegalArgumentException("error alias:" + alias);
      }
      this.name = name.trim();
      this.alias = (alias == null ? null : alias.trim());
    }
    public String getName() {
      return this.name;
    }
    public String getAlias() {
      return StringHelper.isBlank(this.alias) ? getName() : this.alias;
    }
    @Override
    public int hashCode() {
      int prime = 31;
      int result = 1;

      result = 31 * result + (this.name == null ? 0 : this.name
        .hashCode());
      return result;
    }
    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      NameWithAlias other = (NameWithAlias)obj;
      if (this.name == null) {
        if (other.name != null) {
          return false;
        }
      } else if (!this.name.equals(other.name)) {
        return false;
      }
      return true;
    }
    @Override
    public String toString() {
      return this.name + " as " + this.alias;
    }
  }
}


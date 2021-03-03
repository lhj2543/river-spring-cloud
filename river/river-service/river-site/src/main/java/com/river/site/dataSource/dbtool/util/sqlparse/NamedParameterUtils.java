package com.river.site.dataSource.dbtool.util.sqlparse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class NamedParameterUtils
{
  private static final char[] PARAMETER_SEPARATORS = { '"', '\'', ':', '&', ',', ';', '(', ')', '|', '=', '+', '-', '*', '%', '/', '\\', '<', '>', '^' };

  private static final String[] START_SKIP = { "'", "\"", "--", "/*" };

  private static final String[] STOP_SKIP = { "'", "\"", "\n", "*/" };

  public static ParsedSql parseSqlStatement(String sql)
    throws IllegalArgumentException
  {
    if (sql == null) {
      throw new IllegalArgumentException("SQL must not be null");
    }

    Set namedParameters = new HashSet();
    ParsedSql parsedSql = new ParsedSql(sql);

    char[] statement = sql.toCharArray();
    int namedParameterCount = 0;
    int unnamedParameterCount = 0;
    int totalParameterCount = 0;

    int i = 0;
    while (i < statement.length) {
      int skipToPosition = skipCommentsAndQuotes(statement, i);
      if (i != skipToPosition) {
        if (skipToPosition >= statement.length) {
          break;
        }
        i = skipToPosition;
      }
      char c = statement[i];

      if ((c == ':') || (c == '&') || (c == '#') || (c == '$')) {
        int j = i + 1;
        if ((j < statement.length) && (statement[j] == ':') && (c == ':'))
        {
          i += 2;
        }
        else {
          while ((j < statement.length) && (!isParameterSeparator(statement[j]))) {
            j++;
          }
          if (j - i > 1) {
            String parameter = sql.substring(i + 1, j);
            if (!namedParameters.contains(parameter)) {
              namedParameters.add(parameter);
              namedParameterCount++;
            }

            String removedPrefixAndSuffixParameter = removePrefixAndSuffix(c, parameter, sql);

            parsedSql.addNamedParameter(removedPrefixAndSuffixParameter, new StringBuilder().append(c).append(parameter).toString(), i, j);
            totalParameterCount++;
          }
          i = j - 1;
        }
      } else {
        if (c == '?') {
          unnamedParameterCount++;
          totalParameterCount++;
        }

        i++;
      }
    }
    parsedSql.setNamedParameterCount(namedParameterCount);
    parsedSql.setUnnamedParameterCount(unnamedParameterCount);
    parsedSql.setTotalParameterCount(totalParameterCount);
    return parsedSql;
  }

  private static String removePrefixAndSuffix(char startPrifix, String parameter, String sql)
  {
    if ((startPrifix == ':') || (startPrifix == '&')) {
      return parameter;
    }

    if ((parameter.startsWith("{")) || (parameter.endsWith("}"))) {
      if ((parameter.startsWith("{")) && (parameter.endsWith("}"))) {
        parameter = parameter.substring(1, parameter.length() - 1);
      } else {
        throw new IllegalArgumentException(new StringBuilder().append("parameter error:").append(parameter).append(",must wrap with {param},sql:").append(sql).toString());
      }

      return parameter.replaceAll("\\[.*?\\]", "");
    }

    if (startPrifix == '#') {
      if (parameter.endsWith("#")) {
        parameter = parameter.substring(0, parameter.length() - 1);
      } else {
        throw new IllegalArgumentException(new StringBuilder().append("parameter error:").append(parameter).append(",must wrap with #param#,sql:").append(sql).toString());
      }
      if (parameter.endsWith("[]")) {
        return parameter.substring(0, parameter.length() - 2);
      }
      return parameter;
    }

    if (startPrifix == '$') {
      if (parameter.endsWith("$")) {
        parameter = parameter.substring(0, parameter.length() - 1);
      } else {
        throw new IllegalArgumentException(new StringBuilder().append("parameter error:").append(parameter).append(",must wrap with $param$,sql:").append(sql).toString());
      }
      return parameter;
    }

    throw new IllegalArgumentException("cannot reach this");
  }

  private static int skipCommentsAndQuotes(char[] statement, int position)
  {
    for (int i = 0; i < START_SKIP.length; i++) {
      if (statement[position] == START_SKIP[i].charAt(0)) {
        boolean match = true;
        for (int j = 1; j < START_SKIP[i].length(); j++) {
          if (statement[(position + j)] != START_SKIP[i].charAt(j)) {
            match = false;
            break;
          }
        }
        if (match) {
          int offset = START_SKIP[i].length();
          for (int m = position + offset; m < statement.length; m++) {
            if (statement[m] == STOP_SKIP[i].charAt(0)) {
              boolean endMatch = true;
              int endPos = m;
              for (int n = 1; n < STOP_SKIP[i].length(); n++) {
                if (m + n >= statement.length)
                {
                  return statement.length;
                }
                if (statement[(m + n)] != STOP_SKIP[i].charAt(n)) {
                  endMatch = false;
                  break;
                }
                endPos = m + n;
              }
              if (endMatch)
              {
                return endPos + 1;
              }
            }
          }

          return statement.length;
        }
      }
    }

    return position;
  }

  public static String substituteNamedParameters(ParsedSql parsedSql)
  {
    String originalSql = parsedSql.getOriginalSql();
    StringBuilder actualSql = new StringBuilder();
    List paramNames = parsedSql.getParameterNames();
    int lastIndex = 0;
    for (int i = 0; i < paramNames.size(); i++) {
      String paramName = (String)paramNames.get(i);
      int[] indexes = parsedSql.getParameterIndexes(i);
      int startIndex = indexes[0];
      int endIndex = indexes[1];
      actualSql.append(originalSql.substring(lastIndex, startIndex));
      actualSql.append("?");
      lastIndex = endIndex;
    }
    actualSql.append(originalSql.substring(lastIndex, originalSql.length()));
    return actualSql.toString();
  }

  private static boolean isParameterSeparator(char c)
  {
    if (Character.isWhitespace(c)) {
      return true;
    }
    for (char separator : PARAMETER_SEPARATORS) {
      if (c == separator) {
        return true;
      }
    }
    return false;
  }
}


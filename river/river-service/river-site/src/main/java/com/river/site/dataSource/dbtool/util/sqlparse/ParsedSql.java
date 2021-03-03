package com.river.site.dataSource.dbtool.util.sqlparse;

import java.util.ArrayList;
import java.util.List;

public class ParsedSql
{
  private String originalSql;
  private List<String> parameterNames = new ArrayList();

  private List<String> parameterPlaceholders = new ArrayList();

  private List<int[]> parameterIndexes = new ArrayList();
  private int namedParameterCount;
  private int unnamedParameterCount;
  private int totalParameterCount;

  ParsedSql(String originalSql)
  {
    this.originalSql = originalSql;
  }

  String getOriginalSql()
  {
    return this.originalSql;
  }

  public void addNamedParameter(String parameterName, String parameterPlaceholder, int startIndex, int endIndex)
  {
    this.parameterNames.add(parameterName);
    this.parameterPlaceholders.add(parameterPlaceholder);
    this.parameterIndexes.add(new int[] { startIndex, endIndex });
  }

  public List<String> getParameterNames()
  {
    return this.parameterNames;
  }

  public List<String> getParameterPlaceholders()
  {
    return this.parameterPlaceholders;
  }

  public int[] getParameterIndexes(int parameterPosition)
  {
    return (int[])this.parameterIndexes.get(parameterPosition);
  }

  public void setNamedParameterCount(int namedParameterCount)
  {
    this.namedParameterCount = namedParameterCount;
  }

  public int getNamedParameterCount()
  {
    return this.namedParameterCount;
  }

  public void setUnnamedParameterCount(int unnamedParameterCount)
  {
    this.unnamedParameterCount = unnamedParameterCount;
  }

  public int getUnnamedParameterCount()
  {
    return this.unnamedParameterCount;
  }

  public void setTotalParameterCount(int totalParameterCount)
  {
    this.totalParameterCount = totalParameterCount;
  }

  public int getTotalParameterCount()
  {
    return this.totalParameterCount;
  }

  @Override
  public String toString()
  {
    return this.originalSql;
  }
}


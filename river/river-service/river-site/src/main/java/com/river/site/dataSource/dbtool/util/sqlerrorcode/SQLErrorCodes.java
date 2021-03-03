package com.river.site.dataSource.dbtool.util.sqlerrorcode;

public class SQLErrorCodes
{
  private String[] databaseProductNames;
  private boolean useSqlStateForTranslation = false;

  private String[] dataIntegrityViolationCodes = new String[0];

  public void setDatabaseProductNames(String[] databaseProductNames)
  {
    this.databaseProductNames = databaseProductNames;
  }

  public String[] getDatabaseProductNames() {
    return this.databaseProductNames;
  }

  public void setUseSqlStateForTranslation(boolean useStateCodeForTranslation)
  {
    this.useSqlStateForTranslation = useStateCodeForTranslation;
  }

  public boolean isUseSqlStateForTranslation() {
    return this.useSqlStateForTranslation;
  }

  public void setDataIntegrityViolationCodes(String[] dataIntegrityViolationCodes) {
    this.dataIntegrityViolationCodes = dataIntegrityViolationCodes;
  }

  public String[] getDataIntegrityViolationCodes() {
    return this.dataIntegrityViolationCodes;
  }
}


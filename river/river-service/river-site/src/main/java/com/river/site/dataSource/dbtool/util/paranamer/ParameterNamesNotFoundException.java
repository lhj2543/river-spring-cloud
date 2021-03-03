package com.river.site.dataSource.dbtool.util.paranamer;

public class ParameterNamesNotFoundException extends RuntimeException
{
  public static final String __PARANAMER_DATA = "v1.0 \n<init> java.lang.String message \n";
  private Exception cause;

  public ParameterNamesNotFoundException(String message, Exception cause)
  {
    super(message);
    this.cause = cause;
  }

  public ParameterNamesNotFoundException(String message) {
    super(message);
  }

  @Override
  public Throwable getCause()
  {
    return this.cause;
  }
}


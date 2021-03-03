package com.river.site.dataSource.dbtool.util.typemapping;

import java.util.HashMap;

public class DatabaseDataTypesUtils
{
  private static final IntStringMap _preferredJavaTypeForSqlType = new IntStringMap();

  public static boolean isFloatNumber(String javaType) {
    if ((javaType.endsWith("Float")) || (javaType.endsWith("Double")) || (javaType.endsWith("BigDecimal")) || (javaType.endsWith("BigInteger"))) {
      return true;
    }
    if ((javaType.endsWith("float")) || (javaType.endsWith("double")) || (javaType.endsWith("BigDecimal")) || (javaType.endsWith("BigInteger"))) {
      return true;
    }
    return false;
  }

  public static boolean isIntegerNumber(String javaType) {
    if ((javaType.endsWith("Long")) || (javaType.endsWith("Integer")) || (javaType.endsWith("Short")) || (javaType.endsWith("Byte"))) {
      return true;
    }
    if ((javaType.endsWith("long")) || (javaType.endsWith("int")) || (javaType.endsWith("short")) || (javaType.endsWith("byte"))) {
      return true;
    }
    return false;
  }

  public static boolean isDate(String javaType) {
    if ((javaType.endsWith("Date")) || (javaType.endsWith("Timestamp")) || (javaType.endsWith("Time"))) {
      return true;
    }
    return false;
  }

  public static boolean isString(String javaType) {
    if (javaType.endsWith("String")) {
      return true;
    }
    return false;
  }

  public static String getPreferredJavaType(int sqlType, int size, int decimalDigits)
  {
    if (((sqlType == 3) || (sqlType == 2)) && (decimalDigits == 0))
    {
      if (size == 1)
      {
        return "java.lang.Boolean";
      }if (size < 3) {
      return "java.lang.Byte";
    }
      if (size < 5) {
        return "java.lang.Short";
      }
      if (size < 10) {
        return "java.lang.Integer";
      }
      if (size < 19) {
        return "java.lang.Long";
      }
      return "java.math.BigDecimal";
    }

    String result = _preferredJavaTypeForSqlType.getString(sqlType);
    if (result == null) {
      result = "java.lang.Object";
    }
    return result;
  }

  static {
    _preferredJavaTypeForSqlType.put(-6, "java.lang.Byte");
    _preferredJavaTypeForSqlType.put(5, "java.lang.Short");
    _preferredJavaTypeForSqlType.put(4, "java.lang.Integer");
    _preferredJavaTypeForSqlType.put(-5, "java.lang.Long");
    _preferredJavaTypeForSqlType.put(7, "java.lang.Float");
    _preferredJavaTypeForSqlType.put(6, "java.lang.Double");
    _preferredJavaTypeForSqlType.put(8, "java.lang.Double");
    _preferredJavaTypeForSqlType.put(3, "java.math.BigDecimal");
    _preferredJavaTypeForSqlType.put(2, "java.math.BigDecimal");
    _preferredJavaTypeForSqlType.put(-7, "java.lang.Boolean");
    _preferredJavaTypeForSqlType.put(16, "java.lang.Boolean");
    _preferredJavaTypeForSqlType.put(1, "java.lang.String");
    _preferredJavaTypeForSqlType.put(12, "java.lang.String");

    _preferredJavaTypeForSqlType.put(-1, "java.lang.String");
    _preferredJavaTypeForSqlType.put(-2, "byte[]");
    _preferredJavaTypeForSqlType.put(-3, "byte[]");
    _preferredJavaTypeForSqlType.put(-4, "byte[]");
    _preferredJavaTypeForSqlType.put(91, "java.sql.Date");
    _preferredJavaTypeForSqlType.put(92, "java.sql.Time");
    _preferredJavaTypeForSqlType.put(93, "java.sql.Timestamp");
    _preferredJavaTypeForSqlType.put(2005, "java.sql.Clob");
    _preferredJavaTypeForSqlType.put(2004, "java.sql.Blob");
    _preferredJavaTypeForSqlType.put(2003, "java.sql.Array");
    _preferredJavaTypeForSqlType.put(2006, "java.sql.Ref");
    _preferredJavaTypeForSqlType.put(2002, "java.lang.Object");
    _preferredJavaTypeForSqlType.put(2000, "java.lang.Object");
  }

  private static class IntStringMap extends HashMap
  {
    public String getString(int i) {
      return (String)get(new Integer(i));
    }

    public String[] getStrings(int i) {
      return (String[])get(new Integer(i));
    }

    public void put(int i, String s) {
      put(new Integer(i), s);
    }

    public void put(int i, String[] sa) {
      put(new Integer(i), sa);
    }
  }
}


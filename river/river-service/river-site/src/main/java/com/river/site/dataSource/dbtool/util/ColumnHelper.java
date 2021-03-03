package com.river.site.dataSource.dbtool.util;

import com.river.site.dataSource.dbtool.provider.db.table.Column;
import com.river.site.dataSource.dbtool.util.typemapping.DatabaseDataTypesUtils;

public class ColumnHelper
{
  public static String[] removeHibernateValidatorSpecialTags(String str)
  {
    if ((str == null) || (str.trim().length() == 0)) return new String[0];
    return str.trim().replaceAll("@", "").replaceAll("\\(.*?\\)", "").trim().split("\\s+");
  }

  public static String getHibernateValidatorExpression(Column c)
  {
    if ((!c.isPk()) && (!c.isNullable())) {
      if (DatabaseDataTypesUtils.isString(c.getJavaType())) {
        return "@NotBlank " + getNotRequiredHibernateValidatorExpression(c);
      }
      return "@NotNull " + getNotRequiredHibernateValidatorExpression(c);
    }

    return getNotRequiredHibernateValidatorExpression(c);
  }

  public static String getNotRequiredHibernateValidatorExpression(Column c)
  {
    String result = "";
    if (c.getSqlName().indexOf("mail") >= 0) {
      result = result + "@Email ";
    }
    if ((DatabaseDataTypesUtils.isString(c.getJavaType())) && (c.getSize() > 0)) {
      result = result + String.format("@Length(max=%s)", new Object[] { Integer.valueOf(c.getSize()) });
    }
    if (DatabaseDataTypesUtils.isIntegerNumber(c.getJavaType())) {
      String javaType = DatabaseDataTypesUtils.getPreferredJavaType(c.getSqlType(), c.getSize(), c.getDecimalDigits());
      if (javaType.toLowerCase().indexOf("short") >= 0) {
        result = result + " @Max(32767)";
      } else if (javaType.toLowerCase().indexOf("byte") >= 0) {
        result = result + " @Max(127)";
      }
    }
    return result.trim();
  }

  public static String getRapidValidation(Column c)
  {
    String result = "";
    if (c.getSqlName().indexOf("mail") >= 0) {
      result = result + "validate-email ";
    }
    if (DatabaseDataTypesUtils.isFloatNumber(c.getJavaType())) {
      result = result + "validate-number ";
    }
    if (DatabaseDataTypesUtils.isIntegerNumber(c.getJavaType())) {
      result = result + "validate-integer ";
      if (c.getJavaType().toLowerCase().indexOf("short") >= 0) {
        result = result + "max-value-32767";
      } else if (c.getJavaType().toLowerCase().indexOf("integer") >= 0) {
        result = result + "max-value-2147483647";
      } else if (c.getJavaType().toLowerCase().indexOf("byte") >= 0) {
        result = result + "max-value-127";
      }
    }
    return result;
  }
}


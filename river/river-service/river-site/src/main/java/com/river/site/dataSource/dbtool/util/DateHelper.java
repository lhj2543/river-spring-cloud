package com.river.site.dataSource.dbtool.util;

import java.lang.reflect.Constructor;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class DateHelper
{
  public static java.util.Date parseDate(String value, Class targetType, String[] formats)
  {
    for (String format : formats) {
      try {
        long v = new SimpleDateFormat(format).parse(value).getTime();
        return (java.util.Date)targetType.getConstructor(new Class[] { Long.TYPE }).newInstance(new Object[] { Long.valueOf(v) });
      } catch (ParseException e) {
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      try {
        return (java.util.Date)targetType.getConstructor(new Class[] { String.class }).newInstance(new Object[] { value });
      } catch (Exception e) {
      }
    }
    throw new IllegalArgumentException("cannot parse:" + value + " for date by formats:" + Arrays.asList(formats));
  }

  public static boolean isDateType(Class<?> targetType) {
    if (targetType == null) return false;
    return (targetType == java.util.Date.class) || (targetType == Timestamp.class) || (targetType == java.sql.Date.class) || (targetType == Time.class);
  }
}


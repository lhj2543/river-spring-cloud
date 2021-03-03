package com.river.site.dataSource.dbtool.util;

public class ClassHelper
{
  public static Object newInstance(Class<?> c)
  {
    try
    {
      return c.newInstance();
    }
    catch (Exception e) {
      throw new IllegalArgumentException("cannot new instance with class:" + c
        .getName(), e);
    }
  }

  public static Object newInstance(String className) {
    try {
      return newInstance(Class.forName(className));
    } catch (Exception e) {
      throw new IllegalArgumentException("cannot new instance with className:" + className, e);
    }
  }

  public static ClassLoader getDefaultClassLoader()
  {
    ClassLoader cl = null;
    try {
      cl = Thread.currentThread().getContextClassLoader();
    }
    catch (Throwable ex)
    {
    }
    if (cl == null)
    {
      cl = ClassHelper.class.getClassLoader();
    }
    return cl;
  }
}


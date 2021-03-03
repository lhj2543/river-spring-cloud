package com.river.site.dataSource.dbtool.util.paranamer;

import java.lang.reflect.AccessibleObject;

public abstract interface Paranamer
{
  public static final String[] EMPTY_NAMES = new String[0];

  public abstract String[] lookupParameterNames(AccessibleObject paramAccessibleObject);

  public abstract String[] lookupParameterNames(AccessibleObject paramAccessibleObject, boolean paramBoolean);
}


package com.river.site.dataSource.dbtool.util.paranamer;

import java.lang.reflect.AccessibleObject;
import java.util.WeakHashMap;

public class CachingParanamer
  implements Paranamer
{
  public static final String __PARANAMER_DATA = "v1.0 \ncom.thoughtworks.paranamer.CachingParanamer <init> com.thoughtworks.paranamer.Paranamer delegate \ncom.thoughtworks.paranamer.CachingParanamer lookupParameterNames java.lang.AccessibleObject methodOrConstructor \ncom.thoughtworks.paranamer.CachingParanamer lookupParameterNames java.lang.AccessibleObject, boolean methodOrCtor,throwExceptionIfMissing \n";
  private final Paranamer delegate;
  private final WeakHashMap<AccessibleObject, String[]> methodCache = new WeakHashMap();

  public CachingParanamer()
  {
    this(new DefaultParanamer());
  }

  public CachingParanamer(Paranamer delegate)
  {
    this.delegate = delegate;
  }

  @Override
  public String[] lookupParameterNames(AccessibleObject methodOrConstructor) {
    return lookupParameterNames(methodOrConstructor, true);
  }

  @Override
  public String[] lookupParameterNames(AccessibleObject methodOrCtor, boolean throwExceptionIfMissing) {
    if (this.methodCache.containsKey(methodOrCtor)) {
      return (String[])this.methodCache.get(methodOrCtor);
    }

    String[] names = this.delegate.lookupParameterNames(methodOrCtor, throwExceptionIfMissing);
    this.methodCache.put(methodOrCtor, names);

    return names;
  }
}


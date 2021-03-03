package com.river.site.dataSource.dbtool;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class GeneratorContext
{
  static ThreadLocal<Map> context = new ThreadLocal();

  static ThreadLocal<Properties> generatorProperties = new ThreadLocal();

  public static void clear() {
    context.set(null);
    generatorProperties.set(null);
  }

  public static Map getContext() {
    Map map = (Map)context.get();
    if (map == null) {
      setContext(new HashMap());
    }
    return (Map)context.get();
  }

  public static void setContext(Map map) {
    context.set(map);
  }

  public static void put(String key, Object value) {
    getContext().put(key, value);
  }

  public static Properties getGeneratorProperties() {
    return (Properties)generatorProperties.get();
  }

  public static void setGeneratorProperties(Properties props) {
    generatorProperties.set(props);
  }
}


package com.river.site.dataSource.dbtool.util.typemapping;

import com.river.site.dataSource.dbtool.util.StringHelper;
import java.util.HashMap;
import java.util.Map;

public class JavaPrimitiveTypeMapping
{
  static Map<String, String> wraper2primitive = new HashMap();
  static Map<String, String> primitive2wraper = new HashMap();

  public static String getPrimitiveTypeOrNull(String clazz)
  {
    String className = StringHelper.getJavaClassSimpleName(clazz);
    return (String)wraper2primitive.get(className);
  }

  public static String getPrimitiveType(String clazz) {
    String className = StringHelper.getJavaClassSimpleName(clazz);
    String result = (String)wraper2primitive.get(className);
    return result == null ? clazz : result;
  }

  public static String getWrapperTypeOrNull(String clazz) {
    String result = (String)primitive2wraper.get(clazz);
    return result;
  }

  public static String getWrapperType(String clazz) {
    String result = (String)primitive2wraper.get(clazz);
    return result == null ? clazz : result;
  }

  public static String getDefaultValue(String type) {
    if (StringHelper.isBlank(type)) {
      return "null";
    }
    if (type.endsWith("Money"))
    {
      return "0";
    }if (type.lastIndexOf(".") > 0) {
      return "null";
    }
    if (Character.isUpperCase(type.charAt(0))) {
      return "null";
    }
    if ("boolean".equals(type)) {
      return "false";
    }
    if (getWrapperTypeOrNull(type) != null) {
      return "0";
    }
    return "null";
  }

  static
  {
    wraper2primitive.put("Byte", "byte");
    wraper2primitive.put("Short", "short");
    wraper2primitive.put("Integer", "int");
    wraper2primitive.put("Long", "long");
    wraper2primitive.put("Float", "float");
    wraper2primitive.put("Double", "double");
    wraper2primitive.put("Boolean", "boolean");
    wraper2primitive.put("Integer", "int");
    wraper2primitive.put("Character", "char");

    for (String key : wraper2primitive.keySet()) {
      primitive2wraper.put(wraper2primitive.get(key), key);
    }
  }
}


package com.river.site.dataSource.dbtool;

import com.river.site.dataSource.dbtool.util.BuilderLogger;
import com.river.site.dataSource.dbtool.util.PropertiesHelper;
import com.river.site.dataSource.dbtool.util.typemapping.DatabaseTypeUtils;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

public class GeneratorProperties
{
  static final String[] PROPERTIES_FILE_NAMES = { "generator.properties", "generator.xml" };

  static PropertiesHelper propertiesHelper = new PropertiesHelper(new Properties(), true);

  public static void load(String[] files)
    throws InvalidPropertiesFormatException, IOException
  {
    putAll(PropertiesHelper.load(files));
  }

  public static void putAll(Properties props) {
    getHelper().putAll(props);
  }

  public static void clear() {
    getHelper().clear();
  }

  public static void reload() {
    try {
      BuilderLogger.println("Start Load GeneratorPropeties from classpath:" + Arrays.toString(PROPERTIES_FILE_NAMES));
      Properties p = new Properties();
      String[] loadedFiles = PropertiesHelper.loadAllPropertiesFromClassLoader(p, PROPERTIES_FILE_NAMES);
      BuilderLogger.println("GeneratorPropeties Load Success,files:" + Arrays.toString(loadedFiles));

      setSepicalProperties(p, loadedFiles);

      setProperties(p);
    } catch (IOException e) {
      throw new RuntimeException("Load " + PROPERTIES_FILE_NAMES + " error", e);
    }
  }

  private static void setSepicalProperties(Properties p, String[] loadedFiles) {
    if ((loadedFiles != null) && (loadedFiles.length > 0)) {
      String basedir = p.getProperty("basedir");
      if ((basedir != null) && (basedir.startsWith("."))) {
        p.setProperty("basedir", new File(new File(loadedFiles[0]).getParent(), basedir).getAbsolutePath());
      }
    }
  }

  public static String getDatabaseType(String key)
  {
    return getDatabaseType(getHelper().getProperties(), key);
  }

  public static String getDatabaseType(Map p, String key) {
    if (p.containsKey(key)) {
      return (String)p.get(key);
    }
    String jdbcDriver = (String)p.get(GeneratorConstants.JDBC_DRIVER.code);
    return DatabaseTypeUtils.getDatabaseTypeByJdbcDriver(jdbcDriver);
  }

  public static Properties getProperties() {
    return getHelper().getProperties();
  }

  private static PropertiesHelper getHelper() {
    Properties fromContext = GeneratorContext.getGeneratorProperties();
    if (fromContext != null) {
      return new PropertiesHelper(fromContext, true);
    }
    return propertiesHelper;
  }

  public static String getProperty(String key, String defaultValue) {
    return getHelper().getProperty(key, defaultValue);
  }

  public static String getProperty(String key) {
    return getHelper().getProperty(key);
  }

  public static String getProperty(GeneratorConstants key) {
    return getHelper().getProperty(key.code, key.defaultValue);
  }

  public static String getRequiredProperty(String key) {
    return getHelper().getRequiredProperty(key);
  }

  public static String getRequiredProperty(GeneratorConstants key) {
    return getHelper().getRequiredProperty(key.code);
  }

  public static int getRequiredInt(String key) {
    return getHelper().getRequiredInt(key);
  }

  public static boolean getRequiredBoolean(String key) {
    return getHelper().getRequiredBoolean(key);
  }

  public static String getNullIfBlank(String key) {
    return getHelper().getNullIfBlank(key);
  }

  public static String getNullIfBlank(GeneratorConstants key) {
    return getHelper().getNullIfBlank(key.code);
  }

  public static String[] getStringArray(String key) {
    return getHelper().getStringArray(key);
  }

  public static String[] getStringArray(GeneratorConstants key) {
    return getHelper().getStringArray(key.code);
  }

  public static int[] getIntArray(String key) {
    return getHelper().getIntArray(key);
  }

  public static boolean getBoolean(String key, boolean defaultValue) {
    return getHelper().getBoolean(key, defaultValue);
  }

  public static boolean getBoolean(GeneratorConstants key) {
    return getHelper().getBoolean(key.code, Boolean.parseBoolean(key.defaultValue));
  }

  public static void setProperty(GeneratorConstants key, String value) {
    setProperty(key.code, value);
  }

  public static void setProperty(String key, String value) {
    BuilderLogger.debug("[setProperty()] " + key + "=" + value);
    getHelper().setProperty(key, value);
  }

  public static void setProperties(Properties inputProps) {
    propertiesHelper = new PropertiesHelper(inputProps, true);
    for (Iterator it = propertiesHelper.entrySet().iterator(); it.hasNext(); ) {
      Entry entry = (Entry)it.next();

      BuilderLogger.debug("[Property] " + entry.getKey() + "=" + entry.getValue());
    }
    BuilderLogger.println("");
  }

  static
  {
    reload();
  }
}


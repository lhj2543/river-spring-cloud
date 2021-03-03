package com.river.site.dataSource.dbtool.util;

import com.river.site.dataSource.dbtool.GeneratorProperties;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class BuilderLogger
{
  public static final int TRACE = 60;
  public static final int DEBUG = 70;
  public static final int INFO = 80;
  public static final int WARN = 90;
  public static final int ERROR = 100;
  public static int logLevel = 80;
  public static PrintStream out = System.out;
  public static PrintStream err = System.err;

  public static int perfLogLevel = 60;

  public static void trace(String s)
  {
    if (logLevel <= 60)
      out.println("[Generator TRACE] " + s);
  }

  public static void debug(String s) {
    if (logLevel <= 70)
      out.println("[Generator DEBUG] " + s);
  }

  public static void info(String s) {
    if (logLevel <= 80)
      out.println("[Generator INFO] " + s);
  }

  public static void warn(String s) {
    if (logLevel <= 90)
      err.println("[Generator WARN] " + s);
  }

  public static void warn(String s, Throwable e) {
    if (logLevel <= 90) {
      err.println("[Generator WARN] " + s + " cause:" + e);
      e.printStackTrace(err);
    }
  }

  public static void error(String s) {
    if (logLevel <= 100)
      err.println("[Generator ERROR] " + s);
  }

  public static void error(String s, Throwable e) {
    if (logLevel <= 100) {
      err.println("[Generator ERROR] " + s + " cause:" + e);
      e.printStackTrace(err);
    }
  }

  public static void perf(String s)
  {
    if (perfLogLevel <= 80)
      out.println("[Generator Performance] () " + s);
  }

  public static void println(String s)
  {
    if (logLevel <= 80)
      out.println(s);
  }

  public static void init_with_log4j_config()
  {
    Properties props = loadLog4jProperties();
    logLevel = toLogLevel(props.getProperty("cn.org.rapid_framework.generator.util.GLogger", "INFO"));
    perfLogLevel = toLogLevel(props.getProperty("cn.org.rapid_framework.generator.util.GLogger.perf", "ERROR"));
  }

  public static int toLogLevel(String level) {
    if ("TRACE".equalsIgnoreCase(level)) {
      return 60;
    }
    if ("DEBUG".equalsIgnoreCase(level)) {
      return 70;
    }
    if ("INFO".equalsIgnoreCase(level)) {
      return 80;
    }
    if ("WARN".equalsIgnoreCase(level)) {
      return 90;
    }
    if ("ERROR".equalsIgnoreCase(level)) {
      return 100;
    }
    if ("FATAL".equalsIgnoreCase(level)) {
      return 100;
    }
    if ("ALL".equalsIgnoreCase(level)) {
      return 100;
    }
    return 90;
  }

  public static void debug(String string, Map templateModel)
  {
    Iterator localIterator;
    if (logLevel <= 70) {
      println(string);
      if (templateModel == null) {
        return;
      }
      for (localIterator = templateModel.keySet().iterator(); localIterator.hasNext(); ) { Object key = localIterator.next();
        if ((!System.getProperties().containsKey(key)) && (!GeneratorProperties.getProperties().containsKey(key)) && 
          (!key.toString().endsWith("_dir")))
        {
          println(key + " = " + templateModel.get(key));
        }
      }
    }
  }

  private static Properties loadLog4jProperties()
  {
    Properties props = new Properties();
    return props;
  }

  static
  {
    init_with_log4j_config();
  }
}


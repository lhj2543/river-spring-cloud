package com.river.site.dataSource.dbtool.util;


import com.river.site.dataSource.dbtool.resolver.PlaceholderResolver;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class PropertyPlaceholderHelper
{
  private final String placeholderPrefix;
  private final String placeholderSuffix;
  private final String valueSeparator;
  private final boolean ignoreUnresolvablePlaceholders;

  public PropertyPlaceholderHelper(String placeholderPrefix, String placeholderSuffix)
  {
    this(placeholderPrefix, placeholderSuffix, null, true);
  }

  public PropertyPlaceholderHelper(String placeholderPrefix, String placeholderSuffix, String valueSeparator, boolean ignoreUnresolvablePlaceholders)
  {
    if (placeholderPrefix == null) {
      throw new IllegalArgumentException("placeholderPrefix must not be null");
    }
    if (placeholderSuffix == null) {
      throw new IllegalArgumentException("placeholderSuffix must not be null");
    }
    this.placeholderPrefix = placeholderPrefix;
    this.placeholderSuffix = placeholderSuffix;
    this.valueSeparator = valueSeparator;
    this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
  }

  public String replacePlaceholders(String value, final Properties properties)
  {
    if (properties == null) {
      throw new IllegalArgumentException("Argument 'properties' must not be null.");
    }
    return replacePlaceholders(value, new PlaceholderResolver() {
      @Override
      public String resolvePlaceholder(String placeholderName) {
        return properties.getProperty(placeholderName);
      }
    });
  }

  public String replacePlaceholders(String value, PlaceholderResolver placeholderResolver)
  {
    if (value == null) {
      throw new IllegalArgumentException("Argument 'value' must not be null.");
    }
    return parseStringValue(value, placeholderResolver, new HashSet());
  }

  protected String parseStringValue(String strVal, PlaceholderResolver placeholderResolver, Set<String> visitedPlaceholders)
  {
    StringBuilder buf = new StringBuilder(strVal);

    int startIndex = strVal.indexOf(this.placeholderPrefix);
    while (startIndex != -1) {
      int endIndex = findPlaceholderEndIndex(buf, startIndex);
      if (endIndex != -1) {
        String placeholder = buf.substring(startIndex + this.placeholderPrefix.length(), endIndex);
        if (!visitedPlaceholders.add(placeholder)) {
          throw new IllegalArgumentException(new StringBuilder().append("Circular placeholder reference '").append(placeholder).append("' in property definitions").toString());
        }

        placeholder = parseStringValue(placeholder, placeholderResolver, visitedPlaceholders);

        String propVal = placeholderResolver.resolvePlaceholder(placeholder);
        if ((propVal == null) && (this.valueSeparator != null)) {
          int separatorIndex = placeholder.indexOf(this.valueSeparator);
          if (separatorIndex != -1) {
            String actualPlaceholder = placeholder.substring(0, separatorIndex);
            String defaultValue = placeholder.substring(separatorIndex + this.valueSeparator.length());
            propVal = placeholderResolver.resolvePlaceholder(actualPlaceholder);
            if (propVal == null) {
              propVal = defaultValue;
            }
          }
        }
        if (propVal != null)
        {
          propVal = parseStringValue(propVal, placeholderResolver, visitedPlaceholders);
          buf.replace(startIndex, endIndex + this.placeholderSuffix.length(), propVal);

          BuilderLogger.trace(new StringBuilder().append("Resolved placeholder '").append(placeholder).append("'").toString());

          startIndex = buf.indexOf(this.placeholderPrefix, startIndex + propVal.length());
        }
        else if (this.ignoreUnresolvablePlaceholders)
        {
          startIndex = buf.indexOf(this.placeholderPrefix, endIndex + this.placeholderSuffix.length());
        }
        else {
          throw new IllegalArgumentException(new StringBuilder().append("Could not resolve placeholder '").append(placeholder).append("'").toString());
        }

        visitedPlaceholders.remove(placeholder);
      }
      else {
        startIndex = -1;
      }
    }

    return buf.toString();
  }

  private int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
    int index = startIndex + this.placeholderPrefix.length();
    int withinNestedPlaceholder = 0;
    while (index < buf.length()) {
      if (StringHelper.substringMatch(buf, index, this.placeholderSuffix)) {
        if (withinNestedPlaceholder > 0) {
          withinNestedPlaceholder--;
          index = index + this.placeholderPrefix.length() - 1;
        }
        else {
          return index;
        }
      }
      else if (StringHelper.substringMatch(buf, index, this.placeholderPrefix)) {
        withinNestedPlaceholder++;
        index += this.placeholderPrefix.length();
      }
      else {
        index++;
      }
    }
    return -1;
  }
}


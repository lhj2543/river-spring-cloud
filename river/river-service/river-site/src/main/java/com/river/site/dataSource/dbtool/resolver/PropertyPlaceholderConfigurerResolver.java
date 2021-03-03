package com.river.site.dataSource.dbtool.resolver;

import java.util.Properties;

public class PropertyPlaceholderConfigurerResolver
  implements PlaceholderResolver
{
  private final Properties props;

  public PropertyPlaceholderConfigurerResolver(Properties props)
  {
    this.props = props;
  }

  @Override
  public String resolvePlaceholder(String placeholderName) {
    String value = this.props.getProperty(placeholderName);
    if (value == null) {
      value = System.getProperty(placeholderName);
      if ((value == null) && 
        (placeholderName.startsWith("env."))) {
        value = System.getenv(placeholderName.substring("env.".length()));
      }
    }

    return value;
  }
}


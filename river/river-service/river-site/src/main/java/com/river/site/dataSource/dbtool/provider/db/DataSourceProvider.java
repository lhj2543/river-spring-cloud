package com.river.site.dataSource.dbtool.provider.db;

import com.river.site.dataSource.dbtool.GeneratorConstants;
import com.river.site.dataSource.dbtool.GeneratorProperties;
import com.river.site.dataSource.dbtool.util.BuilderLogger;
import com.river.site.dataSource.dbtool.util.StringHelper;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DataSourceProvider
{
  private static Connection connection;
  private static DataSource dataSource;

  public static synchronized Connection getNewConnection()
  {
    try
    {
      return getDataSource().getConnection();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public static synchronized Connection getConnection() {
    try {
      if ((connection == null) || (connection.isClosed())) {
        connection = getDataSource().getConnection();
      }
      return connection;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public static void setDataSource(DataSource dataSource) {
    dataSource = dataSource;
  }

  public static synchronized DataSource getDataSource() {
    if (dataSource == null) {
      dataSource = lookupJndiDataSource(GeneratorProperties.getProperty(GeneratorConstants.DATA_SOURCE_JNDI_NAME));
      if (dataSource == null) {
        dataSource = new DriverManagerDataSource();
      }
    }
    return dataSource;
  }

  private static DataSource lookupJndiDataSource(String name) {
    if (StringHelper.isBlank(name)) {
      return null;
    }
    try
    {
      Context context = new InitialContext();
      return (DataSource)context.lookup(name);
    } catch (NamingException e) {
      BuilderLogger.warn("lookup generator dataSource fail by name:" + name + " cause:" + e.toString() + ",retry by jdbc_url again");
    }return null;
  }

  public static class DriverManagerDataSource implements DataSource
  {
    private static void loadJdbcDriver(String driverClass)
    {
      try {
        if ((driverClass == null) || ("".equals(driverClass.trim()))) {
          throw new IllegalArgumentException("jdbc 'driverClass' must not be empty");
        }
        Class.forName(driverClass.trim());
      } catch (ClassNotFoundException e) {
        throw new RuntimeException("not found jdbc driver class:[" + driverClass + "]", e);
      }
    }

    @Override
    public Connection getConnection()
      throws SQLException
    {
      loadJdbcDriver(getDriverClass());
      Properties props = new Properties();
      props.setProperty("user", getUsername());
      props.setProperty("password", getPassword());

      return DriverManager.getConnection(getUrl(), props);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
      loadJdbcDriver(getDriverClass());
      Properties props = new Properties();
      props.setProperty("user", username);
      props.setProperty("password", password);

      return DriverManager.getConnection(getUrl(), props);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
      throw new UnsupportedOperationException("getLogWriter");
    }

    @Override
    public int getLoginTimeout() throws SQLException {
      throw new UnsupportedOperationException("getLoginTimeout");
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
      throw new UnsupportedOperationException("setLogWriter");
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
      throw new UnsupportedOperationException("setLoginTimeout");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
      if (iface == null) {
        throw new IllegalArgumentException("Interface argument must not be null");
      }
      if (!DataSource.class.equals(iface))
      {
        throw new SQLException("DataSource of type [" + getClass().getName() + "] can only be unwrapped as [javax.sql.DataSource], not as [" + iface
          .getName());
      }
      return (T) this;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
      return DataSource.class.equals(iface);
    }

    private String getUrl() {
      return GeneratorProperties.getRequiredProperty(GeneratorConstants.JDBC_URL);
    }

    private String getUsername() {
      return GeneratorProperties.getRequiredProperty(GeneratorConstants.JDBC_USERNAME);
    }

    private String getPassword() {
      return GeneratorProperties.getProperty(GeneratorConstants.JDBC_PASSWORD);
    }

    private String getDriverClass() {
      return GeneratorProperties.getRequiredProperty(GeneratorConstants.JDBC_DRIVER);
    }

    @Override
    public String toString() {
      return "DataSource: url=" + getUrl() + " username=" + getUsername();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException
    {
      return null;
    }
  }
}


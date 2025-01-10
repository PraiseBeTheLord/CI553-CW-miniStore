package dbAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Apache Derby database access
 *
 * @author Mike Smith University of Brighton
 * @version 2.0
 */

class DerbyAccess extends DBAccess {
  private static final String URL_FORMAT = "jdbc:derby:%s;create=true";
  private static final String DATABASE_NAME = "catshop.db";
  private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

  private Connection connection;

  /**
   * Load the Apache Derby database driver
   */
  @Override
  public void loadDriver() throws Exception {
    Class.forName(DRIVER).newInstance();
  }

  /**
   * Return the url to access the database
   *
   * @return url to database
   */
  @Override
  public String urlOfDatabase() {
    return String.format(URL_FORMAT, DATABASE_NAME);
  }

  /**
   * Return the username to access the database
   *
   * @return username
   */
  @Override
  public String username() {
    return ""; // Default username for Derby embedded
  }

  /**
   * Return the password to access the database
   *
   * @return password
   */
  @Override
  public String password() {
    return ""; // Default password for Derby embedded
  }

  /**
   * Get connection to the database
   *
   * @return Connection object
   */
  @Override
  public Connection getConnection() throws SQLException {
    if (connection == null || connection.isClosed()) {
      try {
        loadDriver();
        connection = DriverManager.getConnection(urlOfDatabase(), username(), password());
      } catch (Exception e) {
        throw new SQLException("Failed to establish connection: " + e.getMessage());
      }
    }
    return connection;
  }
}
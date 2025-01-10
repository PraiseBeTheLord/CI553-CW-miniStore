package dbAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Implements management of an mySQL database on Linux.
 *
 * @author Mike Smith University of Brighton
 * @version 2.0
 */
class LinuxAccess extends DBAccess {
  private static final String URL_FORMAT = "jdbc:mysql://localhost/%s";
  private static final String DATABASE_NAME = "cshop";
  private static final String USERNAME = "root"; // Replace with your MySQL username
  private static final String PASSWORD = ""; // Replace with your MySQL password

  private Connection connection;

  /**
   * Load the MySQL database driver
   */
  @Override
  public void loadDriver() throws Exception {
    Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
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
    return USERNAME;
  }

  /**
   * Return the password to access the database
   *
   * @return password
   */
  @Override
  public String password() {
    return PASSWORD;
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
package dbAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Implements management of a Microsoft Access database.
 *
 * @author Mike Smith University of Brighton
 * @version 2.0
 */
class WindowsAccess extends DBAccess {
  private static final String URL_FORMAT = "jdbc:ucanaccess://%s";
  private static final String DATABASE_NAME = "catshop.mdb"; // Replace with your Access database file
  private static final String DRIVER = "net.ucanaccess.jdbc.UcanaccessDriver";

  private Connection connection;

  @Override
  public void loadDriver() throws Exception {
    Class.forName(DRIVER);
  }

  @Override
  public String urlOfDatabase() {
    return String.format(URL_FORMAT, DATABASE_NAME);
  }

  @Override
  public String username() {
    return ""; // Replace with your Access database username if needed
  }

  @Override
  public String password() {
    return ""; // Replace with your Access database password if needed
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
        connection = DriverManager.getConnection(urlOfDatabase());
      } catch (Exception e) {
        throw new SQLException("Failed to establish connection: " + e.getMessage());
      }
    }
    return connection;
  }
}
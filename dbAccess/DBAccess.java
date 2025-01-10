package dbAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Implements generic management of a database.
 * @author  Mike Smith University of Brighton
 * @version 2.0
 */

/**
 * Base class that defines the access to the database driver
 */
public abstract class DBAccess
{
  protected Connection theConnection = null;

  public abstract void loadDriver() throws Exception;

  public abstract String urlOfDatabase();

  public abstract String username();

  public abstract String password();

  public Connection getConnection() throws SQLException {
    if (theConnection == null || theConnection.isClosed()) {
      try {
        loadDriver();
        theConnection = DriverManager.getConnection(urlOfDatabase(), username(), password());
      } catch (Exception e) {
        throw new SQLException("Failed to establish connection: " + e.getMessage());
      }
    }
    return theConnection;
  }
}
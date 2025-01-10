package dbAccess;

import catalogue.Product;
import debug.DEBUG;
import middle.StockException;
import middle.StockInfo;
import middle.StockReader;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockR implements StockReader {
  private Connection theCon = null;      // Connection to database
  private Statement theStmt = null;      // Statement object

  public StockR() throws StockException {
    try {
      DBAccess dbDriver = (new DBAccessFactory()).getNewDBAccess();
      dbDriver.loadDriver();

      theCon = dbDriver.getConnection();
      theStmt = theCon.createStatement();
      theCon.setAutoCommit(true);
    } catch (SQLException e) {
      throw new StockException("SQL problem:" + e.getMessage());
    } catch (Exception e) {
      throw new StockException("Can not load database driver.");
    }
  }

  protected Statement getStatementObject() {
    return theStmt;
  }

  protected Connection getConnectionObject() {
    return theCon;
  }

  public synchronized boolean exists(String pNum) throws StockException {
    try {
      ResultSet rs = getStatementObject().executeQuery(
              "SELECT price FROM ProductTable " +
                      "WHERE ProductTable.productNo = '" + pNum + "'"
      );
      boolean res = rs.next();
      DEBUG.trace("DB StockR: exists(%s) -> %s", pNum, (res ? "T" : "F"));
      return res;
    } catch (SQLException e) {
      throw new StockException("SQL exists: " + e.getMessage());
    }
  }

  public synchronized Product getDetails(String pNum) throws StockException {
    try {
      Product dt = new Product("0", "", 0.00, 0);
      ResultSet rs = getStatementObject().executeQuery(
              "SELECT description, price, stockLevel " +
                      "FROM ProductTable, StockTable " +
                      "WHERE ProductTable.productNo = '" + pNum + "' " +
                      "AND StockTable.productNo = '" + pNum + "'"
      );
      if (rs.next()) {
        dt.setProductNum(pNum);
        dt.setDescription(rs.getString("description"));
        dt.setPrice(rs.getDouble("price"));
        dt.setQuantity(rs.getInt("stockLevel"));
      }
      rs.close();
      return dt;
    } catch (SQLException e) {
      throw new StockException("SQL getDetails: " + e.getMessage());
    }
  }

  public synchronized ImageIcon getImage(String pNum) throws StockException {
    String filename = "default.jpg";
    try {
      ResultSet rs = getStatementObject().executeQuery(
              "SELECT picture FROM ProductTable " +
                      "WHERE ProductTable.productNo = '" + pNum + "'"
      );

      boolean res = rs.next();
      if (res) {
        filename = rs.getString("picture");
      }
      rs.close();
    } catch (SQLException e) {
      DEBUG.error("getImage()\n%s\n", e.getMessage());
      throw new StockException("SQL getImage: " + e.getMessage());
    }

    return new ImageIcon(filename);
  }

  public synchronized Map<String, List<Integer>> getStockInfo() throws StockException {
    Map<String, List<Integer>> stockInfoMap = new HashMap<>();
    try (PreparedStatement ps = theCon.prepareStatement(
            "SELECT p.productNo, p.description, p.price, s.stockLevel " +
                    "FROM ProductTable p " +
                    "INNER JOIN StockTable s ON p.productNo = s.productNo");
         ResultSet rs = ps.executeQuery()) {

      while (rs.next()) {
        String productNum = rs.getString("productNo");
        String description = rs.getString("description");
        double price = rs.getDouble("price");
        int stockLevel = rs.getInt("stockLevel");

        // Create a StockInfo object
        StockInfo stockInfo = new StockInfo(productNum, description, price, stockLevel);

        // Add the StockInfo object to the map
        stockInfoMap.put(productNum, stockInfo.getDetailsAsList());
      }
    } catch (SQLException e) {
      throw new StockException("SQL getStockInfo: " + e.getMessage());
    }
    return stockInfoMap;
  }
}
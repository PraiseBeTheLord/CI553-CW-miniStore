package remote;

import catalogue.Product;
import dbAccess.StockRW;
import middle.StockException;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

// There can only be 1 ResultSet opened per statement
// so no simultaneous use of the statement object
// hence the synchronized methods

/**
 * Implements Read/Write access to the stock list,
 * the stock list is held in a relational DataBase.
 * @author  Mike Smith University of Brighton
 * @version 2.1
 */

public class R_StockRW extends UnicastRemoteObject implements RemoteStockRW_I {
  private static final long serialVersionUID = 1;
  private final StockRW aStockRW;

  /**
   * All transactions are done via StockRW to ensure
   * that a single connection to the database is used for all transactions
   *
   * @param aStockRW
   * @throws java.rmi.RemoteException if issue
   * @throws middle.StockException if issue
   */
  public R_StockRW(String url, StockRW aStockRW) throws RemoteException, StockException {
    this.aStockRW = aStockRW;
  }

  /**
   * Returns true if product exists
   *
   * @param pNum The product number
   * @return true if product exists else false
   * @throws middle.StockException if underlying error
   */
  public synchronized boolean exists(String pNum) throws RemoteException, StockException {
    return aStockRW.exists(pNum);
  }

  /**
   * Returns details about the product in the stock list
   *
   * @param pNum The product number
   * @return StockNumber, Description, Price, Quantity
   * @throws middle.StockException if underlying error
   */
  public synchronized Product getDetails(String pNum) throws RemoteException, StockException {
    return aStockRW.getDetails(pNum);
  }

  /**
   * Returns an image of the product in the stock list
   *
   * @param pNum The product number
   * @return image
   * @throws middle.StockException if underlying error
   */
  public synchronized ImageIcon getImage(String pNum) throws RemoteException, StockException {
    return aStockRW.getImage(pNum);
  }


  /**
   * Buys stock and hence decrements number in the stock list
   *
   * @param pNum   product number
   * @param amount amount required
   * @return StockNumber, Description, Price, Quantity
   * @throws middle.StockException if underlying error
   */
  public synchronized boolean buyStock(String pNum, int amount) throws RemoteException, StockException {
    return aStockRW.buyStock(pNum, amount);
  }

  /**
   * Adds (Restocks) stock to the product list
   *
   * @param pNum   The product number
   * @param amount Quantity
   * @throws middle.StockException if underlying error
   */
  public synchronized void addStock(String pNum, int amount) throws RemoteException, StockException {
    aStockRW.addStock(pNum, amount);
  }


  /**
   * Modifies Stock details for a given product number.
   * Information modified: Description, Price
   *
   * @param product The product to be modified
   * @throws middle.StockException if underlying error
   */
  public synchronized void modifyStock(Product product) throws RemoteException, StockException {
    aStockRW.modifyStock(product);
  }

  /**
   * Returns details of all products
   *
   * @return Map<String, List < Integer>> of all products with details
   * @throws RemoteException if issue
   * @throws StockException  if issue
   */
  @Override
  public Map<String, List<Integer>> getStockInfo() throws RemoteException, StockException {
    return aStockRW.getStockInfo();
  }
}
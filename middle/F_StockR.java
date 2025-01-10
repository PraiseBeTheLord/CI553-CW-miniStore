package middle;

import catalogue.Product;
import debug.DEBUG;
import remote.RemoteStockR_I;

import javax.swing.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public class F_StockR implements StockReader {
  private RemoteStockR_I aR_StockR = null;
  private String theStockURL = null;

  public F_StockR(String url) {
    DEBUG.trace("F_StockR: %s", url);
    theStockURL = url;
  }

  private void connect() throws StockException {
    try {
      aR_StockR = (RemoteStockR_I) Naming.lookup(theStockURL);
    } catch (Exception e) {
      aR_StockR = null;
      throw new StockException("Com: " + e.getMessage());
    }
  }

  public synchronized boolean exists(String number) throws StockException {
    DEBUG.trace("F_StockR:exists()");
    try {
      if (aR_StockR == null) connect();
      return aR_StockR.exists(number);
    } catch (RemoteException e) {
      aR_StockR = null;
      throw new StockException("Net: " + e.getMessage());
    }
  }

  public synchronized Product getDetails(String number) throws StockException {
    DEBUG.trace("F_StockR:getDetails()");
    try {
      if (aR_StockR == null) connect();
      return aR_StockR.getDetails(number);
    } catch (RemoteException e) {
      aR_StockR = null;
      throw new StockException("Net: " + e.getMessage());
    }
  }


  public synchronized ImageIcon getImage(String number) throws StockException {
    DEBUG.trace("F_StockR:getImage()");
    try {
      if (aR_StockR == null) connect();
      return aR_StockR.getImage(number);
    } catch (RemoteException e) {
      aR_StockR = null;
      throw new StockException("Net: " + e.getMessage());
    }
  }

  public synchronized Map<String, List<Integer>> getStockInfo() throws StockException {
    DEBUG.trace("F_StockR:getStockInfo()");
    try {
      if (aR_StockR == null) connect();
      return aR_StockR.getStockInfo();
    } catch (RemoteException e) {
      aR_StockR = null;
      throw new StockException("Net: " + e.getMessage());
    }
  }
}
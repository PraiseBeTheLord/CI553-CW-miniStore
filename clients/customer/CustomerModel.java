package clients.customer;

import catalogue.Basket;
import catalogue.Product;
import debug.DEBUG;
import middle.MiddleFactory;
import middle.OrderProcessing;
import middle.StockException;
import middle.StockReader;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.Observable;

public class CustomerModel extends Observable {
  private Product theProduct = null;          // Current product
  private Basket theBasket = null;          // Bought items

  private String pn = "";                    // Product being processed

  private StockReader theStock = null;
  private OrderProcessing theOrder = null;
  private ImageIcon thePic = null;

  /*
   * Construct the model of the Customer
   * @param mf The factory to create the connection objects
   */
  public CustomerModel(MiddleFactory mf) {
    try {
      theStock = mf.makeStockReader();        // Database access
    } catch (Exception e) {
      DEBUG.error("CustomerModel.constructor\n" +
              "Database not created?\n%s\n", e.getMessage());
    }
    theBasket = makeBasket();                // Initial Basket
  }

  /**
   * return the Basket of products
   *
   * @return the basket of products
   */
  public Basket getBasket() {
    return theBasket;
  }

  /**
   * Check if the product is in Stock
   *
   * @param productNum The product number
   */
  public void doCheck(String productNum) {
    theBasket.clear();                         // Clear s. list
    String theAction = "";
    pn = productNum.trim();                    // Product no.
    int amount = 1;                         //  & quantity
    try {
      if (theStock.exists(pn)) {                                           // T
        Product pr = theStock.getDetails(pn); //  Product
        if (pr.getQuantity() >= amount) {       //  In stock?
          theAction =                           //   Display
                  String.format("%s : %7.2f (%2d) ", //
                          pr.getDescription(),              //    description
                          pr.getPrice(),                    //    price
                          pr.getQuantity());               //    quantity
          pr.setQuantity(amount);             //   Require 1
          theBasket.add(pr);                  //   Add to basket
          thePic = theStock.getImage(pn);     //   product
        } else {                                //  F
          theAction =                           //   Inform
                  pr.getDescription() +               //   product not
                          " not in stock";                   //   in stock
        }
      } else {                                                // F
        theAction =                           //  Inform Unknown
                "Unknown product number " + pn;       //  product number
      }
    } catch (StockException e) {
      DEBUG.error("CustomerClient.doCheck()\n%s",
              e.getMessage());
    }
    setChanged();
    notifyObservers(theAction);
  }

  /**
   * Clear the products from the basket
   */
  public void doClear() {
    String theAction = "";
    theBasket.clear();                        // Clear s. list
    theAction = "Enter Product Number";       // Set display
    thePic = null;                            // No picture
    setChanged();
    notifyObservers(theAction);
  }

  /**
   * Return a picture of the product
   *
   * @return An instance of an ImageIcon
   */
  public ImageIcon getPicture() {
    return thePic;
  }

  /**
   * ask for update of view callled at start
   */
  private void askForUpdate() {
    setChanged();
    notifyObservers("START only"); // Notify
  }

  /**
   * Make a new Basket
   *
   * @return an instance of a new Basket
   */
  protected Basket makeBasket() {
    return new Basket();
  }

  public Product getProductDetails(String productNum) throws StockException {
    return theStock.getDetails(productNum);
  }
  /**
   * Add a product to the basket.
   * Checks if the specified quantity exceeds the available stock.
   *
   * @param product The product to add.
   */
  public void addToBasket(Product product) {
    try {
      Product stockProduct = theStock.getDetails(product.getProductNum());
      if (stockProduct != null) {
        int availableQuantity = stockProduct.getQuantity();
        int requestedQuantity = 0;

        // Check if the product is already in the basket
        for (Product p : theBasket) {
          if (p.getProductNum().equals(product.getProductNum())) {
            requestedQuantity = p.getQuantity() + 1;
            break;
          }
        }
        if (requestedQuantity == 0) {
          requestedQuantity = 1; // Product is not in the basket, so only 1 is being added
        }

        if (requestedQuantity <= availableQuantity) {
          // Product already in basket, increment quantity
          boolean found = false;
          for (Product p : theBasket) {
            if (p.getProductNum().equals(product.getProductNum())) {
              p.setQuantity(requestedQuantity);
              found = true;
              break;
            }
          }
          if (!found) {
            // Product not in basket, add with quantity 1
            product.setQuantity(1);
            theBasket.add(product);
          }

          setChanged();
          notifyObservers("Product added to basket");
        } else {
          // Not enough stock
          setChanged();
          notifyObservers("Error: " + product.getDescription() + "Not enough stock");
        }
      } else {
        // Product not found
        setChanged();
        notifyObservers("Error: Product not found");
      }
    } catch (StockException e) {
      DEBUG.error("Error adding product to basket\n%s", e.getMessage());
      setChanged();
      notifyObservers("Error: Could not add product to basket");
    }
  }

  public Map<String, List<Integer>> getStockInfo() throws StockException {
    return theStock.getStockInfo();
  }
}
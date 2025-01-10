package clients.cashier;

import catalogue.Basket;
import middle.MiddleFactory;
import middle.OrderProcessing;
import middle.StockReadWriter;
import middle.StockException;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * View of the model
 */
public class CashierView implements Observer {
  private static final int H = 300;       // Height of window pixels
  private static final int W = 400;       // Width  of window pixels

  private static final String CHECK = "Check";
  private static final String BUY = "Buy";
  private static final String BOUGHT = "Bought/Pay";

  // Styling constants
  private static final Color BACKGROUND_COLOR = Color.BLACK;
  private static final Color FOREGROUND_COLOR = Color.WHITE;
  private static final Color BUTTON_COLOR = Color.WHITE;
  private static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 16);
  private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);

  private final JLabel theAction = new JLabel();
  private final JTextField theInput = new JTextField();
  private final JTextArea theOutput = new JTextArea();
  private final JScrollPane theSP = new JScrollPane(theOutput);
  private final JButton theBtCheck = new JButton(CHECK);
  private final JButton theBtBuy = new JButton(BUY);
  private final JButton theBtBought = new JButton(BOUGHT);

  private StockReadWriter theStock;
  private OrderProcessing theOrder;
  private CashierController cont;

  /**
   * Construct the view
   *
   * @param rpc Window in which to construct
   * @param mf  Factor to deliver order and stock objects
   * @param x   x-coordinate of position of window on screen
   * @param y   y-coordinate of position of window on screen
   */
  public CashierView(RootPaneContainer rpc, MiddleFactory mf, int x, int y) {
    try {
      theStock = mf.makeStockReadWriter();             // Database access
      theOrder = mf.makeOrderProcessing();             // Process order
    } catch (Exception e) {
      System.out.println("Exception: " + e.getMessage());
    }

    JFrame frame = (JFrame) rpc;
    frame.setTitle("Cashier Client");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(W, H);
    frame.setLocation(x, y);

    // Set up the layout
    Container cp = frame.getContentPane();
    cp.setLayout(null);
    cp.setBackground(BACKGROUND_COLOR);

    // Title label
    JLabel titleLabel = new JLabel("Cashier");
    titleLabel.setBounds(0, 10, W, 40);
    titleLabel.setFont(TITLE_FONT);
    titleLabel.setForeground(FOREGROUND_COLOR);
    titleLabel.setHorizontalAlignment(JLabel.CENTER);
    cp.add(titleLabel);

    // Button styling
    theBtCheck.setBackground(BUTTON_COLOR);
    theBtBuy.setBackground(BUTTON_COLOR);
    theBtBought.setBackground(BUTTON_COLOR);
    theBtCheck.setForeground(FOREGROUND_COLOR);
    theBtBuy.setForeground(FOREGROUND_COLOR);
    theBtBought.setForeground(FOREGROUND_COLOR);
    theBtCheck.setFont(TEXT_FONT);
    theBtBuy.setFont(TEXT_FONT);
    theBtBought.setFont(TEXT_FONT);
    theBtCheck.setFocusPainted(false);
    theBtBuy.setFocusPainted(false);
    theBtBought.setFocusPainted(false);

    // Button placement
    theBtCheck.setBounds(16, 60, 80, 40);
    theBtBuy.setBounds(16, 110, 80, 40);
    theBtBought.setBounds(16, 160, 150, 40);

    // Add listeners to buttons
    theBtCheck.addActionListener(e -> cont.doCheck(theInput.getText()));
    theBtBuy.addActionListener(e -> cont.doBuy());
    theBtBought.addActionListener(e -> cont.doBought());

    cp.add(theBtCheck);
    cp.add(theBtBuy);
    cp.add(theBtBought);

    // Input and output areas
    theInput.setBounds(110, 60, 270, 40);
    theInput.setFont(TEXT_FONT);
    theInput.setBackground(FOREGROUND_COLOR);
    theInput.setForeground(BACKGROUND_COLOR);
    cp.add(theInput);

    theSP.setBounds(110, 110, 270, 140);
    theOutput.setEditable(false);
    theOutput.setFont(TEXT_FONT);
    theOutput.setText("");
    theOutput.setBackground(BACKGROUND_COLOR);
    theOutput.setForeground(FOREGROUND_COLOR);
    cp.add(theSP);

    // Set focus
    theInput.requestFocus();

    frame.setVisible(true);
  }

  /**
   * The controller object, used so that an interaction can be passed to the controller
   *
   * @param c The controller
   */
  public void setController(CashierController c) {
    cont = c;
  }

  /**
   * Update the view
   *
   * @param modelC The observed model
   * @param arg    Specific args
   */
  @Override
  public void update(Observable modelC, Object arg) {
    CashierModel model = (CashierModel) modelC;
    String message = (String) arg;
    theAction.setText(message);

    Basket basket = model.getBasket();
    if (basket != null) {
      theOutput.setText(basket.getDetails());
    } else {
      theOutput.setText("");
    }

    theInput.requestFocus();
  }
}
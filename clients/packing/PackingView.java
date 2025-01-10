package clients.packing;

import catalogue.Basket;
import middle.MiddleFactory;
import middle.OrderProcessing;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Implements the Packing view.
 */
public class PackingView implements Observer {
  private static final String PACKED = "Picked";

  // Styling constants
  private static final Color BACKGROUND_COLOR = Color.BLACK;
  private static final Color FOREGROUND_COLOR = Color.WHITE;
  private static final Color BUTTON_COLOR = Color.WHITE;
  private static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 16);
  private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);

  private static final int H = 300;       // Height of window pixels
  private static final int W = 400;       // Width  of window pixels

  private final JLabel theAction = new JLabel();
  private final JTextArea theOutput = new JTextArea();
  private final JScrollPane theSP = new JScrollPane(theOutput);
  private final JButton theBtPicked = new JButton(PACKED);

  private OrderProcessing theOrder;
  private PackingController cont;

  /**
   * Construct the view
   *
   * @param rpc Window in which to construct
   * @param mf  Factor to deliver order and stock objects
   * @param x   x-coordinate of position of window on screen
   * @param y   y-coordinate of position of window on screen
   */
  public PackingView(RootPaneContainer rpc, MiddleFactory mf, int x, int y) {
    try {
      theOrder = mf.makeOrderProcessing();        // Process order
    } catch (Exception e) {
      System.out.println("Exception: " + e.getMessage());
    }

    JFrame frame = (JFrame) rpc;
    frame.setTitle("Packing Client");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(W, H);
    frame.setLocation(x, y);

    // Set up the layout
    Container cp = frame.getContentPane();
    cp.setLayout(null);
    cp.setBackground(BACKGROUND_COLOR);

    // Title label
    JLabel titleLabel = new JLabel("Order Packing");
    titleLabel.setBounds(0, 10, W, 40);
    titleLabel.setFont(TITLE_FONT);
    titleLabel.setForeground(FOREGROUND_COLOR);
    titleLabel.setHorizontalAlignment(JLabel.CENTER);
    cp.add(titleLabel);

    // Button styling
    theBtPicked.setBackground(BUTTON_COLOR);
    theBtPicked.setForeground(FOREGROUND_COLOR);
    theBtPicked.setFont(TEXT_FONT);
    theBtPicked.setFocusPainted(false);
    theBtPicked.setBounds(16, 60, 120, 40);

    theBtPicked.addActionListener(e -> cont.doPacked());
    cp.add(theBtPicked);

    // Output and scrolling area
    theSP.setBounds(150, 60, 230, 190);
    theOutput.setEditable(false);
    theOutput.setFont(TEXT_FONT);
    theOutput.setBackground(BACKGROUND_COLOR);
    theOutput.setForeground(FOREGROUND_COLOR);
    cp.add(theSP);

    frame.setVisible(true);
  }

  public void setController(PackingController c) {
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
    PackingModel model = (PackingModel) modelC;
    String message = (String) arg;
    theAction.setText(message);

    Basket basket = model.getBasket();
    if (basket != null) {
      theOutput.setText(basket.getDetails());
    } else {
      theOutput.setText("");
    }
  }
}
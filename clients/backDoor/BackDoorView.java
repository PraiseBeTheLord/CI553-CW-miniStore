package clients.backDoor;

import middle.MiddleFactory;
import middle.StockReadWriter;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Implements the Customer view.
 */

public class BackDoorView implements Observer {
  private static final String RESTOCK = "Add";
  private static final String CLEAR = "Clear";
  private static final String QUERY = "Query";

  // Styling constants
  private static final Color BACKGROUND_COLOR = Color.BLACK;
  private static final Color FOREGROUND_COLOR = Color.WHITE;
  private static final Color BUTTON_COLOR = Color.WHITE;
  private static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 16);
  private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);

  private static final int H = 300;       // Height of window pixels
  private static final int W = 400;       // Width  of window pixels

  private final JLabel theAction = new JLabel();
  private final JTextField theInput = new JTextField();
  private final JTextField theInputNo = new JTextField();
  private final JTextArea theOutput = new JTextArea();
  private final JScrollPane theSP = new JScrollPane();
  private final JButton theBtClear = new JButton(CLEAR);
  private final JButton theBtRStock = new JButton(RESTOCK);
  private final JButton theBtQuery = new JButton(QUERY);

  private StockReadWriter theStock;
  private BackDoorController cont;

  /**
   * Construct the view
   *
   * @param rpc Window in which to construct
   * @param mf  Factor to deliver order and stock objects
   * @param x   x-cordinate of position of window on screen
   * @param y   y-cordinate of position of window on screen
   */
  public BackDoorView(RootPaneContainer rpc, MiddleFactory mf, int x, int y) {
    try {
      theStock = mf.makeStockReadWriter();             // Database access
    } catch (Exception e) {
      System.out.println("Exception: " + e.getMessage());
    }

    JFrame frame = (JFrame) rpc;
    frame.setTitle("BackDoor Client");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(W, H);
    frame.setLocation(x, y);

    // Set up the layout
    Container cp = frame.getContentPane();
    cp.setLayout(null);
    cp.setBackground(BACKGROUND_COLOR);

    // Title label
    JLabel titleLabel = new JLabel("Manage Stock");
    titleLabel.setBounds(0, 10, W, 40);
    titleLabel.setFont(TITLE_FONT);
    titleLabel.setForeground(FOREGROUND_COLOR);
    titleLabel.setHorizontalAlignment(JLabel.CENTER);
    cp.add(titleLabel);

    // Button styling
    theBtQuery.setBackground(BUTTON_COLOR);
    theBtRStock.setBackground(BUTTON_COLOR);
    theBtClear.setBackground(BUTTON_COLOR);
    theBtQuery.setForeground(FOREGROUND_COLOR);
    theBtRStock.setForeground(FOREGROUND_COLOR);
    theBtClear.setForeground(FOREGROUND_COLOR);
    theBtQuery.setFont(TEXT_FONT);
    theBtRStock.setFont(TEXT_FONT);
    theBtClear.setFont(TEXT_FONT);
    theBtQuery.setFocusPainted(false);
    theBtRStock.setFocusPainted(false);
    theBtClear.setFocusPainted(false);

    // Button placement
    theBtQuery.setBounds(16, 60, 80, 40);
    theBtRStock.setBounds(16, 110, 80, 40);
    theBtClear.setBounds(16, 160, 80, 40);

    // Add listeners to buttons
    theBtQuery.addActionListener(e -> cont.doQuery(theInput.getText()));
    theBtRStock.addActionListener(e -> cont.doRStock(theInput.getText(), theInputNo.getText()));
    theBtClear.addActionListener(e -> cont.doClear());

    cp.add(theBtQuery);
    cp.add(theBtRStock);
    cp.add(theBtClear);

    // Input and output areas
    theInput.setBounds(110, 60, 120, 40);
    theInput.setFont(TEXT_FONT);
    theInput.setBackground(FOREGROUND_COLOR);
    theInput.setForeground(BACKGROUND_COLOR);
    cp.add(theInput);

    theInputNo.setBounds(260, 60, 120, 40);
    theInputNo.setFont(TEXT_FONT);
    theInputNo.setBackground(FOREGROUND_COLOR);
    theInputNo.setForeground(BACKGROUND_COLOR);
    cp.add(theInputNo);

    theSP.setBounds(110, 110, 270, 140);
    theOutput.setEditable(false);
    theOutput.setFont(TEXT_FONT);
    theOutput.setText("");
    theOutput.setBackground(BACKGROUND_COLOR);
    theOutput.setForeground(FOREGROUND_COLOR);
    cp.add(theSP);
    theSP.getViewport().add(theOutput);

    // Set focus
    theInput.requestFocus();

    frame.setVisible(true);
  }

  public void setController(BackDoorController c) {
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
    BackDoorModel model = (BackDoorModel) modelC;
    String message = (String) arg;
    theAction.setText(message);

    theOutput.setText(model.getBasket().getDetails());
    theInput.requestFocus();
  }
}
package clients;

import clients.backDoor.BackDoorController;
import clients.backDoor.BackDoorModel;
import clients.backDoor.BackDoorView;
import clients.cashier.CashierController;
import clients.cashier.CashierModel;
import clients.cashier.CashierView;
import clients.customer.CustomerController;
import clients.customer.CustomerModel;
import clients.customer.CustomerView;
import clients.packing.PackingController;
import clients.packing.PackingModel;
import clients.packing.PackingView;
import middle.LocalMiddleFactory;
import middle.MiddleFactory;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Main {
  // Constants for styling
  private static final Color BACKGROUND_COLOR = Color.BLACK;
  private static final Color FOREGROUND_COLOR = Color.WHITE;
  private static final Color BUTTON_BACKGROUND_COLOR = Color.WHITE;
  private static final Color BUTTON_FOREGROUND_COLOR = Color.BLACK;
  private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
  private static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 20);
  private static final Border BUTTON_BORDER = BorderFactory.createLineBorder(Color.WHITE, 2);
  private static final Dimension BUTTON_SIZE = new Dimension(180, 60);
  // New constants for staff window dimensions
  private static final int STAFF_WINDOW_WIDTH = 400;
  private static final int STAFF_WINDOW_HEIGHT = 350;
  private static final Dimension STAFF_BUTTON_SIZE = new Dimension(120, 50);

  public static void main(String[] args) {
    new Main().begin();
  }

  /**
   * Starts the system (Non-distributed)
   */
  public void begin() {
    MiddleFactory mlf = new LocalMiddleFactory(); // Direct access
    showRoleSelectionWindow(mlf);
  }

  /**
   * Displays the role selection window (Customer/Staff)
   *
   * @param mlf A factory to create objects to access the stock list
   */
  private void showRoleSelectionWindow(MiddleFactory mlf) {
    JFrame frame = new JFrame("Role Selection");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(400, 300);
    frame.setLayout(new BorderLayout());
    frame.getContentPane().setBackground(BACKGROUND_COLOR);

    // Title Panel
    JPanel titlePanel = new JPanel();
    JLabel titleLabel = new JLabel("Welcome to the Store");
    titleLabel.setFont(TITLE_FONT);
    titleLabel.setForeground(FOREGROUND_COLOR);
    titlePanel.add(titleLabel);
    titlePanel.setBackground(BACKGROUND_COLOR);
    frame.add(titlePanel, BorderLayout.NORTH);

    // Button Panel
    JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 20, 20));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
    buttonPanel.setBackground(BACKGROUND_COLOR);

    JButton customerButton = new JButton("Customer");
    JButton staffButton = new JButton("Staff");

    // Styling the buttons
    customerButton.setFont(BUTTON_FONT);
    customerButton.setPreferredSize(BUTTON_SIZE);
    customerButton.setBackground(BUTTON_BACKGROUND_COLOR);
    customerButton.setForeground(BUTTON_FOREGROUND_COLOR);
    customerButton.setFocusPainted(false);
    customerButton.setBorder(BUTTON_BORDER);
    customerButton.addActionListener(e -> {
      frame.dispose();
      showLoginWindow("customer", mlf);
    });

    staffButton.setFont(BUTTON_FONT);
    staffButton.setPreferredSize(BUTTON_SIZE);
    staffButton.setBackground(BUTTON_BACKGROUND_COLOR);
    staffButton.setForeground(BUTTON_FOREGROUND_COLOR);
    staffButton.setFocusPainted(false);
    staffButton.setBorder(BUTTON_BORDER);
    staffButton.addActionListener(e -> {
      frame.dispose();
      showLoginWindow("staff", mlf);
    });

    buttonPanel.add(customerButton);
    buttonPanel.add(staffButton);

    frame.add(buttonPanel, BorderLayout.CENTER);

    frame.setLocationRelativeTo(null); // Center on screen
    frame.setVisible(true);
  }

  private void showLoginWindow(String userType, MiddleFactory mlf) {
    new LoginView(userType, new LoginView.LoginListener() {
      @Override
      public void onLoginSuccess(String userType) {
        if ("customer".equals(userType)) {
          startCustomerGUI(mlf);
        } else if ("staff".equals(userType)) {
          showStaffOptions(mlf);
        }
      }

      @Override
      public void onLoginFailure(String message) {
        JOptionPane.showMessageDialog(null, message, "Login Failed", JOptionPane.ERROR_MESSAGE);
      }
    });
  }

  /**
   * Start the Customer GUI
   *
   * @param mlf A factory to create objects to access the stock list
   */
  private void startCustomerGUI(MiddleFactory mlf) {
    JFrame window = new JFrame();
    window.setTitle("Customer View");
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    CustomerModel model = new CustomerModel(mlf);
    CustomerView view = new CustomerView(window, mlf, 0, 0); // x, y are set in CustomerView
    CustomerController cont = new CustomerController(model, view);
    view.setController(cont);

    model.addObserver(view);
    window.setVisible(true);
  }

  /**
   * Displays options for staff roles.
   *
   * @param mlf A factory to create objects to access the stock list
   */
  public void showStaffOptions(MiddleFactory mlf) {
    JFrame staffWindow = new JFrame("Staff Options");
    staffWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    staffWindow.setSize(300, 250);
    staffWindow.setLayout(new GridLayout(3, 1, 15, 15));
    staffWindow.getContentPane().setBackground(BACKGROUND_COLOR);
    staffWindow.getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JButton cashierButton = new JButton("Cashier");
    JButton packingButton = new JButton("Packing");
    JButton backDoorButton = new JButton("Back Door");

    // Styling the buttons
    cashierButton.setFont(BUTTON_FONT);
    cashierButton.setPreferredSize(STAFF_BUTTON_SIZE);
    cashierButton.setBackground(BUTTON_BACKGROUND_COLOR);
    cashierButton.setForeground(BUTTON_FOREGROUND_COLOR);
    cashierButton.setFocusPainted(false);
    cashierButton.setBorder(BUTTON_BORDER);
    cashierButton.addActionListener(e -> {
      staffWindow.dispose();
      startCashierGUI_MVC(mlf);
    });

    packingButton.setFont(BUTTON_FONT);
    packingButton.setPreferredSize(STAFF_BUTTON_SIZE);
    packingButton.setBackground(BUTTON_BACKGROUND_COLOR);
    packingButton.setForeground(BUTTON_FOREGROUND_COLOR);
    packingButton.setFocusPainted(false);
    packingButton.setBorder(BUTTON_BORDER);
    packingButton.addActionListener(e -> {
      staffWindow.dispose();
      startPackingGUI_MVC(mlf);
    });

    backDoorButton.setFont(BUTTON_FONT);
    backDoorButton.setPreferredSize(STAFF_BUTTON_SIZE);
    backDoorButton.setBackground(BUTTON_BACKGROUND_COLOR);
    backDoorButton.setForeground(BUTTON_FOREGROUND_COLOR);
    backDoorButton.setFocusPainted(false);
    backDoorButton.setBorder(BUTTON_BORDER);
    backDoorButton.addActionListener(e -> {
      staffWindow.dispose();
      startBackDoorGUI_MVC(mlf);
    });

    staffWindow.add(cashierButton);
    staffWindow.add(packingButton);
    staffWindow.add(backDoorButton);

    staffWindow.setLocationRelativeTo(null); // Center on screen
    staffWindow.setVisible(true);
  }

  /**
   * Start the cashier client - customer checks stock, buys product.
   *
   * @param mlf A factory to create objects to access the stock list
   */
  public void startCashierGUI_MVC(MiddleFactory mlf) {
    JFrame window = new JFrame();
    window.setTitle("Cashier Client MVC");
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Dimension pos = PosOnScrn.getPos();

    CashierModel model = new CashierModel(mlf);
    CashierView view = new CashierView(window, mlf, pos.width, pos.height);
    CashierController cont = new CashierController(model, view);
    view.setController(cont);

    model.addObserver(view); // Add observer to the model
    window.setVisible(true); // Make window visible
    model.askForUpdate(); // Initial display
  }

  /**
   * Start the Packing client - for warehouse staff to pack the bought order for customers, one order at a time.
   *
   * @param mlf A factory to create objects to access the stock list
   */
  public void startPackingGUI_MVC(MiddleFactory mlf) {
    JFrame window = new JFrame();
    window.setTitle("Packing Client MVC");
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Dimension pos = PosOnScrn.getPos();

    PackingModel model = new PackingModel(mlf);
    PackingView view = new PackingView(window, mlf, pos.width, pos.height);
    PackingController cont = new PackingController(model, view);
    view.setController(cont);

    model.addObserver(view); // Add observer to the model
    window.setVisible(true); // Make window visible
  }

  /**
   * Start the BackDoor client - store staff to check and update stock.
   *
   * @param mlf A factory to create objects to access the stock list
   */
  public void startBackDoorGUI_MVC(MiddleFactory mlf) {
    JFrame window = new JFrame();
    window.setTitle("BackDoor Client MVC");
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Dimension pos = PosOnScrn.getPos();

    BackDoorModel model = new BackDoorModel(mlf);
    BackDoorView view = new BackDoorView(window, mlf, pos.width, pos.height);
    BackDoorController cont = new BackDoorController(model, view);
    view.setController(cont);

    model.addObserver(view); // Add observer to the model
    window.setVisible(true); // Make window visible
  }

  private static void setUIFont(Font f) {
    // Set the font for all components
    java.util.Enumeration keys = UIManager.getDefaults().keys();
    while (keys.hasMoreElements()) {
      Object key = keys.nextElement();
      Object value = UIManager.get(key);
      if (value instanceof javax.swing.plaf.FontUIResource) {
        UIManager.put(key, f);
      }
    }
  }
}
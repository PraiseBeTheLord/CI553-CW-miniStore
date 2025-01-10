package clients.customer;

import catalogue.Basket;
import catalogue.Product;
import middle.MiddleFactory;
import middle.StockException;
import middle.StockReader;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class CustomerView implements Observer {
  private static final int H = 600;       // Height of window pixels
  private static final int W = 800;       // Width  of window pixels
  private static final Color BACKGROUND_COLOR = Color.BLACK;
  private static final Color FOREGROUND_COLOR = Color.WHITE;
  private static final Color BUTTON_BACKGROUND_COLOR = Color.WHITE;
  private static final Color BUTTON_FOREGROUND_COLOR = Color.BLACK;
  private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
  private static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 16);
  private static final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);

  private final JLabel pageTitle = new JLabel("Welcome to the Store");
  private final JLabel searchLabel = new JLabel("Search:");
  private final JTextField searchInput = new JTextField();
  private final JLabel basketLabel = new JLabel("Shopping Basket");
  private JTable basketTable;
  private DefaultTableModel basketTableModel;

  private DefaultTableModel tableModel;
  private JTable productTable;
  private JScrollPane tableScrollPane;

  private StockReader theStock;
  private CustomerController cont;
  private MiddleFactory mlf;

  public CustomerView(JFrame frame, MiddleFactory mf, int x, int y) {
    try {
      theStock = mf.makeStockReader();
      this.mlf = mf;
    } catch (Exception e) {
      System.out.println("Exception: " + e.getMessage());
    }

    frame.setTitle("Customer View");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(W, H);
    frame.setLocation(x, y);
    frame.getContentPane().setBackground(BACKGROUND_COLOR); // Set background color

    // Set up the layout
    frame.setLayout(new BorderLayout());

    // Top Panel for title
    JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    pageTitle.setFont(TITLE_FONT);
    pageTitle.setForeground(FOREGROUND_COLOR);
    titlePanel.add(pageTitle);
    titlePanel.setBackground(BACKGROUND_COLOR);
    frame.add(titlePanel, BorderLayout.NORTH);

    // Center Panel for search and results
    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.setBackground(BACKGROUND_COLOR);

    // Search Panel
    JPanel searchPanel = new JPanel();
    searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
    searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    searchLabel.setFont(TEXT_FONT);
    searchLabel.setForeground(FOREGROUND_COLOR);
    searchInput.setFont(TEXT_FONT);
    searchInput.setBackground(FOREGROUND_COLOR);
    searchInput.setForeground(BACKGROUND_COLOR);
    searchPanel.add(searchLabel);
    searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
    searchPanel.add(searchInput);
    centerPanel.add(searchPanel, BorderLayout.NORTH);

    // Product Table
    String[] columnNames = {"Product", "Description", "Price", "Quantity", "Image"};
    tableModel = new DefaultTableModel(columnNames, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false; // Make cells non-editable
      }
    };

    productTable = new JTable(tableModel);
    productTable.setRowHeight(100);
    productTable.getColumn("Image").setCellRenderer(new ImageRenderer());
    productTable.setBackground(BACKGROUND_COLOR);
    productTable.setForeground(FOREGROUND_COLOR);
    productTable.setFont(TEXT_FONT);
    productTable.setGridColor(FOREGROUND_COLOR);

    JTableHeader header = productTable.getTableHeader();
    header.setBackground(BACKGROUND_COLOR);
    header.setForeground(FOREGROUND_COLOR);
    header.setFont(TABLE_HEADER_FONT);

    tableScrollPane = new JScrollPane(productTable);
    tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    tableScrollPane.setBackground(BACKGROUND_COLOR);
    tableScrollPane.setBorder(BorderFactory.createLineBorder(FOREGROUND_COLOR));

    centerPanel.add(tableScrollPane, BorderLayout.CENTER);

    // South Panel for Basket
    JPanel southPanel = new JPanel(new BorderLayout());
    southPanel.setBackground(BACKGROUND_COLOR);

    // Basket Table
    String[] basketColumnNames = {"Product", "Description", "Quantity", "Price", "Actions"};
    basketTableModel = new DefaultTableModel(basketColumnNames, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return column == 4; // Only the actions column is editable
      }
    };
    basketTable = new JTable(basketTableModel);
    basketTable.setBackground(BACKGROUND_COLOR);
    basketTable.setForeground(FOREGROUND_COLOR);
    basketTable.setFont(TEXT_FONT);
    basketTable.setGridColor(FOREGROUND_COLOR);
    basketTable.setRowHeight(30);

    JTableHeader basketHeader = basketTable.getTableHeader();
    basketHeader.setBackground(BACKGROUND_COLOR);
    basketHeader.setForeground(FOREGROUND_COLOR);
    basketHeader.setFont(TABLE_HEADER_FONT);

    // Custom renderer for the "Actions" column
    basketTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
    basketTable.getColumn("Actions").setCellEditor(new ButtonEditor(new JTextField(), this));

    JScrollPane basketTableScrollPane = new JScrollPane(basketTable);
    basketTableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    basketTableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    basketTableScrollPane.setBackground(BACKGROUND_COLOR);
    basketTableScrollPane.setBorder(BorderFactory.createLineBorder(FOREGROUND_COLOR));
    southPanel.add(basketTableScrollPane, BorderLayout.CENTER);

    // Set preferred size for basketTableScrollPane
    basketTableScrollPane.setPreferredSize(new Dimension(400, 200)); // Adjust size as needed

    // Clear Basket Button
    JButton clearBasketButton = new JButton("Clear Basket");
    clearBasketButton.setFont(TEXT_FONT);
    clearBasketButton.setBackground(BUTTON_BACKGROUND_COLOR);
    clearBasketButton.setForeground(BUTTON_FOREGROUND_COLOR);
    clearBasketButton.setFocusPainted(false);
    clearBasketButton.setBorder(BorderFactory.createLineBorder(FOREGROUND_COLOR, 2));
    clearBasketButton.addActionListener(e -> cont.doClear());
    southPanel.add(clearBasketButton, BorderLayout.SOUTH);

    // Button for purchasing
    JButton purchaseButton = new JButton("Purchase");
    purchaseButton.setFont(TEXT_FONT);
    purchaseButton.setBackground(BUTTON_BACKGROUND_COLOR);
    purchaseButton.setForeground(BUTTON_FOREGROUND_COLOR);
    purchaseButton.setFocusPainted(false);
    purchaseButton.setBorder(BorderFactory.createLineBorder(FOREGROUND_COLOR, 2));
    purchaseButton.addActionListener(e -> {
      if (cont.getBasket() != null && !cont.getBasket().isEmpty()) {
        new PurchaseView(cont.getBasket(), this.mlf);
      } else {
        JOptionPane.showMessageDialog(frame, "Your basket is empty.", "Empty Basket", JOptionPane.INFORMATION_MESSAGE);
      }
    });
    southPanel.add(purchaseButton, BorderLayout.WEST);

    frame.add(centerPanel, BorderLayout.CENTER);
    frame.add(southPanel, BorderLayout.SOUTH);

    // Add DocumentListener for live search
    searchInput.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        updateProductDisplay();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        updateProductDisplay();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        updateProductDisplay();
      }
    });

    // Add MouseListener for adding products to basket
    productTable.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) { // Double-click
          int row = productTable.getSelectedRow();
          String productNum = (String) tableModel.getValueAt(row, 0);
          try {
            Product product = theStock.getDetails(productNum);
            if (product != null) {
              cont.addToBasket(product);
              updateBasketDisplay();
            }
          } catch (StockException ex) {
            JOptionPane.showMessageDialog(frame, "Error adding product to basket: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    });
    updateProductDisplay();
    frame.setVisible(true);
  }

  public void setController(CustomerController c) {
    cont = c;
  }

  @Override
  public void update(Observable modelC, Object arg) {
    CustomerModel model = (CustomerModel) modelC;
    String message = (String) arg;
    if (message != null && message.startsWith("Error:")) {
      JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    } else {
      updateProductDisplay();
      updateBasketDisplay();
      searchInput.requestFocus();
    }
  }

  private void updateProductDisplay() {
    try {
      Map<String, List<Integer>> stockInfo = theStock.getStockInfo();
      tableModel.setRowCount(0); // Clear existing table data

      String searchTerm = searchInput.getText().toLowerCase();

      for (Map.Entry<String, List<Integer>> entry : stockInfo.entrySet()) {
        String productNum = entry.getKey();
        List<Integer> details = entry.getValue();
        Product product = theStock.getDetails(productNum);

        if (product != null) {
          String description = product.getDescription().toLowerCase();
          if (searchTerm.isEmpty() || productNum.toLowerCase().contains(searchTerm) || description.contains(searchTerm)) {
            ImageIcon imageIcon = theStock.getImage(productNum);
            Image image = imageIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            ImageIcon scaledImageIcon = new ImageIcon(image);

            Object[] row = new Object[]{
                    productNum,
                    product.getDescription(),
                    product.getPrice(),
                    details.get(0),
                    scaledImageIcon
            };
            tableModel.addRow(row);
          }
        }
      }
    } catch (StockException e) {
      JOptionPane.showMessageDialog(null, "Error accessing stock information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  public void updateBasketDisplay() {
    basketTableModel.setRowCount(0); // Clear existing basket table data
    Basket basket = cont.getBasket();
    if (basket != null) {
      for (Product product : basket) {
        Object[] row = new Object[]{
                product.getProductNum(),
                product.getDescription(),
                product.getQuantity(),
                product.getPrice(),
                "Update" // This is a placeholder for the buttons
        };
        basketTableModel.addRow(row);
      }
    }
  }

  public void updateProductQuantity(String productNum, int newQuantity) {
    try {
      Product product = theStock.getDetails(productNum);
      if (product != null) {
        // Check if there's enough stock
        if (newQuantity <= theStock.getDetails(productNum).getQuantity()) {
          for (int i = 0; i < cont.getBasket().size(); i++) {
            if (cont.getBasket().get(i).getProductNum().equals(productNum)) {
              cont.getBasket().get(i).setQuantity(newQuantity);
              updateBasketDisplay();
              return;
            }
          }
        } else {
          JOptionPane.showMessageDialog(null, "Not enough stock for product: " + product.getDescription(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    } catch (StockException e) {
      JOptionPane.showMessageDialog(null, "Error updating product quantity: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  // Custom cell renderer for displaying images
  private static class ImageRenderer extends DefaultTableCellRenderer {
    JLabel lbl = new JLabel();

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
      if (value instanceof ImageIcon) {
        lbl.setIcon((ImageIcon) value);
        lbl.setText("");
      } else {
        lbl.setIcon(null);
        lbl.setText("No Image");
      }
      lbl.setHorizontalAlignment(JLabel.CENTER);
      return lbl;
    }
  }

  // Custom cell renderer for buttons
  class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
      setOpaque(true);
      setFont(TEXT_FONT);
      setBackground(BACKGROUND_COLOR);
      setForeground(FOREGROUND_COLOR);
      setFocusPainted(false);
      setBorder(BorderFactory.createLineBorder(FOREGROUND_COLOR, 2));
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
      setText((value == null) ? "" : value.toString());
      return this;
    }
  }

  // Custom cell editor for buttons
  class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;
    private int selectedRow;
    private CustomerView customerView;

    public ButtonEditor(JTextField textField, CustomerView customerView) {
      super(textField);
      this.customerView = customerView;
      button = new JButton();
      button.setOpaque(true);
      button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          fireEditingStopped();
        }
      });
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
      button.setForeground(FOREGROUND_COLOR);
      button.setBackground(BACKGROUND_COLOR);
      label = (value == null) ? "" : value.toString();
      button.setText(label);
      isPushed = true;
      selectedRow = row;
      return button;
    }

    public Object getCellEditorValue() {
      if (isPushed) {
        String productNum = (String) basketTableModel.getValueAt(selectedRow, 0);
        Product product = null;
        try {
          product = theStock.getDetails(productNum);
        } catch (StockException e) {
          throw new RuntimeException(e);
        }

        // Show a dialog to adjust quantity
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(product.getQuantity(), 0, product.getQuantity(), 1);
        JSpinner spinner = new JSpinner(spinnerModel);
        int option = JOptionPane.showOptionDialog(null, spinner, "Adjust Quantity",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

        if (option == JOptionPane.OK_OPTION) {
          int newQuantity = (int) spinner.getValue();
          customerView.updateProductQuantity(productNum, newQuantity);
        }
      }
      isPushed = false;
      return label;
    }

    public boolean stopCellEditing() {
      isPushed = false;
      return super.stopCellEditing();
    }

    protected void fireEditingStopped() {
      super.fireEditingStopped();
    }
  }
}
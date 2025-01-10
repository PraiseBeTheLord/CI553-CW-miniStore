package clients.customer;

import catalogue.Basket;
import catalogue.Product;
import middle.LocalMiddleFactory;
import middle.MiddleFactory;
import middle.OrderException;
import middle.OrderProcessing;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PurchaseView extends JFrame {
    private static final int H = 400;
    private static final int W = 500;
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color FOREGROUND_COLOR = Color.WHITE;
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Border BORDER = BorderFactory.createLineBorder(Color.WHITE, 2);

    private JLabel titleLabel;
    private JLabel basketDetailsLabel;
    private JTextArea basketDetailsArea;
    private JLabel paymentDetailsLabel;
    private JTextField cardNumberField;
    private JTextField expiryDateField;
    private JTextField cvvField;
    private JButton confirmPaymentButton;
    private Basket basket;
    private OrderProcessing orderProcessing;
    private MiddleFactory mlf;

    public PurchaseView(Basket basket, MiddleFactory mf) {
        this.basket = basket;
        this.mlf = mf;
        try {
            orderProcessing = mf.makeOrderProcessing();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error creating order processing object: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Set up the frame
        setTitle("Purchase Details");
        setSize(W, H);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose on close
        setLocationRelativeTo(null); // Center the window
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(BACKGROUND_COLOR);
        titleLabel = new JLabel("Purchase Details");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(FOREGROUND_COLOR);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Center Panel (Basket Details)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BACKGROUND_COLOR);
        basketDetailsLabel = new JLabel("Basket Details:");
        basketDetailsLabel.setFont(TEXT_FONT);
        basketDetailsLabel.setForeground(FOREGROUND_COLOR);
        basketDetailsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(basketDetailsLabel);

        basketDetailsArea = new JTextArea();
        basketDetailsArea.setEditable(false);
        basketDetailsArea.setFont(TEXT_FONT);
        basketDetailsArea.setBackground(BACKGROUND_COLOR);
        basketDetailsArea.setForeground(FOREGROUND_COLOR);
        JScrollPane basketScrollPane = new JScrollPane(basketDetailsArea);
        basketScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(basketScrollPane);

        // Set the basket details
        setBasketDetails();

        // Payment Details Panel
        JPanel paymentDetailsPanel = new JPanel();
        paymentDetailsPanel.setLayout(new GridLayout(0, 2, 5, 5));
        paymentDetailsPanel.setBackground(BACKGROUND_COLOR);
        paymentDetailsLabel = new JLabel("Payment Details:");
        paymentDetailsLabel.setFont(TEXT_FONT);
        paymentDetailsLabel.setForeground(FOREGROUND_COLOR);
        paymentDetailsPanel.add(paymentDetailsLabel);
        paymentDetailsPanel.add(new JLabel()); // Empty label for spacing

        JLabel cardNumberLabel = new JLabel("Card Number:");
        cardNumberLabel.setFont(TEXT_FONT);
        cardNumberLabel.setForeground(FOREGROUND_COLOR);
        paymentDetailsPanel.add(cardNumberLabel);
        cardNumberField = new JTextField();
        cardNumberField.setFont(TEXT_FONT);
        paymentDetailsPanel.add(cardNumberField);

        JLabel expiryDateLabel = new JLabel("Expiry Date (MM/YY):");
        expiryDateLabel.setFont(TEXT_FONT);
        expiryDateLabel.setForeground(FOREGROUND_COLOR);
        paymentDetailsPanel.add(expiryDateLabel);
        expiryDateField = new JTextField();
        expiryDateField.setFont(TEXT_FONT);
        paymentDetailsPanel.add(expiryDateField);

        JLabel cvvLabel = new JLabel("CVV:");
        cvvLabel.setFont(TEXT_FONT);
        cvvLabel.setForeground(FOREGROUND_COLOR);
        paymentDetailsPanel.add(cvvLabel);
        cvvField = new JTextField();
        cvvField.setFont(TEXT_FONT);
        paymentDetailsPanel.add(cvvField);

        centerPanel.add(paymentDetailsPanel);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel (Confirm Payment Button)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        confirmPaymentButton = new JButton("Confirm Payment");
        confirmPaymentButton.setFont(TEXT_FONT);
        confirmPaymentButton.setBackground(Color.WHITE);
        confirmPaymentButton.setForeground(Color.BLACK);
        confirmPaymentButton.setFocusPainted(false);
        confirmPaymentButton.setBorder(BORDER);
        confirmPaymentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Placeholder for payment processing logic
                // Collect payment details from cardNumberField, expiryDateField, cvvField
                // Validate payment details
                // Process payment (not implemented here)

                // Assuming payment is successful
                try {
                    orderProcessing.newOrder(basket);
                    JOptionPane.showMessageDialog(PurchaseView.this, "Payment successful. Order placed!", "Payment Confirmation", JOptionPane.INFORMATION_MESSAGE);
                    basket.clear(); // Clear the basket after successful purchase
                    dispose(); // Close the purchase window
                } catch (OrderException ex) {
                    JOptionPane.showMessageDialog(PurchaseView.this, "Error placing order: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        bottomPanel.add(confirmPaymentButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void setBasketDetails() {
        StringBuilder details = new StringBuilder();
        double total = 0;
        for (Product product : basket) {
            details.append(product.getProductNum())
                    .append(": ")
                    .append(product.getDescription())
                    .append(" x ")
                    .append(product.getQuantity())
                    .append(" - £")
                    .append(String.format("%.2f", product.getPrice() * product.getQuantity()))
                    .append("\n");
            total += product.getPrice() * product.getQuantity();
        }
        details.append("\nTotal: £").append(String.format("%.2f", total));
        basketDetailsArea.setText(details.toString());
    }

    // Placeholder for main method to test the PurchaseView
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PurchaseView(new Basket(), new LocalMiddleFactory()));
    }
}
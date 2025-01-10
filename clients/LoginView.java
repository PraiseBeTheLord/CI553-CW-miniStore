package clients;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel;

    private String userType; // "customer" or "staff"
    private LoginListener loginListener;

    public interface LoginListener {
        void onLoginSuccess(String userType);
        void onLoginFailure(String message);
    }

    public LoginView(String userType, LoginListener listener) {
        this.userType = userType;
        this.loginListener = listener;

        frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 250);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.BLACK); // Set background color

        // Set a custom font for better readability
        Font customFont = new Font("Segoe UI", Font.PLAIN, 16);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.BLACK);
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 5, 10)); // 3 rows, 2 columns
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        fieldsPanel.setBackground(Color.BLACK);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(customFont);
        usernameField = new JTextField();
        usernameField.setFont(customFont);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(customFont);
        passwordField = new JPasswordField();
        passwordField.setFont(customFont);

        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.RED);
        messageLabel.setFont(customFont);

        fieldsPanel.add(usernameLabel);
        fieldsPanel.add(usernameField);
        fieldsPanel.add(passwordLabel);
        fieldsPanel.add(passwordField);
        fieldsPanel.add(new JLabel("")); // Empty label for spacing
        fieldsPanel.add(messageLabel);

        frame.add(fieldsPanel, BorderLayout.CENTER);

        loginButton = new JButton("Login");
        loginButton.setFont(customFont);
        loginButton.setBackground(Color.WHITE);
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Basic validation (replace with your actual authentication logic)
                if (username.equals("customer") && password.equals("password") && userType.equals("customer")) {
                    loginListener.onLoginSuccess(userType);
                    frame.dispose();
                } else if (username.equals("staff") && password.equals("password") && userType.equals("staff")) {
                    loginListener.onLoginSuccess(userType);
                    frame.dispose();
                } else {
                    messageLabel.setText("Invalid credentials");
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.add(loginButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null); // Center on screen
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginView("customer", new LoginListener() {
            @Override
            public void onLoginSuccess(String userType) {
                System.out.println("Login successful for: " + userType);
            }

            @Override
            public void onLoginFailure(String message) {
                System.out.println("Login failed: " + message);
            }
        }));
    }
}
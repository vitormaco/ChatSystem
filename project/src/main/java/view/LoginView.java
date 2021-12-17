package view;

import services.MessageService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame implements ActionListener {

    private MessageService messageService;
    Container container = getContentPane();
    JLabel userLabel = new JLabel("NICKNAME");
    JTextField userTextField = new JTextField();
    JButton loginButton = new JButton("LOGIN");
    JButton resetButton = new JButton("RESET");

    public LoginView(MessageService messageService) {
        this.messageService = messageService;

        this.setTitle("Login Form");
        this.setVisible(true);
        this.setBounds(10, 10, 370, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);

        container.setLayout(null);

        userLabel.setBounds(50, 150, 100, 30);
        userTextField.setBounds(150, 150, 150, 30);
        loginButton.setBounds(50, 300, 100, 30);
        resetButton.setBounds(200, 300, 100, 30);

        container.add(userLabel);
        container.add(userTextField);
        container.add(loginButton);
        container.add(resetButton);

        loginButton.addActionListener(this);
        resetButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String userText;
            userText = userTextField.getText();

            if (this.messageService.validateAndAssingUserNickname(userText, "connected")) {
                // JOptionPane.showMessageDialog(this, "Login Successful");
                this.messageService.setChatView(new ChatView(this.messageService));
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username");
            }
        }

        if (e.getSource() == resetButton) {
            userTextField.setText("");
        }
    }
}

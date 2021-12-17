package view;

import services.MessageService;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends BaseView implements ActionListener {
    private MessageService messageService;
    Container container = getContentPane();

    JLabel welcomeMessage = new JLabel("Login with your chosen nickname");
    JLabel userLabel = new JLabel("NICKNAME");
    JTextField userTextField = new JTextField();
    JButton loginButton = new JButton("LOGIN");

    public LoginView(MessageService messageService) {
        super();
        this.setTitle("Login");
        this.messageService = messageService;
        buildPanel();
    }

    private void buildPanel() {
        JPanel centerPanel = new JPanel();

        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        centerPanel.setBackground(Color.PINK);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0,0,10,0);  //top padding
        welcomeMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(welcomeMessage, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(userLabel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        c.ipady = 40; // size
        userTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(userTextField, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(loginButton, c);

        container.add(centerPanel, BorderLayout.CENTER);

        setActionListeners();
    }

    private void setActionListeners() {
        loginButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String userText;
        userText = userTextField.getText();

        if (this.messageService.validateAndAssingUserNickname(userText, "connected")) {
            dispose();
            this.messageService.setChatView(new ChatView(this.messageService));
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username");
        }
    }
}

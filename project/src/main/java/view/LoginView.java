package view;

import services.MessageService;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import io.github.cdimascio.dotenv.Dotenv;
import models.MessagePDU;

public class LoginView extends BaseView implements ActionListener {
    static Dotenv dotenv = Dotenv.load();

    private MessageService messageService;
    Container container = getContentPane();

    JLabel iconLabel = new JLabel();
    JLabel loginMessageLabel = new JLabel();
    JTextField nicknameTextField = new JTextField();
    JButton loginButton = new JButton();

    public LoginView(MessageService messageService) {
        super();
        this.setTitle("Login");
        this.messageService = messageService;
        seticonLabel();
        setLoginMessageLabel();
        setLoginButton();
        setNicknameTextField();
        buildPanel();
        setActionListeners();
    }

    private void seticonLabel() {
        try {
            iconLabel.setSize(100, 100);
            String imagePath = dotenv.get("STATIC_FOLDER") + "/login-icon.png";
            ImageIcon originalIcon = new ImageIcon(imagePath);
            ImageIcon scaledIcon = new ImageIcon(originalIcon.getImage()
                    .getScaledInstance(iconLabel.getWidth(), iconLabel.getHeight(), Image.SCALE_DEFAULT));
            iconLabel.setIcon(scaledIcon);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error opening login icon file");
        }
    }

    private void setLoginMessageLabel() {
        loginMessageLabel.setText("Choose a nickname and login");
        Font font = new Font("SansSerif", Font.BOLD, 15);
        loginMessageLabel.setFont(font);
    }

    private void setNicknameTextField() {
        Font font = new Font("SansSerif", Font.BOLD, 20);
        nicknameTextField.setFont(font);
        nicknameTextField.setMinimumSize(new Dimension(300, 40));
        nicknameTextField.setPreferredSize(new Dimension(300, 40));
    }

    private void setLoginButton() {
        loginButton.setText("LOGIN");
        Font font = new Font("SansSerif", Font.BOLD, 15);
        loginButton.setFont(font);
    }

    private void buildPanel() {
        JPanel centerPanel = new JPanel();

        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        centerPanel.setBackground(Color.LIGHT_GRAY);

        // common constraints
        c.insets = new Insets(0, 0, 10, 0); // top padding
        c.weightx = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;

        // specific components
        c.gridy = 0;
        centerPanel.add(iconLabel, c);

        c.gridy = 1;
        centerPanel.add(loginMessageLabel, c);

        c.gridy = 2;
        centerPanel.add(nicknameTextField, c);

        c.gridy = 3;
        centerPanel.add(loginButton, c);

        container.add(centerPanel, BorderLayout.CENTER);
    }

    private void setActionListeners() {
        loginButton.addActionListener(this);
        SwingUtilities.getRootPane(loginButton).setDefaultButton(loginButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String userText;
        userText = nicknameTextField.getText();
        if (!this.messageService.isConnected()) {
            JOptionPane.showMessageDialog(this, "Network not configured correctly");
        }

        if (this.messageService.validateAndAssingUserNickname(userText, MessagePDU.Status.CONNECTION)) {
            dispose();
            this.messageService.setChatView();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username");
        }
    }
}

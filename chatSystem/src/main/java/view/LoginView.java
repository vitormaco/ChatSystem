package view;

import javax.swing.*;
import java.awt.event.*;

public class LoginView implements ActionListener {
    JFrame frame = new JFrame("My window");
    JButton button = new JButton("Click Me");// Creating object of JButton class

    public LoginView() {
        createWindow();
        createButton();
    }

    private void createWindow() {
        frame.getContentPane().setLayout(null);
        frame.setVisible(true);
        frame.setBounds(200, 200, 400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void createButton() {
        button.setBounds(130, 200, 100, 40);
        frame.add(button);
        button.addActionListener(this);// Registering ActionListener to the button
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.print("The button was clicked\n");
    }
}

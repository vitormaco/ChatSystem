package view;

import javax.swing.*;

import io.github.cdimascio.dotenv.Dotenv;

import java.awt.*;

public class BaseView extends JFrame {
    protected int windowWidth;
    protected int windowHeight;
    protected int widthOffset;
    protected int heightOffset;
    private Dotenv dotenv = Dotenv.load();

    protected BaseView() {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) size.getWidth();
        int screenHeight = (int) size.getHeight();
        double scaleFactor = Double.parseDouble(dotenv.get("SCREEN_WINDOW_RATIO"));
        windowWidth = (int) (screenWidth * scaleFactor);
        windowHeight = (int) (screenHeight * scaleFactor);
        widthOffset = (int) (screenWidth - windowWidth) / 2;
        heightOffset = (int) (screenHeight - windowHeight) / 2;

        this.setTitle("App");
        this.setVisible(true);
        this.setBounds(widthOffset, heightOffset, windowWidth, windowHeight);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

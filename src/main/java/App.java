import services.MessageService;
import services.NetworkListener;
import view.LoginView;
import models.MessagePDU;
import java.io.*;

public class App {
    public static void main(String[] args) {
        final MessageService messageService = new MessageService();
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
             public void run() {
                 new LoginView(messageService);
             }
        });

    }
}

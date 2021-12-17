import services.MessageService;
import view.LoginView;

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

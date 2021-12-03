import services.MessageService;
import view.LoginView;

public class App {
    public static void main(String[] args) {
        final MessageService messageService = new MessageService();
        try {
            MessageService.broadcast();
            messageService.receiveMessage();
        } catch (Exception e) {
            //TODO: handle exception
        }

        // javax.swing.SwingUtilities.invokeLater(new Runnable() {
        //     public void run() {
        //         new LoginView(messageService);
        //     }
        // });
    }
}

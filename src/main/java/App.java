import services.MessageService;
import services.NetworkListener;
import view.LoginView;

public class App {
    public static void main(String[] args) {
        final MessageService messageService = new MessageService();
        NetworkListener listener = new NetworkListener(4446);
        listener.run();
        messageService.sendBroadcastMessage("teste123", 4446);

        // javax.swing.SwingUtilities.invokeLater(new Runnable() {
        //     public void run() {
        //         new LoginView(messageService);
        //     }
        // });

    }
}

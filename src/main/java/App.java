import services.MessageService;
import view.LoginView;

public class App {
    public static void main(String[] args) {
        final MessageService messageService = new MessageService();
        try {
            EchoClient client = new EchoClient();
            client.sendEcho("funciona");
            EchoServer server = new EchoServer();
            server.run();
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

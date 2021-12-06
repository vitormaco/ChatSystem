import services.MessageService;
import services.NetworkListener;
import view.LoginView;
import models.MessagePDU;
import java.io.*;

public class App {
    public static void main(String[] args) {
        final MessageService messageService = new MessageService();

    	String serializedObject = new MessagePDU()
    			.withMessageContent("hola putos")
    			.withStatus(MessagePDU.Status.ACTIVE)
    			.withSourceNickname("Luisinho97")
    			.serialize();

        messageService.sendBroadcastMessage(serializedObject, 4446);
		System.out.println(serializedObject);

        //NetworkListener listener = new NetworkListener(4446);
        //listener.run();
    	
        
        // javax.swing.SwingUtilities.invokeLater(new Runnable() {
        //     public void run() {
        //         new LoginView(messageService);
        //     }
        // });

    }
}

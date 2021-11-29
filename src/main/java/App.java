import view.LoginView;

public class App
{
    public static void main( String[] args )
    {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginView();
            }
        });
        new MessageService();
    }
}

import java.net.*;
import java.io.*;

class ClientThread extends Thread {

    Socket serverClient;
    int clientNo;

    ClientThread(Socket s, int id){
        serverClient = s;
        clientNo = id;
    }

    public void run(){
        try {
            DataInputStream in = new DataInputStream(serverClient.getInputStream());
            DataOutputStream out = new DataOutputStream(serverClient.getOutputStream());
            String clientMessage = "", serverMessage = "";
            while(!clientMessage.equals("bye")){
                clientMessage = in.readUTF();
                System.out.println("Client: " + clientNo +  " Message: " + clientMessage);
                serverMessage = "From server to client " + clientNo + " " + clientMessage + " I'm your server";
                out.writeUTF(serverMessage);
            }
            in.close();
            out.close();
            serverClient.close();
        } catch (Exception e) {
            System.out.println("client error " + e.getMessage());
        } finally {
            System.out.println("Client: " + clientNo +  " exit!!");
        }

    }
}

public class Server {
	public Server() {
		try {
            int port = 8000;
            ServerSocket ss = new ServerSocket(port);
            int counter = 0;
            System.out.println("Server start...");
            while(true){
                counter++;
                Socket serverClient = ss.accept();
                System.out.println("* Client no: " +  counter + " started");
                ClientThread ct = new ClientThread(serverClient, counter);
                ct.start();
                if(counter == 5)
                    ss.close();   
            }            
        } catch (Exception e) {
            System.out.print("Error: ");
            System.out.println(e.toString());
        }
	}
	

}

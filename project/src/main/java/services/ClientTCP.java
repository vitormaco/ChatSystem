package services;

import java.net.*;
import java.io.*;

public class ClientTCP {

    private Socket socket;
    private DataOutputStream out;

    public ClientTCP(String host, int port, String myMAC){
        try {
            this.socket = new Socket(host, port);
            this.out = new DataOutputStream(socket.getOutputStream());
            this.sendMessage(myMAC);
        } catch (Exception e) {
            e.printStackTrace();
			System.out.println("Exception thrown when creating client TCP");
        }
    }

    public void sendMessage(String message){
        try {
            out.writeUTF(message);
            out.flush();
        } catch (Exception e) {
        	e.printStackTrace();
			System.out.println("Exception thrown when sendMessage client TCP");
        }
    }

    public void closeSocket(){
        try {
            System.out.println("closed socket");
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
			System.out.println("Exception thrown when closing socket client TCP");
        }
    }
}

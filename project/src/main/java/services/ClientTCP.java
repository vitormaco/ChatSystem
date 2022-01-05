package services;

import java.net.*;
import java.io.*;

public class ClientTCP {

    private Socket socket;

    public ClientTCP(String host, int port){
        try {
            this.socket = new Socket(host, port);
        } catch (Exception e) {
            e.printStackTrace();
			System.out.println("Exception thrown when creating client TCP");
        }
    }

    public void sendMessage(String message){
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(message);
            out.flush();
            out.close();
        } catch (Exception e) {
        	e.printStackTrace();
			System.out.println("Exception thrown when sendMessage client TCP");
        }
    }

    public void closeSocket(){
        try {
            System.out.println("closed socket");
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
			System.out.println("Exception thrown when closing socket client TCP");
        }
    }
}

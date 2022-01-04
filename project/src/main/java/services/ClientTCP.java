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
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            String clientMessage = "";
            while(!clientMessage.equals("bye")){
                clientMessage = message;
                out.writeUTF(clientMessage);
                out.flush();
                System.out.println("client message: " + clientMessage);
            }
            out.close();
            in.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("Client Error " +  e.getMessage());
        }
    }
}

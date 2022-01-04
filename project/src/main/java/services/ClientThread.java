package services;

import java.net.*;
import java.io.*;

public class ClientThread extends Thread {

    Socket serverClient;
    int clientNo;

    ClientThread(Socket s){
        serverClient = s;
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
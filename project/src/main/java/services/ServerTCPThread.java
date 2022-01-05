package services;

import java.net.*;
import java.io.*;

public class ServerTCPThread extends Thread {

    Socket serverClient;
    private boolean running;

    ServerTCPThread(Socket s){
        serverClient = s;
    }

    public void run(){
        try {
        	running = true;
            DataInputStream in = new DataInputStream(serverClient.getInputStream());
            DataOutputStream out = new DataOutputStream(serverClient.getOutputStream());
            String clientMessage = "", serverMessage = "";
            while(running){
                clientMessage = in.readUTF();
                System.out.println("Client: " +  " Message: " + clientMessage);
                serverMessage = "From server to client " + clientMessage + " I'm your server";
                out.writeUTF(serverMessage);
            }
            in.close();
            out.close();
            serverClient.close();
        } catch (Exception e) {
            System.out.println("client error " + e.getMessage());
        } finally {
            System.out.println("Client: " +  " exit!!");
        }

    }

    public void setRunning(boolean running) {
    	this.running = running;
    }
}
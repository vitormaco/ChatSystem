package services;

import java.net.*;
import java.io.*;

public class ServerTCPThread extends Thread {

    Socket serverClient;
    private boolean running;
    private DataInputStream in;
    private String clientMAC = "";
    
    ServerTCPThread(Socket s){
        this.serverClient = s;
        try {
			this.in = new DataInputStream(serverClient.getInputStream());
			this.clientMAC = this.in.readUTF();
            System.out.println("New connection with client: " + this.clientMAC);		
        } catch (IOException e) {
			System.out.println("Error while creating DIS Server TCP Thread");
		}
    }

    public void run(){
    	running = true;

        try {
            while(running){
                String clientMessage = in.readUTF();
                System.out.println("Client: " +  " Message: " + clientMessage);
                // messageService.receiveUserMessage(message);
            }
            
            in.close();
            serverClient.close();
        } catch (Exception e) {
        	System.out.println("SERVER TCP THREAD - LOST CONNECTION");
        }
    }

    public void setRunning(boolean running) {
    	this.running = running;
    }
}
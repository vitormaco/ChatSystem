package services;

import java.net.*;
import java.io.*;
import models.Message;

public class ServerTCPThread extends Thread {

    Socket serverClient;
    private boolean running;
    private DataInputStream in;
    private String clientMAC = "";
    private NetworkTCPListener parent;
    
    ServerTCPThread(Socket s, NetworkTCPListener parent){
        this.serverClient = s;
        this.parent = parent;
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
                String clientContent = in.readUTF();
                Message message = new Message(clientContent, true);
                parent.saveMessage(this.clientMAC, message);
                System.out.println("Client: " +  " Message: " + clientContent);
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
package services;

import java.net.*;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.*;
import models.Message;

public class ServerTCPThread extends Thread {

    Socket serverClient;
    private boolean running;
    private DataInputStream in;
    private String clientMAC;
    private MessageService messageService;
    private Dotenv dotenv = Dotenv.load();
    private int timeout = Integer.parseInt(dotenv.get("SOCKETS_TIMEOUT"));

    ServerTCPThread(Socket s, MessageService messageService) {
        this.serverClient = s;
        this.messageService = messageService;
        try {
        	this.serverClient.setSoTimeout(timeout);
            this.in = new DataInputStream(serverClient.getInputStream());
            this.clientMAC = this.in.readUTF();
            System.out.println("New connection with client: " + this.clientMAC);
        } catch (IOException e) {
            System.out.println("Error while creating DIS Server TCP Thread");
        }
    }

    public void run() {
        running = true;
        
        try {
    		while (running) {
    			try {                
    				String clientContent = in.readUTF();
    				Message message = new Message(clientContent, true);
    				this.messageService.receiveUserMessage(this.clientMAC, message);
    				System.out.println("Client: " + " Message: " + clientContent);
    			} catch (SocketTimeoutException e) {
    				continue;
    			}
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

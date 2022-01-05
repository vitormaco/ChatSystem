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
    	running = true;

        try {
        	DataInputStream in = new DataInputStream(serverClient.getInputStream());

            while(running){
                String clientMessage = in.readUTF();
                System.out.println("Client: " +  " Message: " + clientMessage);
            }
            
            in.close();
            serverClient.close();
        } catch (Exception e) {
        	System.out.println("SERVER TCP THREAD - ERROR");
        	e.printStackTrace();
        }
    }

    public void setRunning(boolean running) {
    	this.running = running;
    }
}
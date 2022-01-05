package services;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import services.ServerTCPThread;

import models.MessagePDU;

public class NetworkTCPListener extends Thread {
    private ServerSocket serverSocket;
    private Socket serverClient;
    private boolean running;
    private byte[] buf = new byte[65536];
    private MessageService messageService;
    private ArrayList<ServerTCPThread> myThreads;

    public NetworkTCPListener(int port, MessageService messageService) {
        try {
            this.serverSocket = new ServerSocket(port);
            this.messageService = messageService;
            this.myThreads = new ArrayList<ServerTCPThread> ();
        } catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception thrown when creating network TCP listener");
        }
    }

    public void run() {
        try {
            listenToNetwork();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listenToNetwork() throws Exception {
        System.out.println("NetworkTCPListener up");
        running = true;

        while (running) {
            this.serverClient = serverSocket.accept();
            ServerTCPThread st = new ServerTCPThread(this.serverClient);
            st.start();
            myThreads.add(st);
        }
        
        for(ServerTCPThread st : myThreads) {
        	if(st.isAlive()) {
	        	st.setRunning(false);
	            while (st.isAlive())
	    			;
        	}
        }
        
        serverSocket.close();
        
    }

    public void setRunning(boolean running) {
    	this.running = running;
    }
    
}

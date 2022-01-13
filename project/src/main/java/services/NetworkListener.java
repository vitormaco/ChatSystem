package services;
import java.net.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import models.MessagePDU;

public class NetworkListener extends Thread {
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[65536];
    private MessageService messageService;
    private HashMap<String, Integer> lifeCounter;

    public NetworkListener(int port, MessageService messageService) {
        try {
            socket = new DatagramSocket(port);
            this.messageService = messageService;
            lifeCounter = new HashMap<String, Integer> ();
        } catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception thrown when creating network listener");
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
        System.out.println("NetworkListener up");

        running = true;
        socket.setSoTimeout(1000);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
        	  @Override
        	  public void run() {
        		  checkLifeCounter();
        	  }
        	}, 5000, 5000);

        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                String package_received = new String(packet.getData(), 0, packet.getLength());
                MessagePDU deserializedObject = MessagePDU.deserialize(package_received);
	            resetLifeCounter(deserializedObject.getSourceMAC());
                this.messageService.broadcastMessageReceived(deserializedObject);
            } catch (SocketTimeoutException e) {};
        }

        socket.close();
    }

    public void setRunning(boolean running) {
    	this.running = running;
    }

    public void increaseLifeCounter(String mac) {
    	lifeCounter.put(mac, lifeCounter.get(mac) + 1);
    }

    public void resetLifeCounter(String mac) {
    	lifeCounter.put(mac, 0);
    }

    public void checkLifeCounter() {
    	for(Map.Entry<String, Integer> user : lifeCounter.entrySet()) {
    		String mac = user.getKey();
    		Integer counter = user.getValue();
    		increaseLifeCounter(mac);
    		if(counter >= 5) {
    			messageService.deleteLoggedoutUser(mac);
    			lifeCounter.remove(mac);
    		}
     	}
    }
}

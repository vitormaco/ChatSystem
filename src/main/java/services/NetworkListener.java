package services;
import java.net.*;

import models.MessagePDU;

public class NetworkListener extends Thread {
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[65536];

    public NetworkListener(int port) {
        try {
            socket = new DatagramSocket(port);
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

        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String package_received = new String(packet.getData(), 0, packet.getLength());
    		MessagePDU deserializedObject = MessagePDU.deserialize(package_received);
            System.out.println("listener received: " + deserializedObject.getSourceNickname());
        }

        socket.close();
    }
    
    public void setRunning(boolean running) {
    	this.running = running;
    }
}

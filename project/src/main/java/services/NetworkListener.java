package services;
import java.net.*;

import models.MessagePDU;

public class NetworkListener extends Thread {
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[65536];
    private MessageService messageService;

    public NetworkListener(int port, MessageService messageService) {
        try {
            socket = new DatagramSocket(port);
            this.messageService = messageService;
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
        int message_counter = 0;
        socket.setSoTimeout(1000);

        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                String package_received = new String(packet.getData(), 0, packet.getLength());
                MessagePDU deserializedObject = MessagePDU.deserialize(package_received);
                System.out.println(
                    "message " + message_counter++ + ":\n" +
                    "-------------------------\n" +
                    deserializedObject.toString() +
                    "-------------------------" +
                    "\n\n"
                    );
                this.messageService.messageReceived(deserializedObject);
            } catch (SocketTimeoutException e) {};
        }

        socket.close();
    }

    public void setRunning(boolean running) {
    	this.running = running;
    }
}
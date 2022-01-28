package services;

import java.net.*;
import io.github.cdimascio.dotenv.Dotenv;
import models.MessagePDU;
import utils.NetworkUtils;

public class NetworkUDPListener extends Thread {
    private DatagramSocket socket;
    private boolean running = true;
    private byte[] buf = new byte[65536];
    private MessageService messageService;
    private Dotenv dotenv = Dotenv.load();
    private int timeout = Integer.parseInt(dotenv.get("SOCKETS_TIMEOUT"));

    public NetworkUDPListener(int port, MessageService messageService) {
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

        socket.setSoTimeout(timeout);

        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                String package_received = new String(packet.getData(), 0, packet.getLength());
                MessagePDU deserializedObject = MessagePDU.deserialize(package_received);
                if (!deserializedObject.getSourceMAC().equals(NetworkUtils.getLocalMACAdress())) {
                    this.messageService.receiveBroadcastMessage(deserializedObject);
                }
            } catch (SocketTimeoutException e) {
            }
            ;
        }

        socket.close();
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}

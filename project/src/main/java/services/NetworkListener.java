package services;

import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.github.cdimascio.dotenv.Dotenv;
import models.MessagePDU;

public class NetworkListener extends Thread {
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[65536];
    private MessageService messageService;
    private Dotenv dotenv = Dotenv.load();
    private HashMap<String, Integer> lifeCounter;
    private int maxLife = Integer.parseInt(dotenv.get("CHECKLIFE_MAX"));
    private int timeout = Integer.parseInt(dotenv.get("SOCKETS_TIMEOUT"));
    private int checklife = Integer.parseInt(dotenv.get("CHECKLIFE_TIMER"));

    public NetworkListener(int port, MessageService messageService) {
        try {
            socket = new DatagramSocket(port);
            this.messageService = messageService;
            lifeCounter = new HashMap<String, Integer>();
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
        socket.setSoTimeout(timeout);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkLifeCounter();
            }
        }, checklife, checklife);

        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                String package_received = new String(packet.getData(), 0, packet.getLength());
                MessagePDU deserializedObject = MessagePDU.deserialize(package_received);
                resetLifeCounter(deserializedObject.getSourceMAC());
                this.messageService.broadcastMessageReceived(deserializedObject);
            } catch (SocketTimeoutException e) {
            }
            ;
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
        ArrayList<String> removeList = new ArrayList<String>();
        for (Map.Entry<String, Integer> user : lifeCounter.entrySet()) {
            String mac = user.getKey();
            Integer counter = user.getValue();
            increaseLifeCounter(mac);
            if (counter >= maxLife) {
                removeList.add(mac);
            }
        }
        for (int i = 0; i < removeList.size(); i++) {
            String mac = removeList.get(i);
            messageService.deleteLoggedoutUser(mac);
            lifeCounter.remove(mac);
        }
    }
}

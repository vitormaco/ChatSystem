package services;
import java.net.*;

public class NetworkListener extends Thread {
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];

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
            System.out.println("listener received: " + package_received);
        }

        socket.close();
    }
}

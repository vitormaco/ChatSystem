import java.net.*;

public class EchoServer extends Thread {
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];

    public EchoServer() throws Exception {
        socket = new DatagramSocket(4446);
    }

    public void run() {

        System.out.println("started server");

        running = true;

        while (running) {
            DatagramPacket packet
              = new DatagramPacket(buf, buf.length);
              try {
                  socket.receive(packet);
              } catch (Exception e) {
                  //TODO: handle exception
              }

            System.out.println("received message");

            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            String received
            = new String(packet.getData(), 0, packet.getLength());

            System.out.println(received);

            if (received.equals("end")) {
                running = false;
                continue;
            }
            try {
                socket.send(packet);
            } catch (Exception e) {
                //TODO: handle exception
            }
        }
        socket.close();
    }
}

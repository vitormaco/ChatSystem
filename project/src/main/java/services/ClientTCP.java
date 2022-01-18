package services;

import java.net.*;

import java.io.*;

public class ClientTCP {

    private Socket socket;
    private DataOutputStream out;

    public ClientTCP(String host, int port, String myMAC) {
        try {
            this.socket = new Socket(host, port);
            this.out = new DataOutputStream(socket.getOutputStream());
            this.sendMessage(myMAC);
            System.out.println("TCP Socket opened");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception thrown when creating client TCP");
        }
    }

    public boolean sendMessage(String message) {
        try {
            out.writeUTF(message);
            out.flush();
            return true;
        } catch (SocketException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void closeSocket() {
        try {
            System.out.println("TCP Socket closed");
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception thrown when closing socket client TCP");
        }
    }
}

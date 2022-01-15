package services;

import java.net.*;

import java.util.ArrayList;

import io.github.cdimascio.dotenv.Dotenv;

public class NetworkTCPListener extends Thread {
    private ServerSocket serverSocket;
    private boolean running;
    private MessageService messageService;
    private ArrayList<ServerTCPThread> myThreads;
    private Dotenv dotenv = Dotenv.load();
    private int timeout = Integer.parseInt(dotenv.get("SOCKETS_TIMEOUT"));

    public NetworkTCPListener(int port, MessageService messageService) {
        try {
            this.serverSocket = new ServerSocket(port);
            this.messageService = messageService;
            this.myThreads = new ArrayList<ServerTCPThread>();
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
        serverSocket.setSoTimeout(timeout);
        running = true;

        while (running) {
            try {
                Socket serverClient = serverSocket.accept();
                ServerTCPThread st = new ServerTCPThread(serverClient, this.messageService);
                st.start();
                myThreads.add(st);
            } catch (SocketTimeoutException e) {
            }
            ;
        }

        for (ServerTCPThread st : myThreads) {
            if (st.isAlive()) {
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

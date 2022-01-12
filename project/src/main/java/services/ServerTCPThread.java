package services;

import java.net.*;
import java.io.*;
import models.Message;

public class ServerTCPThread extends Thread {

    Socket serverClient;
    private boolean running;
    private DataInputStream in;
    private String clientMAC;
    private MessageService messageService;

    ServerTCPThread(Socket s, MessageService messageService) {
        this.serverClient = s;
        this.messageService = messageService;
        try {
            this.in = new DataInputStream(serverClient.getInputStream());
            this.clientMAC = this.in.readUTF();
            System.out.println("New connection with client: " + this.clientMAC);
        } catch (IOException e) {
            System.out.println("Error while creating DIS Server TCP Thread");
        }
    }

    public void run() {
        running = true;

        try {
            while (running) {
                String clientContent = in.readUTF();
                Message message = new Message(clientContent, true);
                this.messageService.receiveUserMessage(this.clientMAC, message);
                System.out.println("Client: " + " Message: " + clientContent);
            }

            in.close();
            serverClient.close();
        } catch (Exception e) {
            System.out.println("SERVER TCP THREAD - LOST CONNECTION");
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}

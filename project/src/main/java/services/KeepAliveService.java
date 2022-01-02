package services;

import java.util.*;

public class KeepAliveService extends Thread {

    private boolean running;
    private MessageService messageService;

    public KeepAliveService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void run() {
        try {
            broadcastImAliveMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcastImAliveMessage() throws Exception {
        System.out.println("Broadcast I'm Alive message");

        running = true;

        while (running) {
            this.sleep(5000);
            this.messageService.notifyUserStateChanged("connected");
        }
    }
    
}

package services;

import models.MessagePDU;

import io.github.cdimascio.dotenv.Dotenv;

public class ImAliveService extends Thread {

    private boolean running;
    private MessageService messageService;
    private Dotenv dotenv = Dotenv.load();
    private int aliveTime = Integer.parseInt(dotenv.get("ALIVE_TIME"));

    public ImAliveService(MessageService messageService) {
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
            Thread.sleep(aliveTime);
            this.messageService.notifyUserStateChanged(MessagePDU.Status.CONNECTION);
            this.messageService.checkAliveUsers();
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

}

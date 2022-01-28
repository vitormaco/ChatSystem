package services;

import models.MessagePDU;

import utils.ConfigManager;

public class ImAliveService extends Thread {

    private boolean running;
    private MessageService messageService;
    private ConfigManager properties = new ConfigManager();
    private int aliveTime = Integer.parseInt(properties.get("BROADCAST_IM_ALIVE_TIME"));

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

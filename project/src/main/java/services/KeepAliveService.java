package services;

import io.github.cdimascio.dotenv.Dotenv;

public class KeepAliveService extends Thread {

    private boolean running;
    private MessageService messageService;
    private Dotenv dotenv = Dotenv.load();
    private int aliveTime = Integer.parseInt(dotenv.get("ALIVE_TIME"));

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
            Thread.sleep(aliveTime);
            this.messageService.notifyUserStateChanged("connected");
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

}

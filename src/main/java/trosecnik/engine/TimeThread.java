package trosecnik.engine;

import javafx.application.Platform;
import trosecnik.model.Player;

public class TimeThread extends Thread {

    private boolean isRunning = true;
    private Player player;
    private Runnable onTick;

    public TimeThread(Player player, Runnable onTick) {
        this.player = player;
        this.onTick = onTick;
    }

    public void stopTime() {
        this.isRunning = false;
    }

    @Override
    public void run() {
        System.out.println("Vlákno času spuštěno! Tik... ťak...");

        while (isRunning) {
            try {
                Thread.sleep(3000);

                if (player != null) {
                    Platform.runLater(() -> {
                        player.decreaseHunger(1);

                        if (onTick != null) {
                            onTick.run();
                        }
                    });
                }

            } catch (InterruptedException e) {
                System.out.println("Vlákno času bylo přerušeno.");
                break;
            }
        }
    }
}
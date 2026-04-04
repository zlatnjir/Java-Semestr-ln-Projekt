package trosecnik.engine;

import javafx.application.Platform;
import trosecnik.model.Player;

public class TimeThread extends Thread {

    private volatile boolean isRunning = true;
    private volatile boolean isPaused = false;
    private final Player player;
    private final Runnable onTick;

    public TimeThread(Player player, Runnable onTick) {
        this.player = player;
        this.onTick = onTick;
    }

    public void stopTime() {
        this.isRunning = false;
    }

    public void setPaused(boolean paused) {
        this.isPaused = paused;
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        System.out.println("Vlákno času spuštěno! Tik... ťak...");

        while (isRunning) {
            try {
                Thread.sleep(3000);

                if (!isPaused && player != null) {
                    Platform.runLater(() -> {
                        player.decreaseHunger(1);
                        if (onTick != null) {
                            onTick.run();
                        }
                    });
                }
            } catch (InterruptedException e) {
                System.out.println("Vlákno času bylo přerušeno.");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
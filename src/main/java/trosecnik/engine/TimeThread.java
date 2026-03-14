package trosecnik.engine;

public class TimeThread extends Thread {
    private boolean isRunning = true;

    @Override
    public void run() {
        while (isRunning) {
            try {

                Thread.sleep(5000);
                // TODO: logika pro snížení hladu hráče
                System.out.println("Uběhlo 5 vteřin, hráč dostává hlad.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopTime() {
        this.isRunning = false;
    }
}
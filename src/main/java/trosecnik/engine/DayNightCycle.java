package trosecnik.engine;

public class DayNightCycle {
    private int gameHour;
    private boolean isNight;

    public DayNightCycle() {
        this.gameHour = 8;
        this.isNight = false;
    }


    public void updateTime() {

    }

    public boolean isNight() { return isNight; }
    public int getGameHour() { return gameHour; }
}
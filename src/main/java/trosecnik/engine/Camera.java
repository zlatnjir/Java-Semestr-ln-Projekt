package trosecnik.engine;

import trosecnik.model.Player;

public class Camera {
    private int offsetX;
    private int offsetY;

    public Camera() {
        this.offsetX = 0;
        this.offsetY = 0;
    }

    // Metoda, která se později postará o výpočet toho, co má hráč vidět
    public void update(Player player, GameMap map) {
        // Zatím prázdné
    }

    public int getOffsetX() { return offsetX; }
    public int getOffsetY() { return offsetY; }
}
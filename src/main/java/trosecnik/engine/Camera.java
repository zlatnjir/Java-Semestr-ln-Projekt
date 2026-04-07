package trosecnik.engine;

import trosecnik.model.Player;

public class Camera {
    private int offsetX;
    private int offsetY;

    private final int viewportWidth = 10;
    private final int viewportHeight = 8;

    public Camera() {
        this.offsetX = 0;
        this.offsetY = 0;
    }

    public void update(Player player, GameMap map) {
        offsetX = player.getX() - (viewportWidth / 2);
        offsetY = player.getY() - (viewportHeight / 2);

        if (offsetX < 0) offsetX = 0;
        if (offsetY < 0) offsetY = 0;

        if (offsetX > map.getWidth() - viewportWidth) offsetX = map.getWidth() - viewportWidth;
        if (offsetY > map.getHeight() - viewportHeight) offsetY = map.getHeight() - viewportHeight;

        if (map.getWidth() < viewportWidth) offsetX = 0;
        if (map.getHeight() < viewportHeight) offsetY = 0;
    }

    public int getOffsetX() { return offsetX; }
    public int getOffsetY() { return offsetY; }
}
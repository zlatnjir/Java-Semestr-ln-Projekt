package trosecnik.engine;

public class GameMap {
    private int width;
    private int height;
    private int[][] tiles;

    public void initMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new int[width][height];
    }

    public boolean isWalkable(int x, int y) {
        // TODO: Logika pro zjištění kolize s jiným matrem
        return true;
    }
}
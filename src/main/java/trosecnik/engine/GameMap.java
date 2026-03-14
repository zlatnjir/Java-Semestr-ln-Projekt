package trosecnik.engine;

public class GameMap {
    private int width;
    private int height;
    private char[][] tiles;

    public GameMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new char[height][width];
    }


    public void setTile(int x, int y, char tile) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            tiles[y][x] = tile;
        }
    }


    public boolean isWalkable(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return false;

        char tile = tiles[y][x];
        return tile == '.';
    }

    public void printMap() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(tiles[y][x]);
            }
            System.out.println();
        }
    }
}
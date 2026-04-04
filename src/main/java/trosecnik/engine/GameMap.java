package trosecnik.engine;
import java.util.HashMap;
import java.util.Map;

public class GameMap {
    private final int width;
    private  final int height;
    private  final char[][] tiles;
    private final Map<String, Integer> treeHealth = new HashMap<>();
    private final Map<String, Integer> pigHealth = new HashMap<>();

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
        return tile == '.' || tile == 'k' || tile == 'v';
    }

    public char getTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return tiles[y][x];
        }
        return ' ';
    }

    public void printMap() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(tiles[y][x]);
            }
            System.out.println();
        }
    }

    public boolean chopTree(int x, int y) {

        String key = x + "," + y;


        int health = treeHealth.getOrDefault(key, 3);
        health--;

        if (health <= 0) {
            setTile(x, y, '.');
            treeHealth.remove(key);
            return true;
        } else {
            treeHealth.put(key, health);
            System.out.println("jeste zbyva [" + key + "]: " + health);
            return false;
        }
    }
    public int getTreeHealth(int x, int y) {
        return treeHealth.getOrDefault(x + "," + y, 3);
    }

    public int getPigHealth(int x, int y) {
        return pigHealth.getOrDefault(x + "," + y, 50);
    }

    public boolean huntPig(int x, int y, int damage) {
        String key = x + "," + y;
        int health = pigHealth.getOrDefault(key, 50);
        health -= damage;

        if (health <= 0) {
            setTile(x, y, '.');
            pigHealth.remove(key);
            return true;
        } else {
            pigHealth.put(key, health);
            System.out.println("Kvík! Prase dostalo zásah, zbývá HP: " + health);
            return false;
        }
    }
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
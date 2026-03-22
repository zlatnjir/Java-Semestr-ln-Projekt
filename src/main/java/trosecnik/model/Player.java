package trosecnik.model;

import trosecnik.engine.GameMap;
import trosecnik.inventory.CraftingSystem;
import trosecnik.inventory.Inventory;


public class Player extends Entity {

    private int health = 100;
    private int hunger = 100;

    private Inventory inventory;
    private CraftingSystem craftingSystem;
    private GameMap gameMap;

    public Player(String name, int startX, int startY, GameMap map) {
        this.name = name;
        this.x = startX;
        this.y = startY;
        this.gameMap = map;
        this.inventory = new Inventory();
        this.craftingSystem = new CraftingSystem();
    }

    public void move(int dx, int dy) {
        int newX = this.x + dx;
        int newY = this.y + dy;

        if (gameMap != null && gameMap.isWalkable(newX, newY)) {
            this.x = newX;
            this.y = newY;
            System.out.println(name + "krok na [" + x + ", " + y + "]");
            decreaseHunger(1);
        } else {
            System.out.println(" " + name + " narazil do překážky na [" + newX + ", " + newY + "]");
        }
    }

    @Override
    public void interact() {
        char currentTile = gameMap.getTile(x, y);

        if (currentTile == 'k') {
            inventory.addItem(new trosecnik.inventory.Item("Kámen", "Surovina"));
            gameMap.setTile(x, y, '.');
            System.out.println("Sebral Kamen");

        } else if (currentTile == 'v') {
            inventory.addItem(new trosecnik.inventory.Item("Větve", "Surovina"));
            gameMap.setTile(x, y, '.');
            System.out.println("Sebral Vetve");
        } else {
            System.out.println("Tady na zemi nic zajímavého není.");
        }
    }
    public void decreaseHunger(int amount) {
        if (this.hunger > 0) {
            this.hunger -= amount;
            if (this.hunger < 0) {
                this.hunger = 0;
            }
        } else {
            this.health -= 5;
            System.out.println("Životy klesly na: " + this.health);

            if (this.health <= 0) {
                this.health = 0;
                System.out.println("konecna");
            }
        }
    }
    public CraftingSystem getCraftingSystem() { return craftingSystem; }
    public int getHealth() { return health; }
    public int getHunger() { return hunger; }
    public Inventory getInventory() { return inventory; }
}
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
            boolean actionDone = false;

            if (inventory.hasItemByName("Sekera")) {
                if (tryChop(x, y - 1) || tryChop(x, y + 1) || tryChop(x - 1, y) || tryChop(x + 1, y)) {
                    actionDone = true;
                }
            }

            if (!actionDone && inventory.hasItemByName("Oštěp")) {
                if (tryHunt(x, y - 1) || tryHunt(x, y + 1) || tryHunt(x - 1, y) || tryHunt(x + 1, y)) {
                    actionDone = true;
                }
            }

            if (!actionDone) {
                System.out.println("pod dubem za dubem nic neni test3");
            }
        }
    }
    private boolean tryChop(int targetX, int targetY) {
        if (gameMap.getTile(targetX, targetY) == 'T') {
            System.out.println("CHOPAAAAAAAAAAAAAAA!");
            boolean destroyed = gameMap.chopTree(targetX, targetY);

            if (destroyed) {
                System.out.println("sundal si strom wp");
                inventory.addItem(new trosecnik.inventory.Item("Dřevo", "Surovina"));
            }
            return true;
        }
        return false;
    }
    private boolean tryHunt(int targetX, int targetY) {
        if (gameMap.getTile(targetX, targetY) == 'p') {
            System.out.println("BOD! (Útočíš na prase...)");
            gameMap.setTile(targetX, targetY, '.'); // Prase z mapy zmizí po jedné ráně
            inventory.addItem(new trosecnik.inventory.Item("Syrové maso", "Jídlo"));
            System.out.println("Úspěšný lov! Získal jsi Syrové maso!");
            return true;
        }
        return false;
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
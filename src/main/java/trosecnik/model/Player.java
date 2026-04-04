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
            System.out.println("Sebral Kámen");

        } else if (currentTile == 'v') {
            inventory.addItem(new trosecnik.inventory.Item("Větve", "Surovina"));
            gameMap.setTile(x, y, '.');
            System.out.println("Sebral Větve");
        } else {
            System.out.println("Na zemi tady nic k sebrání není.");
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
    public void eatFood() {
        if (inventory.hasItemByName("Pečené maso")) {
            inventory.removeItemByName("Pečené maso");
            this.hunger += 50;
            if (this.hunger > 100) this.hunger = 100;
            System.out.println("Snedl si pecene maso +50 hunger");
        } else if (inventory.hasItemByName("Syrové maso")) {
            inventory.removeItemByName("Syrové maso");
            this.hunger += 15;
            if (this.hunger > 100) this.hunger = 100;
            System.out.println("Snedl si syrove maso +50 hunger");
        } else {
            System.out.println("si chudak nemas co jest");
        }
    }

    public CraftingSystem getCraftingSystem() { return craftingSystem; }
    public int getHealth() { return health; }
    public int getHunger() { return hunger; }
    public Inventory getInventory() { return inventory; }
    public void setX(int newX) { this.x = newX; }
    public void setY(int newY) { this.y = newY; }
    public void setHealth(int newHealth) { this.health = newHealth; }
    public void setHunger(int newHunger) { this.hunger = newHunger; }
}
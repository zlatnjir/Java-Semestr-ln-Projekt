package trosecnik.model;

import trosecnik.inventory.Inventory;


public class Player extends Entity {
    private trosecnik.inventory.CraftingSystem craftingSystem;
    private trosecnik.engine.GameMap gameMap;

    private int health = 100;
    private int hunger = 100;
    private Inventory inventory;

    public Player(String name) {
        this.name = name;
        this.inventory = new Inventory();
    }

    public void move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
        // TODO: odečítání hladu při pohybu hápek
    }

    @Override
    public void interact() {
        // TODO: Ztráta hp když s něčím hráč interaguhje
    }

    public int getHealth() { return health; }
    public int getHunger() { return hunger; }
}
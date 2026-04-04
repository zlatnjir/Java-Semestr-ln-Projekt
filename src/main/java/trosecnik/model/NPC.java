package trosecnik.model;

public class NPC extends Entity {
    private final String dialogueMessage;
    private int health;

    private final int homeX;
    private final int homeY;
    private boolean isAggroed;

    public NPC(String name, int startX, int startY, String dialogueMessage) {
        this.name = name;
        this.x = startX;
        this.y = startY;
        this.dialogueMessage = dialogueMessage;
        this.health = 50;

        this.homeX = startX;
        this.homeY = startY;
        this.isAggroed = false;
    }

    public String getDialogueMessage() { return dialogueMessage; }
    public String getName() { return name; }
    public int getHealth() { return health; }

    public int getHomeX() { return homeX; }
    public int getHomeY() { return homeY; }
    public boolean isAggroed() { return isAggroed; }
    public void setAggroed(boolean aggroed) { this.isAggroed = aggroed; }

    public void takeDamage(int amount) {
        this.health -= amount;
        if (this.health < 0) this.health = 0;
    }

    public void setX(int newX) { this.x = newX; }
    public void setY(int newY) { this.y = newY; }

    @Override
    public void interact() {
        System.out.println("--- DIALOG ---");
        System.out.println(name + ": \"" + dialogueMessage + "\"");
        System.out.println("--------------");
    }
}
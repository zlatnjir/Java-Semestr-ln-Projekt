package trosecnik.model;

public class NPC extends Entity {
    private String dialogueMessage;
    private boolean isHostile;

    public NPC(String name, int startX, int startY, String dialogueMessage, boolean isHostile) {
        this.name = name;
        this.x = startX;
        this.y = startY;
        this.dialogueMessage = dialogueMessage;
        this.isHostile = isHostile;
    }

    public String getDialogueMessage() {
        return dialogueMessage;
    }

    public boolean isHostile() {
        return isHostile;
    }

    public String getName() {
        return name;
    }

    @Override
    public void interact() {
        System.out.println("--- DIALOG ---");
        System.out.println(name + ": \"" + dialogueMessage + "\"");
        System.out.println("--------------");
    }
}
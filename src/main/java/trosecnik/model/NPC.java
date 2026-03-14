package trosecnik.model;


public class NPC extends Entity {
    private String dialogueMessage;
    private boolean isHostile;

    public NPC(String name, String dialogueMessage, boolean isHostile) {
        this.name = name;
        this.dialogueMessage = dialogueMessage;
        this.isHostile = isHostile;
    }

    @Override
    public void interact() {
        // TODO: interakce NPC a člověka
    }
}
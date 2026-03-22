package trosecnik.inventory;

public class CraftingSystem {


    public boolean craftAxe(Inventory inv) {
        if (inv.hasItemByName("Kámen") && inv.hasItemByName("Větve")) {
            inv.removeItemByName("Kámen");
            inv.removeItemByName("Větve");
            inv.addItem(new Item("Sekera", "Nástroj"));
            return true;
        }
        return false;
    }
}
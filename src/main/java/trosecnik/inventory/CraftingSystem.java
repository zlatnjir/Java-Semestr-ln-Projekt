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
    public boolean craftSpear(Inventory inv) {
        if (inv.hasItemByName("Dřevo") && inv.hasItemByName("Kámen")) {
            inv.removeItemByName("Dřevo");
            inv.removeItemByName("Kámen");
            inv.addItem(new Item("Oštěp", "Nástroj"));
            return true;
        }
        return false;
    }
}
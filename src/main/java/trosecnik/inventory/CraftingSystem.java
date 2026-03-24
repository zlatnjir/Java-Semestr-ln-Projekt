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
    
    public boolean craftSplinters(Inventory inv) {
        if (inv.hasItemByName("Dřevo")) {
            inv.removeItemByName("Dřevo");
            inv.addItem(new Item("Tříska", "Surovina"));
            inv.addItem(new Item("Tříska", "Surovina"));
            inv.addItem(new Item("Tříska", "Surovina"));
            inv.addItem(new Item("Tříska", "Surovina"));
            return true;
        }
        return false;
    }
    public boolean craftFire(Inventory inv) {
        if (inv.hasItemByName("Tříska") && inv.hasItemByName("Kámen")) {
            inv.removeItemByName("Tříska");
            inv.removeItemByName("Kámen");
            inv.addItem(new Item("Oheň", "Nástroj"));
            return true;
        }
        return false;
    }

    public boolean craftCookedMeat(Inventory inv) {
        if (inv.hasItemByName("Oheň") && inv.hasItemByName("Syrové maso")) {
            inv.removeItemByName("Oheň");
            inv.removeItemByName("Syrové maso");
            inv.addItem(new Item("Pečené maso", "Jídlo"));
            return true;
        }
        return false;
    }
}
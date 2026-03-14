package trosecnik.inventory;

public class InventoryTest {
    public static void main(String[] args) {

        Inventory batoh = new Inventory();

        Item kamen = new Item("Kámen", "RESOURCE");
        Item drevo = new Item("Dřevo", "RESOURCE");

        Item druhyKamen = new Item("Kámen", "RESOURCE");

        batoh.addItem(kamen);
        batoh.addItem(drevo);

        System.out.println("Mám v batohu Kámen? " + batoh.hasItem(druhyKamen));

        batoh.removeItem(drevo);

        System.out.println("Aktuální počet věcí v batohu: " + batoh.getItems().size());

    }
}
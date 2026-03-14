package trosecnik.inventory;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<Item> items;

    public Inventory() {
        this.items = new ArrayList<>();
    }


    public void addItem(Item item) {
        if (item != null) {
            items.add(item);
            System.out.println("Do inventáře přidáno: " + item.toString());
        }
    }

    public void removeItem(Item item) {
        if (items.remove(item)) {
            System.out.println("Z inventáře odebráno: " + item.toString());
        }
    }

    public boolean hasItem(Item item) {
        return items.contains(item);
    }

    public List<Item> getItems() {


        return items;
    }
}
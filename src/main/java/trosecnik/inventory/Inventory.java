package trosecnik.inventory;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private final List<Item> items;

    public Inventory() {
        this.items = new ArrayList<>();

    }
    public boolean hasItemByName(String name) {
        for (Item item : items) {
            if (item.name().equals(name)) return true;
        }
        return false;
    }

    public void removeItemByName(String name) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).name().equals(name)) {
                items.remove(i);
                break;
            }
        }
    }
    public void addItem(Item item) {
        if (item != null) {
            items.add(item);
            System.out.println("Do inventáře přidáno: " + item);
        }
    }

    public void removeItem(Item item) {
        if (items.remove(item)) {
            System.out.println("Z inventáře odebráno: " + item);
        }
    }

    public boolean hasItem(Item item) {
        return items.contains(item);
    }

    public List<Item> getItems() {


        return items;
    }

}
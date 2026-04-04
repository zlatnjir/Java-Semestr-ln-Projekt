package trosecnik.inventory;

public record Item(String name, String type) {

    @Override
    public String toString() {
        return name + " [" + type + "]";
    }
}
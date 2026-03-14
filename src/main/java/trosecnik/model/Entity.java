package trosecnik.model;

public abstract class Entity {

    protected int x;
    protected int y;
    protected String name;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public abstract void interact();
}
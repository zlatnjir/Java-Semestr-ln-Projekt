package trosecnik.engine;

public class MapTest {
    public static void main(String[] args) {
        SaveLoadManager slManager = new SaveLoadManager();

        GameMap mapa = slManager.loadLevel("/trosecnik/levels/level1.txt");

        if (mapa != null) {
            System.out.println("\nVykresluji načtenou mapu:");
            mapa.printMap();

            System.out.println("\nMůžu jít na 0,0 (Voda)? " + mapa.isWalkable(0, 0));
            System.out.println("Můžu jít na 3,1 (Tráva)? " + mapa.isWalkable(3, 1));
        }
    }
}
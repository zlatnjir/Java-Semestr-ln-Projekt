package trosecnik.model;

import trosecnik.engine.GameMap;
import trosecnik.engine.SaveLoadManager;

public class PlayerTest {
    public static void main(String[] args) {

        SaveLoadManager slManager = new SaveLoadManager();

        GameMap mapa = slManager.loadLevel("/trosecnik/levels/level1.txt");

        if (mapa != null) {

            Player hrdina = new Player("Trosečník", 2, 1, mapa);
            System.out.println("Startovní pozice: [" + hrdina.getX() + ", " + hrdina.getY() + "]\n");



            System.out.println("doprava (volno):");
            hrdina.move(1, 0);


            System.out.println("\nnahoru (voda):");
            hrdina.move(0, -1);

            System.out.println("\nZkouším jít dolů (volno):");
            hrdina.move(0, 1);
        }


    }
}
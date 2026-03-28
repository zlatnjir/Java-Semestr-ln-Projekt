package trosecnik.engine;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SaveLoadManager {

    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(SaveLoadManager.class.getName());

    public GameMap loadLevel(String levelFileName) {
        try {
            InputStream is = getClass().getResourceAsStream(levelFileName);
            if (is == null) {
                LOGGER.severe("Soubor s mapou nenalezen: " + levelFileName);
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            List<String> lines = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();

            int height = lines.size();
            int width = lines.isEmpty() ? 0 : lines.get(0).length();

            GameMap map = new GameMap(width, height);

            for (int y = 0; y < height; y++) {
                String currentLine = lines.get(y);
                for (int x = 0; x < currentLine.length() && x < width; x++) {
                    map.setTile(x, y, currentLine.charAt(x));
                }
            }

            LOGGER.info("Mapa " + levelFileName + " úspěšně načtena! Rozměry: " + width + "x" + height);
            return map;

        } catch (Exception e) {
            LOGGER.severe("Chyba při načítání mapy: " + e.getMessage());
            return null;
        }
    }

    public void saveGame(String fileName, trosecnik.model.Player player) {
        try {
            java.io.PrintWriter writer = new java.io.PrintWriter(fileName);

            writer.println(player.getX());
            writer.println(player.getY());
            writer.println(player.getHealth());
            writer.println(player.getHunger());

            for (trosecnik.inventory.Item item : player.getInventory().getItems()) {
                writer.println(item.getName() + ";" + item.getType());
            }

            writer.close();
            System.out.println("Paráda! Hra byla úspěšně uložena do souboru: " + fileName);

        } catch (Exception e) {
            System.out.println("Jejda, chyba při ukládání: " + e.getMessage());
        }
    }
}
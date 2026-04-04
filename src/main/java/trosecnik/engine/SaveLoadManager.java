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
            int width = lines.isEmpty() ? 0 : lines.getFirst().length();

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

    public void saveGame(String fileName, trosecnik.model.Player player, GameMap map) {
        try {
            java.io.PrintWriter writer = new java.io.PrintWriter(fileName);

            writer.println(player.getX());
            writer.println(player.getY());
            writer.println(player.getHealth());
            writer.println(player.getHunger());
            for (trosecnik.inventory.Item item : player.getInventory().getItems()) {
                writer.println(item.name() + ";" + item.type());
            }

            writer.println("---MAPA---");
            writer.println(map.getWidth());
            writer.println(map.getHeight());

            for (int y = 0; y < map.getHeight(); y++) {
                StringBuilder row = new StringBuilder();
                for (int x = 0; x < map.getWidth(); x++) {
                    row.append(map.getTile(x, y));
                }
                writer.println(row);
            }

            writer.close();
            System.out.println("Paráda! Hra byla úspěšně uložena včetně stavu světa!");

        } catch (Exception e) {
            System.out.println("Jejda, chyba při ukládání: " + e.getMessage());
        }
    }

    public boolean loadGameState(String fileName, trosecnik.model.Player player, GameMap map) {
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(fileName));

            player.setX(Integer.parseInt(reader.readLine()));
            player.setY(Integer.parseInt(reader.readLine()));
            player.setHealth(Integer.parseInt(reader.readLine()));
            player.setHunger(Integer.parseInt(reader.readLine()));
            player.getInventory().getItems().clear();

            String line;
            boolean readingMap = false;
            int mapY = 0;

            while ((line = reader.readLine()) != null) {
                if (line.equals("---MAPA---")) {
                    readingMap = true;
                    reader.readLine();
                    reader.readLine();
                    continue;
                }

                if (readingMap) {
                    for (int x = 0; x < line.length() && x < map.getWidth(); x++) {
                        map.setTile(x, mapY, line.charAt(x));
                    }
                    mapY++;
                } else {
                    String[] parts = line.split(";");
                    if (parts.length == 2) {
                        player.getInventory().addItem(new trosecnik.inventory.Item(parts[0], parts[1]));
                    }
                }
            }

            reader.close();
            System.out.println("Bomba! Hra byla úspěšně načtena i s vyteženým světem!");
            return true;

        } catch (Exception e) {
            System.out.println("Jejda, nenašel jsem uloženou hru: " + e.getMessage());
            return false;
        }
    }
}
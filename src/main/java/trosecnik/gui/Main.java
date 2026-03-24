package trosecnik.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import trosecnik.engine.GameMap;
import trosecnik.engine.SaveLoadManager;
import trosecnik.engine.TimeThread;
import trosecnik.model.Player;

public class Main extends Application {

    private static final int TILE_SIZE = 80;

    private GameMap gameMap;
    private Player player;
    private SaveLoadManager saveLoadManager;
    private TimeThread timeThread;
    private boolean showInventory = false;

    @Override
    public void start(Stage primaryStage) {
        saveLoadManager = new SaveLoadManager();
        gameMap = saveLoadManager.loadLevel("/trosecnik/levels/level1.txt");

        if (gameMap != null) {
            player = new Player("Trosečník", 2, 1, gameMap);
        } else {
            System.out.println("Kritická chyba: Mapa se nenačetla!");
            return;
        }

        Canvas canvas = new Canvas(10 * TILE_SIZE, 8 * TILE_SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        drawGame(gc);

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root);


        scene.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.C) {
                showInventory = !showInventory;
                drawGame(gc);
                return;
            }

            if (showInventory) {
                if (event.getCode() == javafx.scene.input.KeyCode.F) {
                    boolean success = player.getCraftingSystem().craftAxe(player.getInventory());
                    if (success) {
                        System.out.println("Úspěšně jsi vyrobil Sekeru!");
                    } else {
                        System.out.println("Nemáš dost surovin na Sekeru (Kámen + Větve).");
                    }
                    drawGame(gc);
                }
                if (event.getCode() == javafx.scene.input.KeyCode.G) {
                    boolean success = player.getCraftingSystem().craftSpear(player.getInventory());
                    if (success) {
                        System.out.println("Úspěšně jsi vyrobil Oštěp!");
                    } else {
                        System.out.println("Nemáš dost surovin na Oštěp (Dřevo + Kámen).");
                    }
                    drawGame(gc);
                }
                if (event.getCode() == javafx.scene.input.KeyCode.T) {
                    if (player.getCraftingSystem().craftSplinters(player.getInventory())) {
                        System.out.println("Rozštípl jsi drevo 4 třísky");
                    }
                    drawGame(gc);
                }
                if (event.getCode() == javafx.scene.input.KeyCode.O) {
                    if (player.getCraftingSystem().craftFire(player.getInventory())) {
                        System.out.println("ty žháři pomalu!");
                    }
                    drawGame(gc);
                }
                if (event.getCode() == javafx.scene.input.KeyCode.P) {
                    if (player.getCraftingSystem().craftCookedMeat(player.getInventory())) {
                        System.out.println("uepkl si maso wow! ");
                    }
                    drawGame(gc);
                }
                return;

            }

            switch (event.getCode()) {
                case W:
                case UP:
                    player.move(0, -1);
                    break;
                case S:
                case DOWN:
                    player.move(0, 1);
                    break;
                case A:
                case LEFT:
                    player.move(-1, 0);
                    break;
                case D:
                case RIGHT:
                    player.move(1, 0);
                    break;
                case E:
                    player.interact();
                    break;
                case R:
                    player.eatFood();
                    break;
            }
            drawGame(gc);
        });


        primaryStage.setTitle("Trosecnik");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        timeThread = new TimeThread(player, () -> drawGame(gc));
        timeThread.start();
    }

    private void drawGame(GraphicsContext gc) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 10; x++) {
                char tile = gameMap.getTile(x, y);

                if (tile == '~') {
                    gc.setFill(Color.BLUE);
                    gc.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                } else if (tile == '.') {
                    gc.setFill(Color.LIGHTGREEN);
                    gc.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                } else if (tile == 'T') {
                    gc.setFill(Color.DARKGREEN);
                    gc.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                } else if (tile == 'R') {
                    gc.setFill(Color.GRAY);
                    gc.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                } else if (tile == 'k') {
                    gc.setFill(Color.LIGHTGREEN);
                    gc.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    gc.setFill(Color.DARKGRAY);
                    gc.fillOval(x * TILE_SIZE + 20, y * TILE_SIZE + 20, 40, 40); // Menší šedý kruh
                } else if (tile == 'v') {
                    gc.setFill(Color.LIGHTGREEN);
                    gc.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    gc.setFill(Color.SADDLEBROWN);
                    gc.fillRect(x * TILE_SIZE + 30, y * TILE_SIZE + 10, 20, 60); // Hnědý obdélníček
                }
                else if (tile == 'p') {
                    gc.setFill(Color.LIGHTGREEN);
                    gc.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    gc.setFill(Color.PINK);
                    gc.fillRoundRect(x * TILE_SIZE + 10, y * TILE_SIZE + 20, 60, 40, 15, 15);
                }


            }
        }

        gc.setFill(Color.RED);
        gc.fillOval(player.getX() * TILE_SIZE, player.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 24));

        gc.fillText("Životy: " + player.getHealth(), 20, 35);



        if (player.getHunger() > 30) {
            gc.setFill(Color.WHITE);
        } else {
            gc.setFill(Color.ORANGE);
        }
        gc.fillText("Hlad: " + player.getHunger(), 20, 65);

        // --- VYKRESLENÍ INVENTÁŘE (Pokud je zapnutý) ---
        if (showInventory) {
            // Větší poloprůhledné černé pozadí přes mapu
            gc.setFill(Color.rgb(0, 0, 0, 0.8));
            gc.fillRect(40, 40, 720, 500);

            // Nadpisy sloupečků
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Arial", 28));
            gc.fillText("--- INVENTÁŘ ---", 70, 90);
            gc.fillText("--- RECEPTY ---", 400, 90);

            // Levý sloupeček: Získáme seznam věcí z modelu
            java.util.List<trosecnik.inventory.Item> items = player.getInventory().getItems();

            gc.setFont(javafx.scene.text.Font.font("Arial", 20));
            if (items.isEmpty()) {
                gc.fillText("Batoh je prázdný.", 70, 140);
            } else {
                int yOffset = 140; // Výška prvního řádku pro předměty
                for (trosecnik.inventory.Item item : items) {
                    gc.fillText("- " + item.getName() + " (" + item.getType() + ")", 70, yOffset);
                    yOffset += 30; // Další položka bude o kousek níž
                }
            }

            // Pravý sloupeček: Nápověda pro crafting
            gc.setFill(Color.YELLOW);
            gc.fillText("[F] Vyrobit Sekeru (Kámen + Větve)", 400, 140);
            gc.fillText("[G] Vyrobit Oštěp (Dřevo + Kámen)", 400, 170);
            gc.fillText("[T] Rozštípat Dřevo -> 4x Tříska", 400, 200);
            gc.fillText("[O] Vyrobit Oheň (Tříska + Kámen)", 400, 230);
            gc.fillText("[P] Upéct maso (Oheň + Syrové maso)", 400, 260);
        }
    }
    @Override
    public void stop() {
        if (timeThread != null) {
            timeThread.stopTime();
            System.out.println("ende");
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
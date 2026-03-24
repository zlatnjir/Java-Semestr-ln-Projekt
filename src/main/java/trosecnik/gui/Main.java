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

        if (showInventory) {

            gc.setFill(Color.rgb(0, 0, 0, 0.7));
            gc.fillRect(100, 50, 600, 220);


            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Arial", 28));
            gc.fillText("--- INVENTÁŘ ---", 120, 90);

            java.util.List<trosecnik.inventory.Item> items = player.getInventory().getItems();

            gc.setFont(javafx.scene.text.Font.font("Arial", 20));
            gc.setFill(Color.YELLOW);
            gc.fillText("[F] Vyrobit Sekeru (Kámen + Větve)", 120, 240);
            gc.fillText("[G] Vyrobit Oštěp (Dřevo + Kámen)", 120, 270);
            if (items.isEmpty()) {
                gc.fillText("Batoh je prázdný.", 120, 140);
            } else {
                int yOffset = 140;
                for (trosecnik.inventory.Item item : items) {
                    gc.fillText("- " + item.getName() + " (" + item.getType() + ")", 120, yOffset);
                    yOffset += 30;
                }
            }
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
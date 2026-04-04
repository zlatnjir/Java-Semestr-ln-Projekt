package trosecnik.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import trosecnik.engine.GameMap;
import trosecnik.engine.SaveLoadManager;
import trosecnik.engine.TimeThread;
import trosecnik.model.Player;

import java.util.List;

public class App extends Application {

    private static final int TILE_SIZE = 80;

    private GameMap gameMap;
    private Player player;
    private SaveLoadManager saveLoadManager;
    private TimeThread timeThread;
    private boolean showInventory = false;
    private boolean isPaused = false;
    private trosecnik.model.NPC domorodec;
    private trosecnik.model.NPC divocak;
    private long lastBoarMoveTime = 0;
    private long lastBoarAttackTime = 0;
    private String fullDialogue = null;
    private int visibleChars = 0;
    private long lastTypingTick = 0;
    private javafx.animation.AnimationTimer typingTimer = null;
    private trosecnik.inventory.Item craftSlot1 = null;
    private trosecnik.inventory.Item craftSlot2 = null;
    private int activeHotbarSlot = 0;


    @Override
    public void start(Stage primaryStage) {
        saveLoadManager = new SaveLoadManager();
        gameMap = saveLoadManager.loadLevel("/trosecnik/levels/level1.txt");

        if (gameMap != null) {
            player = new Player("Trosečník", 2, 1, gameMap);
            domorodec = new trosecnik.model.NPC("Pátek", 8, 2, "Cizinče! Voda je zrádná. Najdi Liány a Dřevo, postav Vor a uteč!");
            divocak = new trosecnik.model.NPC("Divoké prase", 2, 6, "");
        } else {
            System.out.println("Kritická chyba: Mapa se nenačetla!");
            return;
        }

        Canvas canvas = new Canvas(10 * TILE_SIZE, 8 * TILE_SIZE);
        final GraphicsContext gc = canvas.getGraphicsContext2D();

        drawGame(gc);

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root);

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                if (fullDialogue != null) {
                    fullDialogue = null;
                    if (typingTimer != null) typingTimer.stop();
                    drawGame(gc);
                    return;
                }
                isPaused = !isPaused;
                showInventory = false;
                if (timeThread != null) timeThread.setPaused(isPaused);
                drawGame(gc);
                return;
            }

            if (isPaused) return;

            if (event.getCode() == javafx.scene.input.KeyCode.C) {
                showInventory = !showInventory;
                drawGame(gc);
                return;
            }

            if (!showInventory) {
                switch (event.getCode()) {
                    case DIGIT1, NUMPAD1 -> { activeHotbarSlot = 0; drawGame(gc); return; }
                    case DIGIT2, NUMPAD2 -> { activeHotbarSlot = 1; drawGame(gc); return; }
                    case DIGIT3, NUMPAD3 -> { activeHotbarSlot = 2; drawGame(gc); return; }
                    case DIGIT4, NUMPAD4 -> { activeHotbarSlot = 3; drawGame(gc); return; }
                    case DIGIT5, NUMPAD5 -> { activeHotbarSlot = 4; drawGame(gc); return; }
                }
            }

            if (showInventory) {
                switch (event.getCode()) {
                    case F -> {
                        if (player.getCraftingSystem().craftAxe(player.getInventory())) System.out.println("Úspěšně jsi vyrobil Sekeru!");
                        else System.out.println("Nemáš dost surovin na Sekeru (Kámen + Větve).");
                        drawGame(gc);
                    }
                    case G -> {
                        if (player.getCraftingSystem().craftSpear(player.getInventory())) System.out.println("Úspěšně jsi vyrobil Oštěp!");
                        else System.out.println("Nemáš dost surovin na Oštěp (Dřevo + Kámen).");
                        drawGame(gc);
                    }
                    case T -> {
                        if (player.getCraftingSystem().craftSplinters(player.getInventory())) System.out.println("Rozštípl jsi drevo 4 třísky");
                        drawGame(gc);
                    }
                    case O -> {
                        if (player.getCraftingSystem().craftFire(player.getInventory())) System.out.println("ty žháři pomalu!");
                        drawGame(gc);
                    }
                    case P -> {
                        if (player.getCraftingSystem().craftCookedMeat(player.getInventory())) System.out.println("uepkl si maso wow! ");
                        drawGame(gc);
                    }
                }
                return;
            }

            int dx = 0;
            int dy = 0;

            if (event.getCode() == javafx.scene.input.KeyCode.W || event.getCode() == javafx.scene.input.KeyCode.UP) dy = -1;
            else if (event.getCode() == javafx.scene.input.KeyCode.S || event.getCode() == javafx.scene.input.KeyCode.DOWN) dy = 1;
            else if (event.getCode() == javafx.scene.input.KeyCode.A || event.getCode() == javafx.scene.input.KeyCode.LEFT) dx = -1;
            else if (event.getCode() == javafx.scene.input.KeyCode.D || event.getCode() == javafx.scene.input.KeyCode.RIGHT) dx = 1;

            if (dx != 0 || dy != 0) {
                int targetX = player.getX() + dx;
                int targetY = player.getY() + dy;

                if (domorodec != null && targetX == domorodec.getX() && targetY == domorodec.getY()) {
                    System.out.println("Bum! Narazil jsi do " + domorodec.getName());
                }
                else if (divocak != null && divocak.getHealth() > 0 && targetX == divocak.getX() && targetY == divocak.getY()) {
                    System.out.println("Narazil jsi do divočáka! K útoku použij myš!");
                }
                else {
                    player.move(dx, dy);
                }

                if (fullDialogue != null) {
                    fullDialogue = null;
                    if (typingTimer != null) typingTimer.stop();
                }
                drawGame(gc);
            }
            else if (event.getCode() == javafx.scene.input.KeyCode.E) {
                List<trosecnik.inventory.Item> items = player.getInventory().getItems();
                trosecnik.inventory.Item activeItem = null;
                if (activeHotbarSlot < items.size()) {
                    activeItem = items.get(activeHotbarSlot);
                }

                if (activeItem != null && activeItem.type().equals("Jídlo")) {
                    if (activeItem.name().equals("Pečené maso")) {
                        player.setHunger(player.getHunger() + 50);
                        System.out.println("Mňam! Snědl jsi Pečené maso (+50 Hlad).");
                    } else if (activeItem.name().equals("Syrové maso")) {
                        player.setHunger(player.getHunger() + 15);
                        System.out.println("Fuj! Snědl jsi Syrové maso (+15 Hlad).");
                    }
                    if (player.getHunger() > 100) player.setHunger(100);
                    player.getInventory().removeItem(activeItem);
                }
                else {
                    player.interact();

                    if (domorodec != null && Math.abs(player.getX() - domorodec.getX()) <= 1 && Math.abs(player.getY() - domorodec.getY()) <= 1) {
                        fullDialogue = domorodec.getName() + ": " + domorodec.getDialogueMessage();
                        visibleChars = 0;
                        lastTypingTick = 0;
                        if (typingTimer != null) typingTimer.stop();

                        typingTimer = new javafx.animation.AnimationTimer() {
                            @Override
                            public void handle(long now) {
                                if (fullDialogue != null && visibleChars < fullDialogue.length()) {
                                    if (now - lastTypingTick > 50_000_000) {
                                        visibleChars++;
                                        lastTypingTick = now;
                                        drawGame(gc);
                                    }
                                } else {
                                    this.stop();
                                }
                            }
                        };
                        typingTimer.start();
                    }
                }
            }
        });

        scene.setOnMouseClicked(event -> {
            if (isPaused) {
                double px = event.getX();
                double py = event.getY();

                if (px >= 300 && px <= 500 && py >= 400 && py <= 450) {
                    javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
                    fileChooser.setTitle("Vyber, kam chceš hru uložit");
                    fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Textové soubory (*.txt)", "*.txt"));
                    java.io.File file = fileChooser.showSaveDialog(primaryStage);

                    if (file != null) {
                        saveLoadManager.saveGame(file.getAbsolutePath(), player, gameMap);
                        isPaused = false;
                        if (timeThread != null) timeThread.setPaused(false);
                    }
                }
                else if (px >= 300 && px <= 500 && py >= 470 && py <= 520) {
                    javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
                    fileChooser.setTitle("Vyber uloženou hru k načtení");
                    fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Textové soubory (*.txt)", "*.txt"));
                    java.io.File file = fileChooser.showOpenDialog(primaryStage);

                    if (file != null) {
                        if (saveLoadManager.loadGameState(file.getAbsolutePath(), player, gameMap)) {
                            isPaused = false;
                            if (timeThread != null) timeThread.setPaused(false);
                        }
                    }
                }
                return;
            }

            int gridX = (int) (event.getX() / TILE_SIZE);
            int gridY = (int) (event.getY() / TILE_SIZE);

            if (!showInventory && gridX >= 0 && gridX < 10 && gridY >= 0 && gridY < 8) {
                double distance = Math.hypot(player.getX() - gridX, player.getY() - gridY);
                List<trosecnik.inventory.Item> items = player.getInventory().getItems();
                String activeItemName = (activeHotbarSlot < items.size()) ? items.get(activeHotbarSlot).name() : "Ruce";

                if (divocak != null && divocak.getHealth() > 0 && gridX == divocak.getX() && gridY == divocak.getY()) {
                    double range = getWeaponRange(activeItemName);
                    int dmg = getWeaponDamage(activeItemName);

                    if (distance <= range) {
                        divocak.takeDamage(dmg);
                        System.out.println("Zásah zbraní '" + activeItemName + "' za " + dmg + " DMG!");
                        if (divocak.getHealth() <= 0) {
                            System.out.println("Prase padlo!");
                            gameMap.setTile(gridX, gridY, 'p');
                        }
                    } else {
                        System.out.println("Prase je moc daleko!");
                    }
                } else {
                    char clickedTile = gameMap.getTile(gridX, gridY);
                    if (clickedTile == 'T') {
                        if (activeItemName.equals("Sekera") && distance <= 1.5) {
                            if (gameMap.chopTree(gridX, gridY)) {
                                System.out.println("Strom padl! Získal jsi Dřevo.");
                                player.getInventory().addItem(new trosecnik.inventory.Item("Dřevo", "Surovina"));
                            }
                        } else if (!activeItemName.equals("Sekera")) {
                            System.out.println("Na strom potřebuješ Sekeru!");
                        } else {
                            System.out.println("Jsi moc daleko od stromu!");
                        }
                    } else if (clickedTile == 'p') {
                        double range = activeItemName.equals("Oštěp") ? 2.5 : 1.5;
                        int dmg = activeItemName.equals("Oštěp") ? 35 : (activeItemName.equals("Sekera") ? 20 : 10);

                        if (distance <= range) {
                            if (gameMap.huntPig(gridX, gridY, dmg)) {
                                System.out.println("Úspěšný lov! Získal jsi Syrové maso.");
                                player.getInventory().addItem(new trosecnik.inventory.Item("Syrové maso", "Jídlo"));
                            }
                        } else {
                            System.out.println("Prase je moc daleko!");
                        }
                    }
                }
                drawGame(gc);
                return;
            }

            if (showInventory) {
                double x = event.getX();
                double y = event.getY();

                if (x >= 70 && x <= 340 && y >= 120 && y <= 390) {
                    int col = (int) ((x - 70) / 70);
                    int row = (int) ((y - 120) / 70);
                    int index = row * 4 + col;

                    List<trosecnik.inventory.Item> items = player.getInventory().getItems();
                    if (index < items.size()) {
                        trosecnik.inventory.Item clickedItem = items.get(index);

                        if (event.getButton() == javafx.scene.input.MouseButton.SECONDARY && clickedItem.type().equals("Jídlo")) {
                            player.eatFood();
                        } else if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                            if (craftSlot1 == null) {
                                craftSlot1 = clickedItem;
                            } else if (craftSlot2 == null && clickedItem != craftSlot1) {
                                craftSlot2 = clickedItem;
                            } else {
                                craftSlot1 = clickedItem;
                                craftSlot2 = null;
                            }
                        }
                    }
                }

                if (x >= 400 && x <= 610 && y >= 260 && y <= 310) {
                    boolean success = false;
                    if (craftSlot1 != null && craftSlot2 != null) {
                        String s1 = craftSlot1.name();
                        String s2 = craftSlot2.name();

                        if ((s1.equals("Kámen") && s2.equals("Větve")) || (s2.equals("Kámen") && s1.equals("Větve"))) {
                            success = player.getCraftingSystem().craftAxe(player.getInventory());
                        } else if ((s1.equals("Dřevo") && s2.equals("Kámen")) || (s2.equals("Dřevo") && s1.equals("Kámen"))) {
                            success = player.getCraftingSystem().craftSpear(player.getInventory());
                        } else if ((s1.equals("Tříska") && s2.equals("Kámen")) || (s2.equals("Tříska") && s1.equals("Kámen"))) {
                            success = player.getCraftingSystem().craftFire(player.getInventory());
                        } else if ((s1.equals("Oheň") && s2.equals("Syrové maso")) || (s2.equals("Oheň") && s1.equals("Syrové maso"))) {
                            success = player.getCraftingSystem().craftCookedMeat(player.getInventory());
                        }
                    } else if (craftSlot1 != null && craftSlot1.name().equals("Dřevo")) {
                        success = player.getCraftingSystem().craftSplinters(player.getInventory());
                    }

                    if (success) {
                        System.out.println("Výroba úspěšná!");
                    } else {
                        System.out.println("Z těchto surovin se nic vyrobit nedá.");
                    }
                    craftSlot1 = null;
                    craftSlot2 = null;
                }
                drawGame(gc);
            }
        });

        var gameLoopTimer = new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isPaused && divocak != null && divocak.getHealth() > 0) {
                    int pDx = player.getX() - divocak.getX();
                    int pDy = player.getY() - divocak.getY();
                    int distance = Math.abs(pDx) + Math.abs(pDy);

                    if (distance == 1) {
                        if (now - lastBoarAttackTime > 2_000_000_000L) {
                            player.setHealth(player.getHealth() - 15);
                            System.out.println("AU! Prase tě kouslo za 15 HP! Zbývá ti: " + player.getHealth());
                            lastBoarAttackTime = now;
                            drawGame(gc);
                        }
                    } else {
                        if (now - lastBoarMoveTime > 1_000_000_000L) {
                            boolean canSeePlayer = hasLineOfSight(divocak.getX(), divocak.getY(), player.getX(), player.getY());

                            if (divocak.isAggroed() && (distance >= 5 || (!canSeePlayer && distance > 1))) {
                                divocak.setAggroed(false);
                            } else if (!divocak.isAggroed() && distance <= 3 && canSeePlayer) {
                                divocak.setAggroed(true);
                            }

                            int targetDivX = divocak.getX();
                            int targetDivY = divocak.getY();
                            boolean moved = false;

                            if (divocak.isAggroed()) {
                                int[] newPos = calculateAIPath(targetDivX, targetDivY, pDx, pDy);
                                if (newPos[0] != targetDivX || newPos[1] != targetDivY) {
                                    targetDivX = newPos[0]; targetDivY = newPos[1]; moved = true;
                                }
                            } else {
                                int hDx = divocak.getHomeX() - divocak.getX();
                                int hDy = divocak.getHomeY() - divocak.getY();
                                if (Math.abs(hDx) + Math.abs(hDy) > 2) {
                                    int[] newPos = calculateAIPath(targetDivX, targetDivY, hDx, hDy);
                                    if (newPos[0] != targetDivX || newPos[1] != targetDivY) {
                                        targetDivX = newPos[0]; targetDivY = newPos[1]; moved = true;
                                    }
                                } else {
                                    if (Math.random() < 0.3) {
                                        int[] dirs = {-1, 0, 1};
                                        int rMoveX = dirs[(int)(Math.random() * 3)];
                                        int rMoveY = (rMoveX == 0) ? dirs[(int)(Math.random() * 3)] : 0;
                                        if (isValidMove(targetDivX + rMoveX, targetDivY + rMoveY)) {
                                            targetDivX += rMoveX; targetDivY += rMoveY; moved = true;
                                        }
                                    }
                                }
                            }

                            if (moved) {
                                divocak.setX(targetDivX);
                                divocak.setY(targetDivY);
                                drawGame(gc);
                            }
                            lastBoarMoveTime = now;
                        }
                    }
                }
            }
        };
        gameLoopTimer.start();

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

                    int hp = gameMap.getTreeHealth(x, y);
                    if (hp < 3) {
                        gc.setFill(Color.RED);
                        gc.fillRect(x * TILE_SIZE + 15, y * TILE_SIZE + 5, 50, 5);
                        gc.setFill(Color.LIGHTGREEN);
                        gc.fillRect(x * TILE_SIZE + 15, y * TILE_SIZE + 5, (hp / 3.0) * 50, 5);
                    }
                } else if (tile == 'R') {
                    gc.setFill(Color.GRAY);
                    gc.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                } else if (tile == 'k') {
                    gc.setFill(Color.LIGHTGREEN);
                    gc.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    gc.setFill(Color.DARKGRAY);
                    gc.fillOval(x * TILE_SIZE + 20, y * TILE_SIZE + 20, 40, 40);
                } else if (tile == 'v') {
                    gc.setFill(Color.LIGHTGREEN);
                    gc.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    gc.setFill(Color.SADDLEBROWN);
                    gc.fillRect(x * TILE_SIZE + 30, y * TILE_SIZE + 10, 20, 60);
                } else if (tile == 'p') {
                    gc.setFill(Color.LIGHTGREEN);
                    gc.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    gc.setFill(Color.PINK);
                    gc.fillRoundRect(x * TILE_SIZE + 10, y * TILE_SIZE + 20, 60, 40, 15, 15);

                    int hp = gameMap.getPigHealth(x, y);
                    if (hp < 50) {
                        gc.setFill(Color.RED);
                        gc.fillRect(x * TILE_SIZE + 15, y * TILE_SIZE + 5, 50, 5);
                        gc.setFill(Color.LIGHTGREEN);
                        gc.fillRect(x * TILE_SIZE + 15, y * TILE_SIZE + 5, (hp / 50.0) * 50, 5);
                    }
                }
            }
        }

        gc.setFill(Color.RED);
        gc.fillOval(player.getX() * TILE_SIZE, player.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        if (domorodec != null) {
            gc.setFill(Color.YELLOW);
            gc.fillOval(domorodec.getX() * TILE_SIZE, domorodec.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Arial", 14));
            gc.fillText(domorodec.getName(), domorodec.getX() * TILE_SIZE + 20, domorodec.getY() * TILE_SIZE - 5);
        }

        if (divocak != null && divocak.getHealth() > 0) {
            gc.setFill(Color.PURPLE);
            gc.fillRoundRect(divocak.getX() * TILE_SIZE + 10, divocak.getY() * TILE_SIZE + 20, 60, 40, 20, 20);

            gc.setFill(Color.RED);
            gc.fillRect(divocak.getX() * TILE_SIZE + 15, divocak.getY() * TILE_SIZE + 5, 50, 5);
            gc.setFill(Color.LIGHTGREEN);
            gc.fillRect(divocak.getX() * TILE_SIZE + 15, divocak.getY() * TILE_SIZE + 5, divocak.getHealth(), 5);
        }

        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 24));
        gc.fillText("Životy: " + player.getHealth(), 20, 35);

        gc.setFill(player.getHunger() > 30 ? Color.WHITE : Color.ORANGE);
        gc.fillText("Hlad: " + player.getHunger(), 20, 65);

        if (showInventory) {
            gc.setFill(Color.rgb(0, 0, 0, 0.8));
            gc.fillRect(40, 40, 720, 500);

            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Arial", 28));
            gc.fillText("--- BATOH ---", 70, 90);
            gc.fillText("--- CRAFTING (1+1) ---", 400, 90);

            List<trosecnik.inventory.Item> items = player.getInventory().getItems();
            int startX = 70;
            int startY = 120;
            int slotSize = 60;
            int padding = 10;

            for (int i = 0; i < 16; i++) {
                int col = i % 4;
                int row = i / 4;
                int x = startX + col * (slotSize + padding);
                int y = startY + row * (slotSize + padding);

                gc.setFill(Color.DARKGRAY);
                gc.fillRect(x, y, slotSize, slotSize);
                gc.setStroke(Color.WHITE);
                gc.strokeRect(x, y, slotSize, slotSize);

                if (i < items.size()) {
                    gc.setFill(Color.BLACK);
                    gc.setFont(javafx.scene.text.Font.font("Arial", 12));
                    String name = items.get(i).name();
                    String shortName = name.length() > 6 ? name.substring(0, 5) + "." : name;
                    gc.fillText(shortName, x + 5, y + 35);
                }
            }

            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Arial", 30));

            gc.setFill(Color.DARKGRAY);
            gc.fillRect(400, 150, 80, 80);
            gc.setStroke(Color.WHITE);
            gc.strokeRect(400, 150, 80, 80);
            if (craftSlot1 != null) {
                gc.setFill(Color.BLACK);
                gc.setFont(javafx.scene.text.Font.font("Arial", 14));
                gc.fillText(craftSlot1.name(), 405, 195);
            }

            gc.setFill(Color.WHITE);
            gc.fillText("+", 495, 200);

            gc.setFill(Color.DARKGRAY);
            gc.fillRect(530, 150, 80, 80);
            gc.strokeRect(530, 150, 80, 80);
            if (craftSlot2 != null) {
                gc.setFill(Color.BLACK);
                gc.fillText(craftSlot2.name(), 535, 195);
            }

            gc.setFill(Color.WHITE);
            gc.fillText("=", 625, 200);

            gc.setFill(Color.rgb(40, 80, 40));
            gc.fillRect(660, 150, 80, 80);
            gc.setStroke(Color.LIGHTGREEN);
            gc.strokeRect(660, 150, 80, 80);

            String previewName = getCraftingPreviewName();
            if (!previewName.isEmpty()) {
                gc.setFill(Color.LIGHTGREEN);
                gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 13));
                gc.fillText(previewName, 665, 195);
            } else if (craftSlot1 != null || craftSlot2 != null) {
                gc.setFill(Color.RED);
                gc.setFont(javafx.scene.text.Font.font("Arial", 12));
                gc.fillText("Neznámý", 675, 195);
            }

            gc.setFill(Color.ORANGE);
            gc.fillRect(400, 260, 210, 50);
            gc.setFill(Color.BLACK);
            gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 22));
            gc.fillText("VYROBIT", 455, 295);

            gc.setFill(Color.LIGHTGRAY);
            gc.setFont(javafx.scene.text.Font.font("Arial", 14));
            gc.fillText("Tip: Pro snězení jídla na něj klikni PRAVÝM tlačítkem v batohu.", 400, 350);
        }

        if (fullDialogue != null && domorodec != null) {
            String typedMessage = fullDialogue.substring(0, visibleChars);
            double barX = 0;
            double barY = 100;
            double barW = 10 * TILE_SIZE;
            double barH = TILE_SIZE * 1.5;

            gc.setFill(Color.rgb(10, 10, 10, 0.9));
            gc.fillRect(barX, barY, barW, barH);
            gc.setFill(Color.YELLOW);
            gc.fillRect(barX, barY + barH - 5, barW, 5);

            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 22));

            int maxChars = 55;
            double textStartX = 30;
            double textYStart = barY + 40;

            if (typedMessage.length() > maxChars) {
                int splitIndex = fullDialogue.lastIndexOf(' ', maxChars);
                if (splitIndex == -1 || splitIndex < typedMessage.length() / 2) splitIndex = maxChars;

                String line1;
                String line2 = "";

                if (visibleChars <= splitIndex) {
                    line1 = typedMessage;
                } else {
                    line1 = fullDialogue.substring(0, splitIndex).trim();
                    line2 = typedMessage.substring(splitIndex).trim();
                }

                gc.fillText(line1, barX + textStartX, textYStart);
                gc.fillText(line2, barX + textStartX, textYStart + 30);
            } else {
                gc.fillText(typedMessage, barX + textStartX, textYStart);
            }
        }

        if (!showInventory) {
            int hotbarSlots = 5;
            int slotSize = 60;
            int spacing = 10;
            int totalWidth = (hotbarSlots * slotSize) + ((hotbarSlots - 1) * spacing);
            int startX = (10 * TILE_SIZE - totalWidth) / 2;
            int startY = 8 * TILE_SIZE - slotSize - 20;

            gc.setFill(Color.rgb(0, 0, 0, 0.6));
            gc.fillRect(startX - 15, startY - 15, totalWidth + 30, slotSize + 30);

            List<trosecnik.inventory.Item> items = player.getInventory().getItems();

            for (int i = 0; i < hotbarSlots; i++) {
                int x = startX + i * (slotSize + spacing);

                gc.setFill(Color.rgb(50, 50, 50, 0.9));
                gc.fillRect(x, startY, slotSize, slotSize);

                if (i == activeHotbarSlot) {
                    gc.setStroke(Color.YELLOW);
                    gc.setLineWidth(4);
                } else {
                    gc.setStroke(Color.WHITE);
                    gc.setLineWidth(2);
                }
                gc.strokeRect(x, startY, slotSize, slotSize);
                gc.setLineWidth(1);

                gc.setFill(Color.LIGHTGRAY);
                gc.setFont(javafx.scene.text.Font.font("Arial", 12));
                gc.fillText(String.valueOf(i + 1), x + 5, startY + 15);

                if (i < items.size()) {
                    String name = items.get(i).name();
                    String shortName = name.length() > 6 ? name.substring(0, 5) + "." : name;

                    gc.setFill(Color.WHITE);
                    gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 12));
                    gc.fillText(shortName, x + 5, startY + 40);
                }
            }
        }

        if (isPaused) {
            gc.setFill(Color.rgb(0, 0, 0, 0.7));
            gc.fillRect(0, 0, 10 * TILE_SIZE, 8 * TILE_SIZE);

            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 60));
            gc.fillText("PAUZA", 300, 300);

            gc.setFont(javafx.scene.text.Font.font("Arial", 20));
            gc.fillText("Stiskni ESC pro návrat do hry", 260, 350);

            gc.setFill(Color.DARKGRAY);
            gc.fillRect(300, 400, 200, 50);
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Arial", 20));
            gc.fillText("ULOŽIT HRU", 340, 432);

            gc.setFill(Color.DARKGRAY);
            gc.fillRect(300, 470, 200, 50);
            gc.setFill(Color.WHITE);
            gc.fillText("NAČÍST HRU", 340, 502);
        }
    }

    private double getWeaponRange(String weaponName) {
        return weaponName.equals("Oštěp") ? 2.5 : 1.5;
    }

    private int getWeaponDamage(String weaponName) {
        return switch (weaponName) {
            case "Oštěp" -> 35;
            case "Sekera" -> 20;
            default -> 10;
        };
    }
    private String getCraftingPreviewName() {
        if (craftSlot1 == null) return "";
        String s1 = craftSlot1.name();
        String s2 = (craftSlot2 != null) ? craftSlot2.name() : "";

        if ((s1.equals("Kámen") && s2.equals("Větve")) || (s2.equals("Kámen") && s1.equals("Větve"))) return "Sekera";
        if ((s1.equals("Dřevo") && s2.equals("Kámen")) || (s2.equals("Dřevo") && s1.equals("Kámen"))) return "Oštěp";
        if ((s1.equals("Tříska") && s2.equals("Kámen")) || (s2.equals("Tříska") && s1.equals("Kámen"))) return "Oheň";
        if ((s1.equals("Oheň") && s2.equals("Syrové maso")) || (s2.equals("Oheň") && s1.equals("Syrové maso"))) return "Pečené maso";
        if (s1.equals("Dřevo") && s2.isEmpty()) return "4x Tříska";
        return "";
    }

    private int[] calculateAIPath(int currentX, int currentY, int dx, int dy) {
        int stepX = Integer.compare(dx, 0);
        int stepY = Integer.compare(dy, 0);

        if (Math.abs(dx) > Math.abs(dy)) {
            if (stepX != 0 && isValidMove(currentX + stepX, currentY)) return new int[]{currentX + stepX, currentY};
            if (stepY != 0 && isValidMove(currentX, currentY + stepY)) return new int[]{currentX, currentY + stepY};
        } else {
            if (stepY != 0 && isValidMove(currentX, currentY + stepY)) return new int[]{currentX, currentY + stepY};
            if (stepX != 0 && isValidMove(currentX + stepX, currentY)) return new int[]{currentX + stepX, currentY};
        }
        return new int[]{currentX, currentY};
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

    private boolean isValidMove(int x, int y) {
        if (!gameMap.isWalkable(x, y)) return false;
        if (x == player.getX() && y == player.getY()) return false;
        return domorodec == null || x != domorodec.getX() || y != domorodec.getY();
    }

    private boolean hasLineOfSight(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = Integer.compare(x2, x1);
        int sy = Integer.compare(y2, y1);
        int err = dx - dy;

        int currentX = x1;
        int currentY = y1;

        while (true) {
            if ((currentX != x1 || currentY != y1) && (currentX != x2 || currentY != y2)) {
                if (!gameMap.isWalkable(currentX, currentY)) return false;
            }
            if (currentX == x2 && currentY == y2) break;

            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; currentX += sx; }
            if (e2 < dx) { err += dx; currentY += sy; }
        }
        return true;
    }
}
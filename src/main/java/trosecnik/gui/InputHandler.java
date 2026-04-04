package trosecnik.gui;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import trosecnik.engine.GameMap;
import trosecnik.engine.SaveLoadManager;
import trosecnik.engine.TimeThread;
import trosecnik.model.NPC;
import trosecnik.model.Player;

import java.util.List;

public class InputHandler {

    public void handleKeyPressed(KeyEvent event, App app, Player player, NPC domorodec, NPC divocak, GameMap gameMap) {

        var state = app.getGameStateManager().getCurrentState();
        var GameState = trosecnik.engine.GameStateManager.GameState.class;

        if (state == trosecnik.engine.GameStateManager.GameState.MAIN_MENU) {
            return;
        }

        if (state == trosecnik.engine.GameStateManager.GameState.GAME_OVER) {
            return;
        }

        if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
            if (app.getFullDialogue() != null) {
                app.setFullDialogue(null);
                if (app.getTypingTimer() != null) app.getTypingTimer().stop();
                app.requestDraw();
                return;
            }
            app.setPaused(!app.isPaused());
            app.setShowInventory(false);
            if (app.getTimeThread() != null) app.getTimeThread().setPaused(app.isPaused());
            app.requestDraw();
            return;
        }

        if (app.isPaused()) return;

        if (event.getCode() == javafx.scene.input.KeyCode.C) {
            app.setShowInventory(!app.isShowInventory());
            app.requestDraw();
            return;
        }

        if (!app.isShowInventory()) {
            switch (event.getCode()) {
                case DIGIT1, NUMPAD1 -> { app.setActiveHotbarSlot(0); app.requestDraw(); return; }
                case DIGIT2, NUMPAD2 -> { app.setActiveHotbarSlot(1); app.requestDraw(); return; }
                case DIGIT3, NUMPAD3 -> { app.setActiveHotbarSlot(2); app.requestDraw(); return; }
                case DIGIT4, NUMPAD4 -> { app.setActiveHotbarSlot(3); app.requestDraw(); return; }
                case DIGIT5, NUMPAD5 -> { app.setActiveHotbarSlot(4); app.requestDraw(); return; }
            }
        }

        if (app.isShowInventory()) {
            switch (event.getCode()) {
                case F -> {
                    if (player.getCraftingSystem().craftAxe(player.getInventory())) System.out.println("Úspěšně jsi vyrobil Sekeru!");
                    else System.out.println("Nemáš dost surovin na Sekeru (Kámen + Větve).");
                    app.requestDraw();
                }
                case G -> {
                    if (player.getCraftingSystem().craftSpear(player.getInventory())) System.out.println("Úspěšně jsi vyrobil Oštěp!");
                    else System.out.println("Nemáš dost surovin na Oštěp (Dřevo + Kámen).");
                    app.requestDraw();
                }
                case T -> {
                    if (player.getCraftingSystem().craftSplinters(player.getInventory())) System.out.println("Rozštípl jsi drevo 4 třísky");
                    app.requestDraw();
                }
                case O -> {
                    if (player.getCraftingSystem().craftFire(player.getInventory())) System.out.println("ty žháři pomalu!");
                    app.requestDraw();
                }
                case P -> {
                    if (player.getCraftingSystem().craftCookedMeat(player.getInventory())) System.out.println("uepkl si maso wow! ");
                    app.requestDraw();
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

            if (domorodec != null && targetX == domorodec.getHomeX() && targetY == domorodec.getHomeY()) {
                System.out.println("Bum! Narazil jsi do " + domorodec.getName());
            }
            else if (divocak != null && divocak.getHealth() > 0 && targetX == divocak.getX() && targetY == divocak.getY()) {
                System.out.println("Narazil jsi do divočáka! K útoku použij myš!");
            }
            else {
                player.move(dx, dy);
            }

            if (app.getFullDialogue() != null) {
                app.setFullDialogue(null);
                if (app.getTypingTimer() != null) app.getTypingTimer().stop();
            }
            app.requestDraw();
        }
        else if (event.getCode() == javafx.scene.input.KeyCode.E) {
            List<trosecnik.inventory.Item> items = player.getInventory().getItems();
            trosecnik.inventory.Item activeItem = null;
            if (app.getActiveHotbarSlot() < items.size()) {
                activeItem = items.get(app.getActiveHotbarSlot());
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

                if (domorodec != null && Math.abs(player.getX() - domorodec.getHomeX()) <= 1 && Math.abs(player.getY() - domorodec.getHomeY()) <= 1) {
                    app.startDialogue(domorodec);
                }
            }
        }
    }

    public void handleMouseClicked(MouseEvent event, App app, Player player, NPC divocak, GameMap gameMap, SaveLoadManager saveLoadManager, javafx.stage.Stage primaryStage) {

        var state = app.getGameStateManager().getCurrentState();
        double px = event.getX();
        double py = event.getY();

        if (state == trosecnik.engine.GameStateManager.GameState.MAIN_MENU) {
            if (px >= 250 && px <= 550) {
                if (py >= 350 && py <= 400) {
                    app.resetGame();
                } else if (py >= 420 && py <= 470) {
                    javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
                    fileChooser.setTitle("Vyber uloženou hru k načtení");
                    fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Textové soubory (*.txt)", "*.txt"));
                    java.io.File file = fileChooser.showOpenDialog(primaryStage);

                    if (file != null) {
                        if (saveLoadManager.loadGameState(file.getAbsolutePath(), player, gameMap)) {
                            app.getGameStateManager().setState(trosecnik.engine.GameStateManager.GameState.PLAYING);
                            if (app.getTimeThread() != null) app.getTimeThread().setPaused(false);
                            app.requestDraw();
                        }
                    }
                } else if (py >= 490 && py <= 540) {
                    System.exit(0);
                }
            }
            return;
        }

        if (state == trosecnik.engine.GameStateManager.GameState.GAME_OVER) {
            if (px >= 250 && px <= 650) {
                if (py >= 420 && py <= 470) {
                    if (app.getTimeThread() != null) app.getTimeThread().setPaused(true);
                    app.getGameStateManager().setState(trosecnik.engine.GameStateManager.GameState.MAIN_MENU);
                    app.requestDraw();
                } else if (py >= 490 && py <= 540) {
                    System.exit(0);
                }
            }
            return;
        }

        if (app.isPaused()) {
            if (px >= 300 && px <= 500) {
                if (py >= 400 && py <= 450) {
                    javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
                    fileChooser.setTitle("Vyber, kam chceš hru uložit");
                    fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Textové soubory (*.txt)", "*.txt"));
                    java.io.File file = fileChooser.showSaveDialog(primaryStage);

                    if (file != null) {
                        saveLoadManager.saveGame(file.getAbsolutePath(), player, gameMap);
                        app.setPaused(false);
                        if (app.getTimeThread() != null) app.getTimeThread().setPaused(false);
                        app.requestDraw();
                    }
                } else if (py >= 470 && py <= 520) {
                    app.setPaused(false);
                    if (app.getTimeThread() != null) app.getTimeThread().setPaused(true);
                    app.getGameStateManager().setState(trosecnik.engine.GameStateManager.GameState.MAIN_MENU);
                    app.requestDraw();
                }
            }
            return;
        }

        int TILE_SIZE = 80;
        int gridX = (int) (event.getX() / TILE_SIZE);
        int gridY = (int) (event.getY() / TILE_SIZE);

        if (!app.isShowInventory() && gridX >= 0 && gridX < 10 && gridY >= 0 && gridY < 8) {
            double distance = Math.hypot(player.getX() - gridX, player.getY() - gridY);
            List<trosecnik.inventory.Item> items = player.getInventory().getItems();
            String activeItemName = (app.getActiveHotbarSlot() < items.size()) ? items.get(app.getActiveHotbarSlot()).name() : "Ruce";

            if (divocak != null && divocak.getHealth() > 0 && gridX == divocak.getX() && gridY == divocak.getY()) {
                double range = app.getWeaponRange(activeItemName);
                int dmg = app.getWeaponDamage(activeItemName);

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
            app.requestDraw();
            return;
        }

        if (app.isShowInventory()) {
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
                        if (app.getCraftSlot1() == null) {
                            app.setCraftSlot1(clickedItem);
                        } else if (app.getCraftSlot2() == null && clickedItem != app.getCraftSlot1()) {
                            app.setCraftSlot2(clickedItem);
                        } else {
                            app.setCraftSlot1(clickedItem);
                            app.setCraftSlot2(null);
                        }
                    }
                }
            }

            if (x >= 400 && x <= 610 && y >= 260 && y <= 310) {
                boolean success = false;
                if (app.getCraftSlot1() != null && app.getCraftSlot2() != null) {
                    String s1 = app.getCraftSlot1().name();
                    String s2 = app.getCraftSlot2().name();

                    if ((s1.equals("Kámen") && s2.equals("Větve")) || (s2.equals("Kámen") && s1.equals("Větve"))) {
                        success = player.getCraftingSystem().craftAxe(player.getInventory());
                    } else if ((s1.equals("Dřevo") && s2.equals("Kámen")) || (s2.equals("Dřevo") && s1.equals("Kámen"))) {
                        success = player.getCraftingSystem().craftSpear(player.getInventory());
                    } else if ((s1.equals("Tříska") && s2.equals("Kámen")) || (s2.equals("Tříska") && s1.equals("Kámen"))) {
                        success = player.getCraftingSystem().craftFire(player.getInventory());
                    } else if ((s1.equals("Oheň") && s2.equals("Syrové maso")) || (s2.equals("Oheň") && s1.equals("Syrové maso"))) {
                        success = player.getCraftingSystem().craftCookedMeat(player.getInventory());
                    }
                } else if (app.getCraftSlot1() != null && app.getCraftSlot1().name().equals("Dřevo")) {
                    success = player.getCraftingSystem().craftSplinters(player.getInventory());
                }

                if (success) {
                    System.out.println("Výroba úspěšná!");
                } else {
                    System.out.println("Z těchto surovin se nic vyrobit nedá.");
                }
                app.setCraftSlot1(null);
                app.setCraftSlot2(null);
            }
            app.requestDraw();
        }
    }
}
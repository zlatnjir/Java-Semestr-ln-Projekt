package trosecnik.engine;

public class GameStateManager {

    public enum GameState {
        MAIN_MENU,
        PLAYING,
        INVENTORY_CRAFTING,
        GAME_OVER,
        VICTORY
    }

    private GameState currentState;

    public GameStateManager() {
        this.currentState = GameState.MAIN_MENU;
    }

    public void setState(GameState state) {
        this.currentState = state;
    }

    public GameState getCurrentState() {
        return currentState;
    }
}
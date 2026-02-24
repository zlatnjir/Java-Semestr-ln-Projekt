package trosecnik;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GameController {

    @FXML
    private Label welcomeText;

    @FXML
    protected void onStartButtonClick() {
        welcomeText.setText("Hra se načítá, připrav se na přežití!");

    }

    @FXML
    protected void onExitButtonClick() {
        Platform.exit();
    }
}
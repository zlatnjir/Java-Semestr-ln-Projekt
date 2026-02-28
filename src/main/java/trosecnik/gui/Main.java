package trosecnik.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Trosečník - Opuštěný ostrov");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button btnStart = new Button("Nová hra");
        Button btnExit = new Button("Ukončit");

        btnStart.setOnAction(e -> System.out.println("Zde se později načte herní mapa z textového souboru."));
        btnExit.setOnAction(e -> Platform.exit()); // Bezpečné zavření hry

        root.getChildren().addAll(titleLabel, btnStart, btnExit);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Trosečník Engine");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
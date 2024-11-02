package org.testing.project_2_1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.scene.layout.CornerRadii;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GUI extends Application {
    public boolean isSinglePlayer;
    public boolean isAgentWhite = false;

    @Override
    public void start(Stage selectionStage) {
        Pane selectionPane = new Pane();
        selectionPane.setPrefSize(400, 400);  // Square window size

        // Background set to Linen (#FAF0E6)
        selectionPane.setBackground(new Background(new BackgroundFill(Color.web("#FAF0E6"), CornerRadii.EMPTY, Insets.EMPTY)));

        // Title label (smaller, centered, and bold with a deep color) positioned higher
        Label gameTitle = new Label("FRISIAN DRAUGHTS");
        gameTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: BLACK;");
        gameTitle.setFont(new Font("Arial", 28));  // Set the font to Arial, smaller size
        gameTitle.setLayoutX(75); // Centered horizontally in 400px window
        gameTitle.setLayoutY(50); // Positioned higher up

        // Dropdown menu for selecting player mode - centered horizontally, moved higher
        ComboBox<String> playerSelection = new ComboBox<>();
        playerSelection.getItems().addAll("1 Player", "2 Players");
        playerSelection.setValue("Select Players");
        playerSelection.setLayoutX(125); // Centered horizontally in 400px window
        playerSelection.setLayoutY(190); // Moved higher up
        playerSelection.setPrefWidth(150);  // Ensuring consistent width
        playerSelection.setStyle("-fx-font-size: 16px;");

        // Primary button (Start Game) with Light Green (#90EE90) centered below dropdown, moved higher
        Button startGameButton = new Button("START GAME");
        startGameButton.setLayoutX(125); // Centered horizontally in 400px window
        startGameButton.setLayoutY(250);  // Moved higher up
        startGameButton.setPrefWidth(150);  // Ensuring consistent width with the dropdown
        startGameButton.setStyle("-fx-background-color: #90EE90; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10px 20px;");

        // Add hover effect to the Start Game button
        startGameButton.setOnMouseEntered(e -> startGameButton.setStyle("-fx-background-color: #7CFC00; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10px 20px;"));
        startGameButton.setOnMouseExited(e -> startGameButton.setStyle("-fx-background-color: #90EE90; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10px 20px;"));

        // Set action based on dropdown selection
        startGameButton.setOnAction(e -> {
            String selected = playerSelection.getValue();
            if (selected.equals("2 Players")) {
                selectionStage.close();  // Close the selection window
                try {
                    CheckersApp game = new CheckersApp();
                    Stage gameStage = new Stage();
                    game.start(gameStage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (selected.equals("1 Player")) {
                // isSinglePlayer = true;
                isAgentWhite = false;
                Agent agent = new BaselineAgent();
                CheckersApp game = new CheckersApp(agent, isAgentWhite);
                try {
                    Stage gameStage = new Stage();
                    game.start(gameStage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Add title, dropdown, and button to the selection pane
        selectionPane.getChildren().addAll(gameTitle, playerSelection, startGameButton);

        // Setup and show the selection stage
        Scene selectionScene = new Scene(selectionPane);
        selectionStage.setTitle("Game Mode Selection");
        selectionStage.setScene(selectionScene);
        selectionStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

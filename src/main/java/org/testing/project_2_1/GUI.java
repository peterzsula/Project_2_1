package org.testing.project_2_1;

import org.testing.project_2_1.Agents.*;
import org.testing.project_2_1.UI.CheckersApp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GUI extends Application {
    public boolean isSinglePlayer;
    public boolean isAgentWhite = false;

    @Override
    public void start(@SuppressWarnings("exports") Stage selectionStage) {
        Pane selectionPane = new Pane();
        selectionPane.setPrefSize(500, 500);
        selectionPane.setBackground(new Background(new BackgroundFill(Color.web("#FAF0E6"), CornerRadii.EMPTY, Insets.EMPTY)));

        Label gameTitle = new Label("Esteban Checkers");
        gameTitle.setFont(new Font("Arial", 32));
        gameTitle.setTextFill(Color.BLACK);  // Set title color to black
        gameTitle.setStyle("-fx-font-weight: bold;");
        gameTitle.setEffect(new DropShadow(5, Color.GRAY));
        gameTitle.setLayoutX(85);
        gameTitle.setLayoutY(70);

        ComboBox<String> player1Selection = new ComboBox<>();
        player1Selection.getItems().addAll("Human", "Baseline AI", "AB-Pruning", "MCTS");
        player1Selection.setValue("Select Player 1");
        player1Selection.setLayoutX(125);
        player1Selection.setLayoutY(180);
        player1Selection.setPrefWidth(250);
        player1Selection.setStyle("-fx-font-size: 16px; -fx-background-radius: 10; -fx-border-radius: 10;");
        player1Selection.setEffect(new DropShadow(5, Color.LIGHTGRAY));

        ComboBox<String> player2Selection = new ComboBox<>();
        player2Selection.getItems().addAll("Human", "Baseline AI", "AB-Pruning", "MCTS");
        player2Selection.setValue("Select Player 2");
        player2Selection.setLayoutX(125);
        player2Selection.setLayoutY(240);
        player2Selection.setPrefWidth(250);
        player2Selection.setStyle("-fx-font-size: 16px; -fx-background-radius: 10; -fx-border-radius: 10;");
        player2Selection.setEffect(new DropShadow(5, Color.LIGHTGRAY));

        Button startGameButton = new Button("START GAME");
        startGameButton.setLayoutX(175);
        startGameButton.setLayoutY(330);
        startGameButton.setPrefWidth(150);
        startGameButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 18px; -fx-background-radius: 15; -fx-border-radius: 15;");
        startGameButton.setEffect(new DropShadow(10, Color.DARKGREEN));

        startGameButton.setOnMouseEntered(e -> startGameButton.setStyle("-fx-background-color: #45A049; -fx-text-fill: white; -fx-font-size: 18px; -fx-background-radius: 15; -fx-border-radius: 15;"));
        startGameButton.setOnMouseExited(e -> startGameButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 18px; -fx-background-radius: 15; -fx-border-radius: 15;"));

        startGameButton.setOnAction(e -> {
            CheckersApp game = null;
            Agent player1Agent = null;
            Agent player2Agent = null;

            // Player 1 
            switch (player1Selection.getValue()) {
                case "Baseline AI":
                    player1Agent = new BaselineAgent(true);
                    break;
                case "Minimax":
                    player1Agent = new MinimaxAgent(true, 3);
                    break;
                case "AB-Pruning":
                    player1Agent = new AlphaBetaAgent(true, 3);
                    break;
                case "MCTS":
                    player1Agent = new AgentMCTS(true);
                    break;
                default:
                    break; // Human
            }

            // Player
            switch (player2Selection.getValue()) {
                case "Baseline AI":
                    player2Agent = new BaselineAgent(false);
                    break;
                case "Minimax":
                    player2Agent = new MinimaxAgent(false, 3);
                    break;
                case "AB-Pruning":
                    player2Agent = new AlphaBetaAgent(false, 3);
                    break;
                case "MCTS":
                    player2Agent = new AgentMCTS(false);
                    break;
                default:
                    break; // Human
            }

            if (player1Agent != null && player2Agent != null) {
                game = new CheckersApp(player1Agent, player2Agent);
            } else if (player1Agent != null) {
                game = new CheckersApp(player1Agent); // Player 1 AI, Player 2 Human
            } else if (player2Agent != null) {
                game = new CheckersApp(player2Agent); // Player 1 Human, Player 2 AI
            } else {
                game = new CheckersApp(); // Both Human
            }

            try {
                game.start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        selectionPane.getChildren().addAll(gameTitle, player1Selection, player2Selection, startGameButton);

        Scene selectionScene = new Scene(selectionPane);
        selectionStage.setTitle("Game Mode Selection");
        selectionStage.setScene(selectionScene);
        selectionStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

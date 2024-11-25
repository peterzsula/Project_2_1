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
import javafx.scene.image.Image;

public class GUI extends Application {
    public boolean isSinglePlayer;
    public boolean isAgentWhite = false;

    @Override
    public void start(@SuppressWarnings("exports") Stage selectionStage) {
        Pane selectionPane = new Pane();
        selectionPane.setPrefSize(500, 500);
        selectionPane.setBackground(new Background(new BackgroundFill(Color.web("#FAF0E6"), CornerRadii.EMPTY, Insets.EMPTY)));

        Image icon = new Image("pixel-frisian.png");
        selectionStage.getIcons().add(icon);

        Label gameTitle = new Label("Frisian Checkers");
        gameTitle.setFont(new Font("Arial", 32));
        gameTitle.setTextFill(Color.BLACK);
        gameTitle.setStyle("-fx-font-weight: bold;");
        gameTitle.setEffect(new DropShadow(5, Color.GRAY));

        gameTitle.layoutXProperty().bind(selectionPane.widthProperty().subtract(gameTitle.widthProperty()).divide(2));
        gameTitle.setLayoutY(70); 

        ComboBox<String> player1Selection = new ComboBox<>();
        player1Selection.getItems().addAll("Human", "Minimax", "Baseline AI", "Alpha-Beta Pruning", "MCTS");
        player1Selection.setValue("Select Player 1");
        player1Selection.setLayoutX(125);
        player1Selection.setLayoutY(180);
        player1Selection.setPrefWidth(250);
        player1Selection.setStyle("-fx-font-size: 16px; -fx-background-radius: 10; -fx-border-radius: 10;");
        player1Selection.setEffect(new DropShadow(5, Color.LIGHTGRAY));

        ComboBox<String> player2Selection = new ComboBox<>();
        player2Selection.getItems().addAll("Human", "Minimax", "Baseline AI", "Alpha-Beta Pruning", "MCTS");
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
        startGameButton.setDisable(true); 
        startGameButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 18px; -fx-background-radius: 15; -fx-border-radius: 15;");
        startGameButton.setEffect(new DropShadow(10, Color.DARKGREEN));

        startGameButton.setOnMouseEntered(e -> startGameButton.setStyle("-fx-background-color: #45A049; -fx-text-fill: white; -fx-font-size: 18px; -fx-background-radius: 15; -fx-border-radius: 15;"));
        startGameButton.setOnMouseExited(e -> startGameButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 18px; -fx-background-radius: 15; -fx-border-radius: 15;"));

        player1Selection.setOnAction(e -> validateSelections(player1Selection, player2Selection, startGameButton));
        player2Selection.setOnAction(e -> validateSelections(player1Selection, player2Selection, startGameButton));

        // Click or Enter Key
        startGameButton.setOnAction(e -> {
            if (!startGameButton.isDisabled()) {
                startGame(player1Selection, player2Selection);
            }
        });

        startGameButton.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ENTER: // Start game when ENTER is pressed
                    if (!startGameButton.isDisabled()) {
                        startGame(player1Selection, player2Selection);
                    }
                    break;
                default:
                    break;
            }
        });

        player1Selection.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case DOWN:
                    player2Selection.requestFocus(); // Navigate to player 2 selection
                    break;
                case ENTER:
                    player2Selection.requestFocus(); // Navigate to player 2 selection
                    break;
                default:
                    break;
            }
        });

        player2Selection.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case UP:
                    player1Selection.requestFocus(); // Navigate back to player 1 selection
                    break;
                case ENTER:
                    startGameButton.requestFocus(); // Navigate to start button
                    break;
                default:
                    break;
            }
        });

        selectionPane.getChildren().addAll(gameTitle, player1Selection, player2Selection, startGameButton);

        Scene selectionScene = new Scene(selectionPane);
        selectionStage.setTitle("Game Mode Selection");
        selectionStage.setScene(selectionScene);
        selectionStage.show();
    }

    private void validateSelections(ComboBox<String> player1, ComboBox<String> player2, Button startButton) {
        String p1 = player1.getValue();
        String p2 = player2.getValue();
        boolean valid = !p1.equals("Select Player 1") && !p2.equals("Select Player 2");
        startButton.setDisable(!valid);
    }

    private void startGame(ComboBox<String> player1Selection, ComboBox<String> player2Selection) {
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
            case "Alpha-Beta Pruning":
                player1Agent = new AlphaBetaAgent(true, 6);
                break;
            case "MCTS":
                player1Agent = new AgentMCTS(true);
                break;
            default:
                player1Agent = null;
                break; // Human
        }

        // Player 2 
        switch (player2Selection.getValue()) {
            case "Baseline AI":
                player2Agent = new BaselineAgent(false);
                break;
            case "Minimax":
                player2Agent = new MinimaxAgent(false, 3);
                break;
            case "Alpha-Beta Pruning":
                player2Agent = new AlphaBetaAgent(false, 3);
                break;
            case "MCTS":
                player2Agent = new AgentMCTS(false);
                break;
            default:
                player2Agent = null;
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
    }

    public static void main(String[] args) {
        launch(args);
    }
}

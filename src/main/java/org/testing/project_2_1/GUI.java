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

/**
 * The GUI class for the Frisian Checkers game.
 * This class provides the main game mode selection screen where users can choose
 * between human and AI players, and start the game with the chosen configurations.
 */
public class GUI extends Application {
    public boolean isSinglePlayer; // Tracks if the game is single-player
    public boolean isAgentWhite = false; // Tracks if the agent plays as white

    /**
     * Starts the JavaFX application and initializes the game mode selection screen.
     *
     * @param selectionStage The main stage for the selection screen.
     */
    @Override
    public void start(Stage selectionStage) {
        Pane selectionPane = new Pane();
        selectionPane.setPrefSize(500, 500);
        selectionPane.setBackground(new Background(new BackgroundFill(Color.web("#FAF0E6"), CornerRadii.EMPTY, Insets.EMPTY)));

        // Set application icon
        Image icon = new Image("pixel-frisian.png");
        selectionStage.getIcons().add(icon);

        // Create and style the game title label
        Label gameTitle = new Label("Frisian Checkers");
        gameTitle.setFont(new Font("Arial", 32));
        gameTitle.setTextFill(Color.BLACK);
        gameTitle.setStyle("-fx-font-weight: bold;");
        gameTitle.setEffect(new DropShadow(5, Color.GRAY));
        gameTitle.layoutXProperty().bind(selectionPane.widthProperty().subtract(gameTitle.widthProperty()).divide(2));
        gameTitle.setLayoutY(70);

        // Player 1 selection dropdown
        ComboBox<String> player1Selection = new ComboBox<>();
        player1Selection.getItems().addAll("Human", "Minimax", "Baseline AI", "Alpha-Beta Pruning", "MCTS");
        player1Selection.setValue("Select Player 1");
        player1Selection.setLayoutX(125);
        player1Selection.setLayoutY(180);
        player1Selection.setPrefWidth(250);
        player1Selection.setStyle("-fx-font-size: 16px; -fx-background-radius: 10; -fx-border-radius: 10;");
        player1Selection.setEffect(new DropShadow(5, Color.LIGHTGRAY));

        // Player 2 selection dropdown
        ComboBox<String> player2Selection = new ComboBox<>();
        player2Selection.getItems().addAll("Human", "Minimax", "Baseline AI", "Alpha-Beta Pruning", "MCTS");
        player2Selection.setValue("Select Player 2");
        player2Selection.setLayoutX(125);
        player2Selection.setLayoutY(240);
        player2Selection.setPrefWidth(250);
        player2Selection.setStyle("-fx-font-size: 16px; -fx-background-radius: 10; -fx-border-radius: 10;");
        player2Selection.setEffect(new DropShadow(5, Color.LIGHTGRAY));

        // Start Game button
        Button startGameButton = new Button("START GAME");
        startGameButton.setLayoutX(175);
        startGameButton.setLayoutY(330);
        startGameButton.setPrefWidth(150);
        startGameButton.setDisable(true); // Initially disabled until valid selections are made
        startGameButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 18px; -fx-background-radius: 15; -fx-border-radius: 15;");
        startGameButton.setEffect(new DropShadow(10, Color.DARKGREEN));

        // Button hover effects
        startGameButton.setOnMouseEntered(e -> startGameButton.setStyle("-fx-background-color: #45A049; -fx-text-fill: white; -fx-font-size: 18px; -fx-background-radius: 15; -fx-border-radius: 15;"));
        startGameButton.setOnMouseExited(e -> startGameButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 18px; -fx-background-radius: 15; -fx-border-radius: 15;"));

        // Validate selections and enable/disable the Start Game button
        player1Selection.setOnAction(e -> validateSelections(player1Selection, player2Selection, startGameButton));
        player2Selection.setOnAction(e -> validateSelections(player1Selection, player2Selection, startGameButton));

        // Start the game when the button is clicked or ENTER is pressed
        startGameButton.setOnAction(e -> {
            if (!startGameButton.isDisabled()) {
                startGame(player1Selection, player2Selection);
            }
        });

        startGameButton.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ENTER:
                    if (!startGameButton.isDisabled()) {
                        startGame(player1Selection, player2Selection);
                    }
                    break;
                default:
                    break;
            }
        });

        // Keyboard navigation for dropdowns and buttons
        player1Selection.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case DOWN:
                case ENTER:
                    player2Selection.requestFocus();
                    break;
                default:
                    break;
            }
        });

        player2Selection.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case UP:
                    player1Selection.requestFocus();
                    break;
                case ENTER:
                    startGameButton.requestFocus();
                    break;
                default:
                    break;
            }
        });

        // Add UI elements to the selection pane
        selectionPane.getChildren().addAll(gameTitle, player1Selection, player2Selection, startGameButton);

        // Set up and display the scene
        Scene selectionScene = new Scene(selectionPane);
        selectionStage.setTitle("Game Mode Selection");
        selectionStage.setScene(selectionScene);
        selectionStage.show();
    }

    /**
     * Validates the player selections and enables/disables the Start Game button accordingly.
     *
     * @param player1     The ComboBox for Player 1's selection.
     * @param player2     The ComboBox for Player 2's selection.
     * @param startButton The Start Game button.
     */
    private void validateSelections(ComboBox<String> player1, ComboBox<String> player2, Button startButton) {
        String p1 = player1.getValue();
        String p2 = player2.getValue();
        boolean valid = !p1.equals("Select Player 1") && !p2.equals("Select Player 2");
        startButton.setDisable(!valid);
    }

    /**
     * Starts the game with the selected player configurations.
     *
     * @param player1Selection The ComboBox for Player 1's selection.
     * @param player2Selection The ComboBox for Player 2's selection.
     */
    private void startGame(ComboBox<String> player1Selection, ComboBox<String> player2Selection) {
        CheckersApp game = null;
        Agent player1Agent = null;
        Agent player2Agent = null;

        // Configure Player 1
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
                player1Agent = null; // Human
                break;
        }

        // Configure Player 2
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
                player2Agent = null; // Human
                break;
        }

        // Create game with the appropriate configurations
        if (player1Agent != null && player2Agent != null) {
            game = new CheckersApp(player1Agent, player2Agent);
        } else if (player1Agent != null) {
            game = new CheckersApp(player1Agent);
        } else if (player2Agent != null) {
            game = new CheckersApp(player2Agent);
        } else {
            game = new CheckersApp();
        }

        // Start the game
        try {
            game.start(new Stage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Main method to launch the JavaFX application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}

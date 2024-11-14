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
import javafx.geometry.Insets;
import javafx.scene.layout.CornerRadii;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GUI extends Application {
    public boolean isSinglePlayer;
    public boolean isAgentWhite = false;

    @Override
    public void start(@SuppressWarnings("exports") Stage selectionStage) {
        Pane selectionPane = new Pane();
        selectionPane.setPrefSize(400, 400);

        selectionPane.setBackground(new Background(new BackgroundFill(Color.web("#FAF0E6"), CornerRadii.EMPTY, Insets.EMPTY)));

        Label gameTitle = new Label("FRISIAN DRAUGHTS");
        gameTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: BLACK;");
        gameTitle.setFont(new Font("Arial", 28));
        gameTitle.setLayoutX(75);
        gameTitle.setLayoutY(50);

        ComboBox<String> gameModeSelection = new ComboBox<>();
        gameModeSelection.getItems().addAll("NI vs NI", "NI vs AI", "AI vs AI");
        gameModeSelection.setValue("Select Players");
        gameModeSelection.setLayoutX(125);
        gameModeSelection.setLayoutY(190);
        gameModeSelection.setPrefWidth(150);
        gameModeSelection.setStyle("-fx-font-size: 16px;");

        ComboBox<String> agentSelection = new ComboBox<>();
        agentSelection.getItems().addAll("NI vs BaselineAI", "NI vs AB-Pruning", "NI vs MCTS");
        agentSelection.setValue("Select AI");
        agentSelection.setLayoutX(125);
        agentSelection.setLayoutY(230);
        agentSelection.setPrefWidth(150);
        agentSelection.setStyle("-fx-font-size: 16px;");
        agentSelection.setVisible(false);

        Button startGameButton = new Button("START GAME");
        startGameButton.setLayoutX(125);
        startGameButton.setLayoutY(300);
        startGameButton.setPrefWidth(150);
        startGameButton.setStyle("-fx-background-color: #90EE90; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10px 20px;");

        startGameButton.setOnMouseEntered(e -> startGameButton.setStyle("-fx-background-color: #7CFC00; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10px 20px;"));
        startGameButton.setOnMouseExited(e -> startGameButton.setStyle("-fx-background-color: #90EE90; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10px 20px;"));

        startGameButton.setOnAction(e -> {
            CheckersApp game = null;
            switch (gameModeSelection.getValue()) {
                case "NI vs NI":
                    System.out.println("NI vs NI");
                    game = new CheckersApp();
                    break;
                case "NI vs AI":
                    isAgentWhite = false;
                    switch (agentSelection.getValue()) {
                        case "NI vs BaselineAI":
                            Agent agent_baseline = new BaselineAgent(false);
                            game = new CheckersApp(agent_baseline);
                            break;
                        case "NI vs AB-Pruning":
                            Agent agent_abPruning = new AlphaBetaAgent(false);
                            game = new CheckersApp(agent_abPruning);
                            break;
                        case "NI vs MCTS":
                            Agent agent_mcts = new AgentMCTS(false);
                            game = new CheckersApp(agent_mcts);
                            break;
                        default:
                            System.out.println("Please select an agent");
                            return;
                    }
                    break;
                case "AI vs AI": // for now lets keep this baseline vs baseline
                    Agent agent1 = new AlphaBetaAgent(true);
                    Agent agent2 = new MLBaseLine(false);
                    game = new CheckersApp(agent1, agent2);
                    break;
                default:
                    break;
            }
            try {
                game.start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        // Show agentSelection only if second option elected
        gameModeSelection.setOnAction(e -> {
            agentSelection.setVisible(gameModeSelection.getValue().equals("NI vs AI"));
        });

        selectionPane.getChildren().addAll(gameTitle, gameModeSelection, agentSelection, startGameButton);
        Scene selectionScene = new Scene(selectionPane);
        selectionStage.setTitle("Game Mode Selection");
        selectionStage.setScene(selectionScene);
        selectionStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

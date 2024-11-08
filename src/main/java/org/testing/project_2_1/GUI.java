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
        selectionPane.setPrefSize(400, 400);  

        selectionPane.setBackground(new Background(new BackgroundFill(Color.web("#FAF0E6"), CornerRadii.EMPTY, Insets.EMPTY)));

        Label gameTitle = new Label("FRISIAN DRAUGHTS");
        gameTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: BLACK;");
        gameTitle.setFont(new Font("Arial", 28)); 
        gameTitle.setLayoutX(75); 
        gameTitle.setLayoutY(50); 

        ComboBox<String> playerSelection = new ComboBox<>();
        playerSelection.getItems().addAll("NI vs AI", " NI vs NI", "AI vs AI");
        playerSelection.setValue("Select Players");
        playerSelection.setLayoutX(125); 
        playerSelection.setLayoutY(190);
        playerSelection.setPrefWidth(150); 
        playerSelection.setStyle("-fx-font-size: 16px;");

        Button startGameButton = new Button("START GAME");
        startGameButton.setLayoutX(125); 
        startGameButton.setLayoutY(250);  
        startGameButton.setPrefWidth(150); 
        startGameButton.setStyle("-fx-background-color: #90EE90; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10px 20px;");

        startGameButton.setOnMouseEntered(e -> startGameButton.setStyle("-fx-background-color: #7CFC00; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10px 20px;"));
        startGameButton.setOnMouseExited(e -> startGameButton.setStyle("-fx-background-color: #90EE90; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10px 20px;"));

        
        startGameButton.setOnAction(e -> {
            CheckersApp game = null;
            switch (playerSelection.getValue()) {
                case "NI vs NI":
                    game = new CheckersApp();
                    selectionStage.close();  
                    break;
                case "NI vs AI":
                    isAgentWhite = false;
                    Agent agent = new BaselineAgent(false);
                    game = new CheckersApp(agent, isAgentWhite);
                    break;
                case "AI vs AI":
                    Agent agent1 = new BaselineAgent(true);
                    Agent agent2 = new BaselineAgent(false);
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

        selectionPane.getChildren().addAll(gameTitle, playerSelection, startGameButton);

        Scene selectionScene = new Scene(selectionPane);
        selectionStage.setTitle("Game Mode Selection");
        selectionStage.setScene(selectionScene);
        selectionStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

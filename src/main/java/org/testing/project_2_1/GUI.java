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
        playerSelection.getItems().addAll("1 Player", "2 Players");
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
            String selected = playerSelection.getValue();
            if (selected.equals("2 Players")) {
                selectionStage.close();  
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

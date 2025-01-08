package org.testing.project_2_1.Moves;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.testing.project_2_1.GameLogic.GameState;
import org.testing.project_2_1.GameLogic.Piece;

public class CSVHandler {
    public static void writeCSV(GameState game){
        try {
            FileWriter writer = new FileWriter("src/main/resources/moves.csv");
            writer.write("\n");
            for (Turn turn : game.getTurnsPlayed()) {
                for (Move move : turn.getMoves()) {

                    writer.write(move.getFromX() + "," + move.getFromY() + "," + move.getToX() + "," + move.getToY() + ",");
                }
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("An error occurred, while writing to the file.");
        }
    }

    public static List<Move> readCSV(){
        // TODO: 
        return null;
    }
}

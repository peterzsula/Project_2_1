package org.testing.project_2_1.GameLogic;

import static org.testing.project_2_1.UI.CheckersApp.SIZE;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testing.project_2_1.Moves.Capture;
import org.testing.project_2_1.Moves.InvalidMove;
import org.testing.project_2_1.Moves.Move;
import org.testing.project_2_1.Moves.NormalMove;
import org.testing.project_2_1.Moves.Turn;

/**
 * Represents the state of the game, including the board, pieces, turns, and game status.
 * Provides methods to manage the game state, evaluate board positions, and determine game phases.
 */
public class GameState {
    protected Tile[][] board; // The game board represented as a 2D array of tiles
    protected boolean isWhiteTurn; // Indicates if it's White's turn
    private ArrayList<Piece> whitePieces; // List of ALL white pieces currently on the board (including kings)
    private ArrayList<Piece> blackPieces; // List of ALL black pieces currently on the board (including kings)
    private ArrayList<Turn> turnsPlayed; // History of turns played
    private Turn currentTurn; // The current turn being processed
    private List<Turn> possibleTurns; // List of possible turns for the current player
    private boolean isGameOver; // Indicates if the game is over
    private int winner; // Stores the winner of the game (0 = none, 1 = white, -1 = black)
    private ArrayList<Piece> whiteKings; // List of white kings
    private ArrayList<Piece> blackKings; // List of black kings

    /**
     * Default constructor for initializing a new game state.
     * Sets up the board with pieces in their starting positions.
     */
    public GameState() {
        isWhiteTurn = true;
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();
        whiteKings = new ArrayList<>();
        blackKings = new ArrayList<>();
        turnsPlayed = new ArrayList<>();
        currentTurn = new Turn();
        possibleTurns = new ArrayList<>();
        isGameOver = false;
        board = new Tile[SIZE][SIZE];
        winner = 0;

        // Initialize the board and place the pieces in their starting positions
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                Tile tile = new Tile(x, y);
                board[x][y] = tile;
                if (y <= ((SIZE - 2) / 2) - 1 && tile.isBlack()) {
                    Piece piece = new Piece(PieceType.BLACK, x, y, this);
                    tile.setPiece(piece);
                    blackPieces.add(piece);
                } else if (y >= SIZE - ((SIZE - 2) / 2) && tile.isBlack()) {
                    Piece piece = new Piece(PieceType.WHITE, x, y, this);
                    tile.setPiece(piece);
                    whitePieces.add(piece);
                }
            }
        }
    }

    /**
     * Copy constructor for cloning an existing game state.
     * @param originalB The original game state to copy.
     */
    public GameState(GameState originalB) {
        this.whitePieces = new ArrayList<>();
        this.blackPieces = new ArrayList<>();
        this.whiteKings = new ArrayList<>();
        this.blackKings = new ArrayList<>();
        this.turnsPlayed = new ArrayList<>();
        this.currentTurn = new Turn(originalB.getCurrentTurn());
        this.possibleTurns = new ArrayList<>(originalB.getPossibleTurns());
        this.isWhiteTurn = originalB.getIsWhiteTurn();
        this.board = new Tile[SIZE][SIZE];
        this.isGameOver = false;
        winner = 0;

        // Clone the board and pieces
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                this.board[x][y] = new Tile(x, y);
                if (originalB.board[x][y].hasPiece()) {
                    Piece originalPiece = originalB.board[x][y].getPiece();
                    Piece piece = new Piece(originalPiece.type, x, y, this);
                    this.board[x][y].setPiece(piece);
                    if (piece.type == PieceType.WHITE || piece.type == PieceType.WHITEKING) {
                        this.whitePieces.add(this.board[x][y].getPiece());
                    } else {
                        this.blackPieces.add(this.board[x][y].getPiece());
                    }
                    if (piece.type == PieceType.WHITEKING) {
                        whiteKings.add(piece);
                    }
                    else if (piece.type == PieceType.BLACKKING) {
                        blackKings.add(piece);
                    }
                }
            }
        }

        // Clone the turn history and possible turns
        for (Turn turn : originalB.getTurnsPlayed()) {
            Turn turnCopy = new Turn(turn);
            turnsPlayed.add(turnCopy);
        }
        for (Turn turn : originalB.possibleTurns) {
            possibleTurns.add(new Turn(turn));
        }
    }

    /**
     * Retrieves the board of the current game state.
     * @return A 2D array representing the game board.
     */
    public Tile[][] getBoard() {
        return board;
    }

    /**
     * Retrieves the list of white pieces currently on the board.
     * @return A list of white pieces.
     */
    public ArrayList<Piece> getWhitePieces() {
        return whitePieces;
    }

    /**
     * Retrieves the list of black pieces currently on the board.
     * @return A list of black pieces.
     */
    public ArrayList<Piece> getBlackPieces() {
        return blackPieces;
    }

    /**
     * Retrieves the history of turns played in the game.
     * @return A list of turns played.
     */
    public ArrayList<Turn> getTurnsPlayed() {
        return turnsPlayed;
    }
    public int getDepth() {
        return turnsPlayed.size();
    }
    /**
     * Switches the turn to the other player.
     */
    public void switchTurn() {
        isWhiteTurn = !isWhiteTurn;
    }

    /**
     * Sets the game over state and the winner of the game.
     * @param isGameOver Whether the game is over.
     * @param winner The winner of the game (0 = none, 1 = white, -1 = black).
     */
    public void setGameOver(boolean isGameOver, int winner) {
        this.isGameOver = isGameOver;
        this.winner = winner;
    }

    /**
     * Checks if the game is over by verifying if any player has no remaining pieces.
     * @return True if the game is over, otherwise false.
     */
    public boolean isGameOver() {
        if (whitePieces.isEmpty() || blackPieces.isEmpty() || isDraw()) {
            isGameOver = true;
        }
        return isGameOver;
    }

    /**
     * Determines if the game is in the endgame phase based on the number of remaining pieces.
     * @return True if the game is in the endgame phase, otherwise false.
     */
    public boolean isEndgame() {
        int totalPieces = countPieces();
        int endgameThreshold = 12;
        return totalPieces < endgameThreshold;
    }

    /**
     * Counts the total number of pieces on the board.
     * @return The total number of pieces.
     */
    public int countPieces() {
        return whitePieces.size() + blackPieces.size();
    }

    /**
     * Evaluates the board position and returns a score based on the current game state.
     * @return A score indicating the favorability of the current state.
     */
    public int evaluate() {
        if (isGameOver()) {
            if ((isWhiteTurn && whitePieces.isEmpty()) || (!isWhiteTurn && blackPieces.isEmpty())) {
                return -1; // Losing state for the current player
            } else {
                return 1; // Winning state for the current player
            }
        }

        int score = 0;

        // Calculate score for white pieces
        for (Piece piece : whitePieces) {
            if (piece.getType() == PieceType.BLACKKING || piece.getType() == PieceType.WHITEKING) {
                score += 3;
            } else {
                score += 1;
            }
        }

        // Calculate score for black pieces
        for (Piece piece : blackPieces) {
            if (piece.getType() == PieceType.BLACKKING || piece.getType() == PieceType.WHITEKING) {
                score -= 3;
            } else {
                score -= 1;
            }
        }

        // Adjust score based on whose turn it is
        if (isWhiteTurn) {
            if (score > 0) {
                return 1; // Favorable position for white
            } else if (score < 0) {
                return -1; // Unfavorable position for white
            } else {
                return 0; // Neutral position
            }
        } else {
            if (score < 0) {
                return 1; // Favorable position for black
            } else if (score > 0) {
                return -1; // Unfavorable position for black
            } else {
                return 0; // Neutral position
            }
        }
    }

        /**
     * Sets the list of possible turns for the current game state.
     * @param possibleTurns The list of possible turns.
     */
    public void setPossibleTurns(List<Turn> possibleTurns) {
        this.possibleTurns = possibleTurns;
    }

    /**
     * Retrieves the list of possible turns for the current game state.
     * @return The list of possible turns.
     */
    public List<Turn> getPossibleTurns() {
        return possibleTurns;
    }

    /**
     * Retrieves all the pieces currently on the board.
     * Combines white and black pieces into a single list.
     * @return A list of all pieces on the board.
     */
    public List<Piece> getAllPieces() {
        List<Piece> pieces = new ArrayList<>();
        pieces.addAll(whitePieces);
        pieces.addAll(blackPieces);
        return pieces;
    }

    /**
     * Executes a move on the board and updates the game state accordingly.
     * Handles normal moves and captures, as well as turn switching.
     * @param move The move to execute.
     * @return True if the move was successfully executed.
     */
    public boolean move(Move move) {
        currentTurn.addMove(move);
        Piece piece = board[move.getFromX()][move.getFromY()].getPiece();
        piece.movePiece(move);
        board[move.getToX()][move.getToY()].setPiece(piece);
        board[move.getFromX()][move.getFromY()].setPiece(null);
        if (move.isCapture()) {
            Capture capture = (Capture) move;
            Piece capturedPiece = board[capture.getCaptureAtX()][capture.getCaptureAtY()].getPiece();
            board[capturedPiece.getX()][capturedPiece.getY()].setPiece(null);
            removeCapturedPieceFromLists(capturedPiece);
        }
        if (move.isTurnEnding()) {
            turnsPlayed.add(currentTurn);
            currentTurn = new Turn();
            // possibleTurns = GameLogic.getLegalTurns(this);
            switchTurn();
        }
        return true;
    }

    /**
     * Undoes a previously executed move on the board and restores the game state.
     * Handles normal moves and captures, as well as turn switching.
     * @param move The move to undo.
     * @return True if the move was successfully undone.
     */
    public boolean undoMove(Move move) {
        Piece piece = board[move.getToX()][move.getToY()].getPiece();
        if (piece == null) {
            throw new IllegalStateException("Piece to undo does not exist at the target position.");
        }
        piece.undoMove(move);
        board[move.getFromX()][move.getFromY()].setPiece(piece);
        board[move.getToX()][move.getToY()].setPiece(null);
        if (move.isCapture()) {
            Capture capture = (Capture) move;
            Piece capturedPiece = new Piece(capture.getCapturedPiece().getType(), capture.getCaptureAtX(), capture.getCaptureAtY(), this);
            board[capturedPiece.getX()][capturedPiece.getY()].setPiece(capturedPiece);
            addCapturedPieceToLists(capturedPiece);
        }
        if (move.isTurnEnding() && !turnsPlayed.isEmpty()) {
            currentTurn = turnsPlayed.remove(turnsPlayed.size() - 1);
            List<Move> moves = currentTurn.getMoves();
            moves.remove(moves.size() - 1);
            switchTurn();
        }
        return true;
    }

    /**
     * Removes a captured piece from the respective color list.
     * @param piece The captured piece to remove.
     */
    private void removeCapturedPieceFromLists(Piece piece) {
        if (piece.type.color.equals("white")) {
            whitePieces.remove(piece);
        } else {
            blackPieces.remove(piece);
        }
    }

    /**
     * Adds a captured piece back to the respective color list.
     * @param piece The captured piece to add.
     */
    private void addCapturedPieceToLists(Piece piece) {
        if (piece.type.color.equals("white")) {
            whitePieces.add(piece);
        } else {
            blackPieces.add(piece);
        }
    }

    /**
     * Calculates the advantage of the white player based on the current board state.
     * Factors include the number of pieces, number of kings, and their positions.
     * @return A score representing White's advantage.
     */
    public double getWhiteAdvantage() {
        double advantage = 0;

        for (Piece piece : whitePieces) {
            advantage += 1; // Standard value
            if (piece.getType() == PieceType.WHITEKING) {
                advantage += 2; // Additional value for a king
            }

            advantage += (board.length - 1 - piece.getY()) * 0.1;
        }

        return advantage;
    }

    /**
     * Calculates the advantage of the black player based on the current board state.
     * Factors include the number of pieces, number of kings, and their positions.
     * @return A score representing Black's advantage.
     */
    public double getBlackAdvantage() {
        double advantage = 0;

        for (Piece piece : blackPieces) {
            advantage += 1; // Standard value
            if (piece.getType() == PieceType.BLACKKING) {
                advantage += 2; // Additional value for a king
            }

            advantage += piece.getY() * 0.1;
        }

        return advantage;
    }

    /**
     * Retrieves the piece located at the specified board coordinates.
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @return The piece at the specified coordinates, or null if no piece exists.
     */
    public Piece getPieceAt(int x, int y) {
        if (x >= 0 && x < board.length && y >= 0 && y < board[0].length) {
            return board[x][y].getPiece();
        }
        return null;
    }

    /**
     * Checks if there is a piece located at the specified board coordinates.
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @return True if a piece exists at the specified coordinates, otherwise false.
     */
    public boolean hasPieceAt(int x, int y) {
        if (x >= 0 && x < board.length && y >= 0 && y < board[0].length) {
            return board[x][y].hasPiece();
        }
        return false;
    }

    /**
     * Determines whether it is currently White's turn.
     * @return True if it is White's turn, otherwise false.
     */
    public boolean getIsWhiteTurn() {
        return isWhiteTurn;
    }

    /**
     * Determines the type of move based on the starting and target positions.
     * Handles normal moves, captures, and invalid moves.
     * @param x0 The starting x-coordinate.
     * @param y0 The starting y-coordinate.
     * @param newX The target x-coordinate.
     * @param newY The target y-coordinate.
     * @return A Move object representing the determined move type.
     */
    public Move determineMoveType(int x0, int y0, int newX, int newY) {
        // Check for out-of-bounds coordinates
        if (!isValidPosition(x0, y0) || !isValidPosition(newX, newY)) {
            System.out.println("Invalid board coordinates: (" + x0 + ", " + y0 + ") to (" + newX + ", " + newY + ")");
            return new InvalidMove(x0, y0, null, newX, newY);
        }

        Tile startTile = board[x0][y0];
        Tile targetTile = board[newX][newY];

        // Check if start tile or piece is null
        if (startTile == null || !startTile.hasPiece()) {
            System.out.println("No piece at starting position: (" + x0 + ", " + y0 + ")");
            return new InvalidMove(x0, y0, null, newX, newY);
        }

        Piece piece = startTile.getPiece();

        // Check if it's the correct player's turn
        if (isWhiteTurn != piece.getType().color.equals("white")) {
            return new InvalidMove(x0, y0, piece, newX, newY);
        }

        // Check if the target tile is empty
        if (targetTile == null || targetTile.hasPiece()) {
            return new InvalidMove(x0, y0, piece, newX, newY);
        }

        // Check if the target tile is black
        if (!targetTile.isBlack()) {
            return new InvalidMove(x0, y0, piece, newX, newY);
        }

        // King logic
        if (piece.getType() == PieceType.BLACKKING || piece.getType() == PieceType.WHITEKING) {
            if (isMoveforKing(x0, y0, newX, newY) && isPathClearforKing(x0, y0, newX, newY)) {
                return new NormalMove(x0, y0, piece, newX, newY);
            }
            if (isCapturePathforKing(x0, y0, newX, newY)) {
                Piece capturedPiece = getCapturedPieceOnPathforKing(x0, y0, newX, newY);
                return new Capture(x0, y0, piece, capturedPiece, newX, newY);
            }
        } else {
            // Normal piece logic
            if (isMoveDiagonalNormal(x0, y0, newX, newY) && piece.getType().moveDir == (newY - y0)) {
                return new NormalMove(x0, y0, piece, newX, newY);
            }
            if (newY == y0 && Math.abs(newX - x0) == 4) {
                return checkHorizontalCapture(x0, y0, newX, newY, piece);
            }
            if (newX == x0 && Math.abs(newY - y0) == 4) {
                return checkVerticalCapture(x0, y0, newX, newY, piece);
            }
            if (Math.abs(newX - x0) == 2 && Math.abs(newY - y0) == 2) {
                return checkDiagonalCapture(x0, y0, newX, newY, piece);
            }
        }

        return new InvalidMove(x0, y0, piece, newX, newY);
    }

    /**
     * Checks if a horizontal capture move is valid.
     * Determines if there is an opponent's piece between the starting and target positions.
     * @param x0 The starting x-coordinate of the piece.
     * @param y0 The starting y-coordinate of the piece.
     * @param newX The target x-coordinate.
     * @param newY The target y-coordinate.
     * @param piece The piece attempting the capture.
     * @return A valid Capture move if the opponent's piece is found; otherwise, an InvalidMove.
     */
    private Move checkHorizontalCapture(int x0, int y0, int newX, int newY, Piece piece) {
        int x1 = (newX + x0) / 2; // Middle x-coordinate
        Tile halfWay = board[x1][y0]; // Tile halfway between the start and target
        if (halfWay != null && halfWay.hasPiece()) {
            Piece capturedPiece = halfWay.getPiece();
            if (!capturedPiece.getType().color.equals(piece.getType().color)) {
                return new Capture(x0, y0, piece, capturedPiece, newX, newY);
            }
        }
        return new InvalidMove(x0, y0, piece, newX, newY);
    }

    /**
     * Checks if a vertical capture move is valid.
     * Determines if there is an opponent's piece between the starting and target positions.
     * @param x0 The starting x-coordinate of the piece.
     * @param y0 The starting y-coordinate of the piece.
     * @param newX The target x-coordinate.
     * @param newY The target y-coordinate.
     * @param piece The piece attempting the capture.
     * @return A valid Capture move if the opponent's piece is found; otherwise, an InvalidMove.
     */
    private Move checkVerticalCapture(int x0, int y0, int newX, int newY, Piece piece) {
        int y1 = (newY + y0) / 2; // Middle y-coordinate
        Tile halfWay = board[x0][y1]; // Tile halfway between the start and target
        if (halfWay != null && halfWay.hasPiece()) {
            Piece capturedPiece = halfWay.getPiece();
            if (!capturedPiece.getType().color.equals(piece.getType().color)) {
                return new Capture(x0, y0, piece, capturedPiece, newX, newY);
            }
        }
        return new InvalidMove(x0, y0, piece, newX, newY);
    }

    /**
     * Checks if a diagonal capture move is valid.
     * Determines if there is an opponent's piece between the starting and target positions.
     * @param x0 The starting x-coordinate of the piece.
     * @param y0 The starting y-coordinate of the piece.
     * @param newX The target x-coordinate.
     * @param newY The target y-coordinate.
     * @param piece The piece attempting the capture.
     * @return A valid Capture move if the opponent's piece is found; otherwise, an InvalidMove.
     */
    private Move checkDiagonalCapture(int x0, int y0, int newX, int newY, Piece piece) {
        int x1 = (newX + x0) / 2; // Middle x-coordinate
        int y1 = (newY + y0) / 2; // Middle y-coordinate
        Tile halfWay = board[x1][y1]; // Tile halfway between the start and target
        if (halfWay != null && halfWay.hasPiece()) {
            Piece capturedPiece = halfWay.getPiece();
            if (!capturedPiece.getType().color.equals(piece.getType().color)) {
                return new Capture(x0, y0, piece, capturedPiece, newX, newY);
            }
        }
        return new InvalidMove(x0, y0, piece, newX, newY);
    }

    /**
     * Validates if a position is within the bounds of the board.
     * @param x The x-coordinate of the position.
     * @param y The y-coordinate of the position.
     * @return True if the position is within the board's bounds, otherwise false.
     */
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < board.length && y >= 0 && y < board[0].length;
    }

    /**
     * Checks if a move is valid for a king piece.
     * A valid move for a king is diagonal, where the absolute differences in x and y coordinates are equal.
     * @param x0 The starting x-coordinate.
     * @param y0 The starting y-coordinate.
     * @param newX The target x-coordinate.
     * @param newY The target y-coordinate.
     * @return True if the move is valid for a king, otherwise false.
     */
    private boolean isMoveforKing(int x0, int y0, int newX, int newY) {
        return (Math.abs(newX - x0) == Math.abs(newY - y0)); // Adjusted for only normal diagonal moves
    }

    /**
     * Checks if a move is a diagonal move for a normal piece.
     * @param x0 The starting x-coordinate.
     * @param y0 The starting y-coordinate.
     * @param newX The target x-coordinate.
     * @param newY The target y-coordinate.
     * @return True if the move is diagonal for a normal piece, otherwise false.
     */
    private boolean isMoveDiagonalNormal(int x0, int y0, int newX, int newY) {
        return Math.abs(newX - x0) == 1 && Math.abs(newY - y0) == 1;
    }

    /**
     * Checks if a move is a potential capture move for a king.
     * Valid capture moves for kings include diagonal, horizontal, or vertical movements.
     * @param x0 The starting x-coordinate.
     * @param y0 The starting y-coordinate.
     * @param newX The target x-coordinate.
     * @param newY The target y-coordinate.
     * @return True if the move is a valid capture move for a king, otherwise false.
     */
    private boolean isMoveACaptureForKing(int x0, int y0, int newX, int newY) {
        return (x0 == newX || y0 == newY || Math.abs(newX - x0) == Math.abs(newY - y0)); // Adjusted for only normal diagonal moves
    }

    /**
     * Checks if the path for a king's movement is clear (diagonal, horizontal, or vertical).
     * Ensures there are no pieces blocking the path.
     * @param x0 The starting x-coordinate.
     * @param y0 The starting y-coordinate.
     * @param newX The target x-coordinate.
     * @param newY The target y-coordinate.
     * @return True if the path is clear, otherwise false.
     */
    private boolean isPathClearforKing(int x0, int y0, int newX, int newY) {
        int dx = Integer.compare(newX, x0); // Direction along x-axis
        int dy = Integer.compare(newY, y0); // Direction along y-axis

        int x = x0 + dx;
        int y = y0 + dy;

        while (x != newX || y != newY) {
            if (board[x][y].hasPiece()) {
                return false;  // Path is blocked
            }
            x += dx;
            y += dy;
        }
        return true;
    }

    /**
     * Checks if there is a capturable piece on the path of a king's movement.
     * Ensures the path has exactly one opponent's piece to capture.
     * @param x0 The starting x-coordinate.
     * @param y0 The starting y-coordinate.
     * @param newX The target x-coordinate.
     * @param newY The target y-coordinate.
     * @return True if a capturable piece is found on the path, otherwise false.
     */
    private boolean isCapturePathforKing(int x0, int y0, int newX, int newY) {
        if (!isMoveACaptureForKing(x0, y0, newX, newY)) {
            return false;  // Not a capture move for the king
        }

        int dx = Integer.compare(newX, x0); // Direction of movement along x-axis
        int dy = Integer.compare(newY, y0); // Direction of movement along y-axis

        int x = x0 + dx;
        int y = y0 + dy;
        Piece capturedPiece = null;
        int capturedX = -1;
        int capturedY = -1;

        while (x != newX || y != newY) {
            if (board[x][y].hasPiece()) {
                if (capturedPiece == null) {
                    // Check if it's an opponent's piece
                    if (!board[x][y].getPiece().getType().color.equals(board[x0][y0].getPiece().getType().color)) {
                        capturedPiece = board[x][y].getPiece();  // Found an opponent's piece to capture
                        capturedX = x;
                        capturedY = y;
                    } else {
                        return false;  // Path is blocked by a friendly piece
                    }
                } else {
                    return false;  // More than one piece on the path
                }
            }
            x += dx;
            y += dy;
        }

        // Ensure the capturing piece can only land on a tile behind the captured one
        if (capturedPiece != null) {
            int landingX = capturedX + dx;
            int landingY = capturedY + dy;

            if (Math.abs(newX - landingX) <= board.length && Math.abs(newY - landingY) <= board.length) {
                return true;  // Valid capture
            } else {
                return false; // Invalid landing
            }
        }

        return false;  // No capturable piece was found
    }

    /**
     * Returns the piece to be captured along the path for a king's movement.
     * Validates the path to ensure there is only one opponent's piece to capture.
     * @param x0 The starting x-coordinate of the king.
     * @param y0 The starting y-coordinate of the king.
     * @param newX The target x-coordinate.
     * @param newY The target y-coordinate.
     * @return The captured piece if the move is valid; otherwise, null.
     */
    private Piece getCapturedPieceOnPathforKing(int x0, int y0, int newX, int newY) {
        if (!isMoveACaptureForKing(x0, y0, newX, newY)) {
            return null;  // Not a capture-move for the king
        }
        int dx = Integer.compare(newX, x0); // Direction of movement along x-axis
        int dy = Integer.compare(newY, y0); // Direction of movement along y-axis

        int x = x0 + dx;
        int y = y0 + dy;
        Piece capturedPiece = null;
        int capturedX = -1;
        int capturedY = -1;

        while (x != newX || y != newY) {
            if (board[x][y].hasPiece()) {
                if (capturedPiece == null) {
                    // Check if the piece belongs to the opponent
                    if (!board[x][y].getPiece().getType().color.equals(board[x0][y0].getPiece().getType().color)) {
                        capturedPiece = board[x][y].getPiece();  // Found an opponent's piece to capture
                        capturedX = x;
                        capturedY = y;
                    } else {
                        return null;  // Path is blocked by a friendly piece
                    }
                } else {
                    return null;  // More than one piece on the path
                }
            }
            x += dx;
            y += dy;
        }

        // Ensure the king lands on a valid tile behind the captured piece
        if (capturedPiece != null) {
            int landingX = capturedX + dx;
            int landingY = capturedY + dy;

            if (Math.abs(newX - landingX) <= board.length && Math.abs(newY - landingY) <= board.length) {
                return capturedPiece;  // Valid capture
            } else {
                return null;  // Invalid landing
            }
        }

        return null; // No valid capture is found
    }

    /**
     * Returns a string representation of the current game state.
     * Black pieces are represented by 'b', white pieces by 'w',
     * black kings by 'B', and white kings by 'W'. Empty tiles are represented by '0'.
     * @return A string representation of the board.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (!board[x][y].hasPiece()) {
                    sb.append("0 "); // Empty tile
                } else {
                    Piece piece = board[x][y].getPiece();
                    if (piece.getType() == PieceType.BLACK) {
                        sb.append("b "); // Black piece
                    } else if (piece.getType() == PieceType.WHITE) {
                        sb.append("w "); // White piece
                    } else if (piece.getType() == PieceType.BLACKKING) {
                        sb.append("B "); // Black king
                    } else if (piece.getType() == PieceType.WHITEKING) {
                        sb.append("W "); // White king
                    }
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Compares the current game state with another game state for equality.
     * Checks if both boards have the same pieces in the same positions.
     * @param b The other game state to compare with.
     * @return True if both game states are identical; otherwise, false.
     */
    public boolean equals(GameState b) {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (board[x][y].hasPiece() != b.getBoard()[x][y].hasPiece()) {
                    return false; // Different piece presence
                }
                if (board[x][y].hasPiece() && b.getBoard()[x][y].hasPiece()) {
                    if (!board[x][y].getPiece().equals(b.getBoard()[x][y].getPiece())) {
                        return false; // Different piece details
                    }
                }
            }
        }
        return true; // Boards are identical
    }

    /**
     * Retrieves the piece located at the specified coordinates on the board.
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @return The piece located at the specified coordinates, or null if no piece exists.
     */
    public Piece getPiece(int x, int y) {
        return board[x][y].getPiece();
    }

    /**
     * Retrieves the current turn being played.
     * @return The current turn.
     */
    public Turn getCurrentTurn() {
        return currentTurn;
    }

    /**
     * Determines if the game is a draw.
     * A draw occurs if the number of turns played reaches or exceeds 100.
     * @return True if the game is a draw, otherwise false.
     */
    public boolean isDraw() {
        return turnsPlayed.size() >= 150;
    }

    /**
     * Determines the winner of the game.
     * @return 1 if White wins, -1 if Black wins, 100 if the game is a draw, 0 if the game is not over.
     */
    public int getWinner() {
        if (winner != 0) {
            return winner;
        }
        if (isDraw()) {
            winner = 100; // Game is a draw
            return winner;
        }
        if (blackPieces.isEmpty()) {
            winner = 1; // White wins
            return winner;
        }
        if (whitePieces.isEmpty()) {
            winner = -1; // Black wins
            return winner;
        }
        return 0; // Game is not over
    }

    // Old GameLogic Methods

    /**
     * Retrieves all legal turns for the current player.
     * If no legal moves are available, the game is set to over.
     * @return A list of legal turns for the current player.
     */
    public List<Turn> getLegalTurns() {
        ArrayList<Move> availableMoves = getPossibleMoves();
        if (availableMoves.isEmpty()) {
            setGameOver(true, isWhiteTurn ? -1 : 1); // Set game over if no moves are available
            return new ArrayList<>();
        }
        if (availableMoves.get(0).isNormal()) {
            return Turn.copyMovesToTurns(availableMoves); // Convert moves to turns if no captures are available
        }
        ArrayList<Turn> availableTurns = new ArrayList<>();
        Set<Piece> pieces = Piece.movesToPieces(availableMoves);
        int maxCaptures = 0;
        for (Piece piece : pieces) {
            ArrayList<Turn> pieceTurns = getLegalTurns(piece);
            for (Turn turn : pieceTurns) {
                // TODO: Add 2 kings rule
                if (turn.getMoves().size() > maxCaptures) {
                    availableTurns.clear();
                    availableTurns.add(turn); // Add turns with the highest number of captures
                    maxCaptures = turn.getMoves().size();
                } else if (turn.getMoves().size() == maxCaptures) {
                    availableTurns.add(turn); // Add turns with equal maximum captures
                }
            }
        }
        return availableTurns;
    }

    /**
     * Retrieves all legal turns for a specific piece.
     * Uses depth-first search (DFS) to explore potential capture sequences.
     * @param piece The piece for which legal turns are being determined.
     * @return A list of legal turns for the specified piece.
    */
    public ArrayList<Turn> getLegalTurns(Piece piece) {
        // Create a copy of the current game state
        GameState g = new GameState(this);

        // Get all possible moves for the given piece
        ArrayList<Move> availableMoves = g.getPossibleMoves(piece);

        // Handle cases where the list of available moves is null or empty
        if (availableMoves == null || availableMoves.isEmpty()) {
            // Return an empty list if no moves are available
            return new ArrayList<>();
        }

        // Check if the first move is a normal move (not a capture)
        if (availableMoves.get(0).isNormal()) {
            // Convert normal moves to turns and return
            return Turn.copyMovesToTurns(availableMoves);
        }

        // Reset DFS-related variables to ensure a clean state
        DepthFirstSearch.resetMaxCaptures(); // Clear any previous max capture data
        DepthFirstSearch.resetResult(); // Reset the DFS result storage

        // Initialize the starting point for the DFS
        Turn initialTurn = new Turn();

        // Perform depth-first search to find all capture sequences
        DepthFirstSearch.dfs(g, piece, initialTurn, 0);

        // Retrieve the results of the DFS
        ArrayList<Turn> result = DepthFirstSearch.getResult();

        // Ensure the result is not null; return an empty list if no turns are found
        return result != null ? result : new ArrayList<>();
    }

    /**
     * Retrieves all capture moves available for the current player's pieces.
     * @return A list of all available capture moves.
     */
    public ArrayList<Move> getCaptures() {
        ArrayList<Move> availableCaptures = new ArrayList<>();
        ArrayList<Piece> pieces = getListOfPieces();
        for (Piece piece : pieces) {
            availableCaptures.addAll(getCaptures(piece));
        }
        return availableCaptures;
    }

    /**
     * Retrieves all capture moves available for a specific piece.
     * @param piece The piece for which capture moves are being determined.
     * @return A list of all available capture moves for the specified piece.
     */
    public ArrayList<Move> getCaptures(Piece piece) {
        ArrayList<Move> availableCaptures = new ArrayList<>();
        // TODO: Instead of iterating over all black tiles, iterate over all tiles where the piece can move.
        for (int row = 0; row < SIZE; row++) {
            int startCol = (row % 2 == 0) ? 1 : 0;
            for (int col = startCol; col < SIZE; col += 2) {
                Move move = determineMoveType(piece.getX(), piece.getY(), row, col);
                if (move.isCapture()) {
                    availableCaptures.add(move);
                }
            }
        }
        return availableCaptures;
    }

    /**
     * Retrieves all possible moves for the current player's pieces.
     * Prioritizes capture moves if available.
     * @return A list of all possible moves for the current player.
     */
    private ArrayList<Move> getPossibleMoves() {
        ArrayList<Move> availableMoves = new ArrayList<>();
        ArrayList<Move> availableCaptures = new ArrayList<>();
        ArrayList<Move> currentMoves = new ArrayList<>();
        ArrayList<Piece> pieces = getListOfPieces();
        for (Piece piece : pieces) {
            // TODO: Instead of iterating over all black tiles, iterate over all tiles where the piece can move.
            currentMoves = getPossibleMoves(piece);
            if (currentMoves.isEmpty()) {
                continue;
            } else if (currentMoves.get(0).isCapture()) {
                availableCaptures.addAll(currentMoves);
            } else {
                availableMoves.addAll(currentMoves);
            }
        }
        if (!availableCaptures.isEmpty()) {
            return availableCaptures; // Return captures if available
        }
        return availableMoves; // Return normal moves otherwise
    }

    /**
     * Retrieves all possible moves for a specific piece.
     * Prioritizes capture moves if available.
     * @param piece The piece for which possible moves are being determined.
     * @return A list of all possible moves for the specified piece.
     */
    public ArrayList<Move> getPossibleMoves(Piece piece) {
        ArrayList<Move> availableMoves = new ArrayList<>();
        ArrayList<Move> availableCaptures = new ArrayList<>();
        // TODO: Instead of iterating over all black tiles, iterate over all tiles where the piece can move.
        for (int row = 0; row < SIZE; row++) {
            int startCol = (row % 2 == 0) ? 1 : 0;
            for (int col = startCol; col < SIZE; col += 2) {
                Move move = determineMoveType(piece.getX(), piece.getY(), row, col);
                if (move.isNormal() && availableCaptures.isEmpty()) {
                    availableMoves.add(move); // Add normal move if no captures are available
                } else if (move.isCapture()) {
                    availableCaptures.add(move); // Add capture move
                }
            }
        }
        if (!availableCaptures.isEmpty()) {
            return availableCaptures; // Return captures if available
        }
        return availableMoves; // Return normal moves otherwise
    }

    /**
     * Determines all pieces that can be captured by the given player's pieces.
     * @param pieces The player's pieces to evaluate.
     * @return A set of pieces that are threatened (can be captured).
     */
    public Set<Piece> getPiecesTheathenedBy(ArrayList<Piece> pieces) {
        // TODO: Instead of getLegalMoves, iterate over all legal Turns.
        Set<Piece> threatenedPieces = new HashSet<>();
        for (Piece piece : pieces) {
            for (Move move : getPossibleMoves(piece)) {
                if (move.isCapture()) {
                    Capture captureMove = (Capture) move;
                    threatenedPieces.add(captureMove.getCapturedPiece());
                }
            }
        }
        return threatenedPieces;
    }

    /**
     * Retrieves all legal moves for a specific piece.
     * @param piece The piece for which legal moves are being determined.
     * @return A list of legal moves for the specified piece.
     */
    public List<Move> getLegalMoves(Piece piece) {
        List<Turn> availableTurns = getLegalTurns(piece); // Fetch all legal turns for the piece
        List<Move> availableMoves = new ArrayList<>();

        for (Turn turn : availableTurns) {
            availableMoves.add(turn.getMoves().get(0)); // Add the first move of each turn
        }

        return availableMoves;
    }

    /**
     * Retrieves all legal moves for the current player.
     * @return A list of legal moves for the current player.
     */
    public List<Move> getLegalMoves() {
        List<Turn> availableTurns = getLegalTurns(); // Fetch all legal turns for the current player
        List<Move> availableMoves = new ArrayList<>();
        for (Turn turn : availableTurns) {
            availableMoves.add(turn.getMoves().get(0)); // Add the first move of each turn
        }
        return availableMoves;
    }

    /**
     * Retrieves a list of all pieces for the current player.
     * @return A list of all pieces for the current player.
     */
    public ArrayList<Piece> getListOfPieces() {
        if (isWhiteTurn) {
            return getWhitePieces(); // Return white pieces if it's White's turn
        } else {
            return getBlackPieces(); // Return black pieces if it's Black's turn
        }
    }

    /**
     * Evaluates the board using weighted features to determine favorability for White or Black.
     * 
     * The evaluation function is based on the following features:
     * - x1: Number of black pieces on the board.
     * - x2: Number of white pieces on the board.
     * - x3: Number of black kings on the board.
     * - x4: Number of white kings on the board.
     * - x5: Number of black pieces threatened by white (i.e., can be captured on White's next turn).
     * - x6: Number of white pieces threatened by black.
     * 
     * Weights for the features:
     * - w1 = -1 (negative weight for black pieces)
     * - w2 = 1 (positive weight for white pieces)
     * - w3 = -3 (heavier negative weight for black kings)
     * - w4 = 3 (heavier positive weight for white kings)
     * - w5 = 1 (positive weight for black pieces threatened by White)
     * - w6 = -1 (negative weight for white pieces threatened by Black)
     * 
     * @return A positive score for White's advantage, negative for Black's advantage.
     */
    public double evaluateBoard() {
        int[] x = boardParameters();
        // evaluation positive for white, negative for black
        double w0 = 1, w1 = -1, w2 = 3, w3 = -3, w4 = 1, w5 = -1;
        // Calculate and return the weighted evaluation
        return w0*x[0] + w1*x[1] + w2*x[2] + w3*x[3] + w4*x[4] + w5*x[5];
    }

    public double evaluateBoard(double[] weights) {
        int[] x = boardParameters();
        return weights[0]*x[0] - weights[0]*x[1] + weights[1]*x[2] - weights[1]*x[3] + weights[2]*x[4] + weights[2]*x[5];
    }

    // end of old GameLogic methods

    public int[] boardParameters() {
        int[] parameters = new int[6];
        
        for (Piece piece : getWhitePieces()) {
            if (piece.type == PieceType.WHITE) {
                parameters[0]++;
            } else {
                parameters[2]++;
            }
        }

        for (Piece piece : getBlackPieces()) {
            if (piece.type == PieceType.BLACK) {
                parameters[1]++;
            } else {
                parameters[3]++;
            }
        }

        parameters[4] = getPiecesTheathenedBy(getWhitePieces()).size();
        parameters[5] = getPiecesTheathenedBy(getBlackPieces()).size();
        return parameters;
    }

    public void addWhiteKing(Piece piece) {
        whiteKings.add(piece);
    }

    public void addBlackKing(Piece piece) {
        blackKings.add(piece);
    }

    public void removeWhiteKing(Piece piece) {
        whiteKings.remove(piece);
    }

    public void removeBlackKing(Piece piece) {
        blackKings.remove(piece);
    }
}